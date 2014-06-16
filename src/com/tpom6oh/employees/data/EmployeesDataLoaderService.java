package com.tpom6oh.employees.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.tpom6oh.employees.model.EmployeeInfo;
import com.tpom6oh.employees.model.EmployeesProvider;
import com.tpom6oh.employees.model.employee.EmployeeColumns;
import com.tpom6oh.employees.model.employee.EmployeeContentValues;
import com.tpom6oh.employees.json.CountryInfo;
import com.tpom6oh.employees.json.EmployeesJsonParser;
import com.tpom6oh.employees.json.IParseListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EmployeesDataLoaderService extends IntentService implements IParseListener {

    public static final String BASE_ORGANIZATION_JSON_FILE_NAME = "base.organization.json";
    public static final String URL = "https://s3.amazonaws.com/quizup-mirror/organization.json";
    public static final String ETAG_VALUE_LABEL = "ETag value";
    private static final String TAG = "EmployeesDataLoaderService.class";
    private String currentETag;
    private boolean parsingFromWeb;

    private ArrayList<EmployeeInfo> employeesBuffer = new ArrayList<EmployeeInfo>(500);

    public EmployeesDataLoaderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Cursor cursor = getContentResolver().query(EmployeeColumns.CONTENT_URI, null, null, null,
                                           null);
        InputStream in = null;
        if (cursor == null || !cursor.moveToFirst()) {
            in = getDefaultJsonStream(this);
            parsingFromWeb = false;
        } else if (eTagChanged()) {
            in = getWebJsonStream();
            parsingFromWeb = true;
        }
        if (in != null) {
            parseInputStream(in);
        }
    }

    /**
     * @return an input stream from {@link #URL}
     */
    public static InputStream getWebJsonStream() {
        try {
            URL url = new URL(URL);
            return url.openStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get inputStream from " + URL);
        }
        return null;
    }

    private boolean eTagChanged() {
        String previousETag = getETagFromPreferences();
        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(URL).openConnection();
            currentETag = conn.getHeaderField("ETag");
            if (currentETag == null) {
                return true;
            } else {
                return !currentETag.equals(previousETag);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get ETag");
            return true;
        }
    }

    private void saveNewETagToPreferences(String newETag) {
        SharedPreferences preferences = getSharedPreferences(ETAG_VALUE_LABEL, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ETAG_VALUE_LABEL, newETag);
        editor.commit();
    }

    private String getETagFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(ETAG_VALUE_LABEL, MODE_PRIVATE);
        return preferences.getString(ETAG_VALUE_LABEL, "");
    }

    protected void parseInputStream(InputStream in) {
        EmployeesJsonParser parser = new EmployeesJsonParser(this);
        try {
            parser.parseEnterpriseCountryJson(in);
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse " + BASE_ORGANIZATION_JSON_FILE_NAME);
        }
    }

    public boolean isParsingFromWeb() {
        return parsingFromWeb;
    }

    /**
     * @return an input stream from assets: {@link #BASE_ORGANIZATION_JSON_FILE_NAME}
     */
    public static InputStream getDefaultJsonStream(Context context) {
        AssetManager manager = context.getAssets();
        InputStream in = null;
        try {
            in = manager.open(BASE_ORGANIZATION_JSON_FILE_NAME);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get inputStream from " + BASE_ORGANIZATION_JSON_FILE_NAME);
        }
        return in;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAllEmployeesDataParsed() {
        loadEmployeesDataToDb();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onParseDataStart() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onParseDataComplete() {
        getContentResolver().notifyChange(EmployeeColumns.CONTENT_URI, null);
        if (parsingFromWeb) {
            saveNewETagToPreferences(currentETag);
        } else {
            startService(new Intent(this, EmployeesDataLoaderService.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEmployeeInfoReceive(EmployeeInfo employeeInfo) {
        employeesBuffer.add(employeeInfo);
        if (employeesBuffer.size() > 499) {
            loadEmployeesDataToDb();
        }
    }

    private void loadEmployeesDataToDb() {
        ContentValues[] contentValues = new ContentValues[employeesBuffer.size()];
        for (int i = 0; i < employeesBuffer.size(); i++) {
            EmployeeInfo employeeInfo = employeesBuffer.get(i);
            int employmentYear = parseYear(employeeInfo.getEmploymentDate());
            EmployeeContentValues employeeContentValues = new EmployeeContentValues();
            employeeContentValues.putName(employeeInfo.getEmployeeName())
                    .putCompany(employeeInfo.getCompanyName())
                    .putCountryId(employeeInfo.getCountryId())
                    .putCountryNameNull()
                    .putTeam(employeeInfo.getTeamName())
                    .putDivision(employeeInfo.getDivisionName())
                    .putEmploymentDate(employeeInfo.getEmploymentDate())
                    .putEmploymentYear(employmentYear)
                    .putSalary(employeeInfo.getMonthlySalary())
                    .putEnterprise(employeeInfo.getEnterpriseName())
                    .putImageUrl(employeeInfo.getImageUri());
            contentValues[i] = employeeContentValues.values();
        }
        Uri uri = EmployeesProvider.notify(EmployeeColumns.CONTENT_URI, false);
        getContentResolver().bulkInsert(uri, contentValues);
        employeesBuffer.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCountryInfoReceive(CountryInfo country) {
        ContentValues contentValues = new EmployeeContentValues().
            putCountryName(country.getCountryName()).values();
        String where = "country_id = ?";
        String[] args = {String.valueOf(country.getCountryId())};

        Uri uri = EmployeesProvider.notify(EmployeeColumns.CONTENT_URI, false);
        getContentResolver().update(uri, contentValues, where, args);
    }

    private int parseYear(Date employmentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(employmentDate);
        return calendar.get(Calendar.YEAR);
    }
}
