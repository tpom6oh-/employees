package com.tpom6oh.employees;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;

import com.tpom6oh.employees.model.EmployeeInfo;
import com.tpom6oh.employees.model.employee.EmployeeColumns;
import com.tpom6oh.employees.model.employee.EmployeeContentValues;
import com.tpom6oh.employees.model.json.CountryInfo;
import com.tpom6oh.employees.model.json.EmployeesJsonParser;
import com.tpom6oh.employees.model.json.IParseListener;

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

    private ArrayList<EmployeeInfo> employeesBuffer = new ArrayList<EmployeeInfo>(500);

    public EmployeesDataLoaderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Cursor cursor = getContentResolver().query(EmployeeColumns.CONTENT_URI, null, null, null,
                                           null);
        InputStream in = null;
        if (cursor != null && !cursor.moveToFirst()) {
            in = getDefaultJsonStream(this);
        }
        if (eTagChanged()) {
            in = getWebJsonStream();
        }
        parseInputStream(in);
    }

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
            return !currentETag.equals(previousETag);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get ETag");
            return true;
        }
    }

    private void saveNewETagToPreferences(String newETag) {
        SharedPreferences preferences = getSharedPreferences(ETAG_VALUE_LABEL, MODE_PRIVATE);;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ETAG_VALUE_LABEL, newETag);
        editor.commit();
    }

    private String getETagFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(ETAG_VALUE_LABEL, MODE_PRIVATE);
        return preferences.getString(ETAG_VALUE_LABEL, "");
    }

    private void parseInputStream(InputStream in) {
        EmployeesJsonParser parser = new EmployeesJsonParser(this);
        try {
            parser.parseEnterpriseCountryJson(in);
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse " + BASE_ORGANIZATION_JSON_FILE_NAME);
        }
    }

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

    @Override
    public void onAllEmployeesDataParsed() {
        loadEmployeesDataToDb();
    }

    @Override
    public void onParseDataStart() {

    }

    @Override
    public void onParseDataEnd() {
        saveNewETagToPreferences(currentETag);
    }

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
                    .putDivision(employeeInfo.getDivisionName())
                    .putEmployementDate(employeeInfo.getEmploymentDate())
                    .putEmployementYear(employmentYear)
                    .putSalary(employeeInfo.getMonthlySalary())
                    .putEnterprise(employeeInfo.getEnterpriseName())
                    .putImageUrl(employeeInfo.getImageUri());
            contentValues[i] = employeeContentValues.values();
        }
        getContentResolver().bulkInsert(EmployeeColumns.CONTENT_URI, contentValues);
        employeesBuffer.clear();
    }

    @Override
    public void onCountryInfoReceive(CountryInfo country) {
        ContentValues contentValues = new EmployeeContentValues().
            putCountryName(country.getCountryName()).values();
        String where = "country_id = ?";
        String[] args = {String.valueOf(country.getCountryId())};

        getContentResolver().update(EmployeeColumns.CONTENT_URI, contentValues, where, args);
    }

    private int parseYear(Date employmentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(employmentDate);
        return calendar.get(Calendar.YEAR);
    }
}
