package com.tpom6oh.employees;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;

import com.tpom6oh.employees.model.EmployeeInfo;
import com.tpom6oh.employees.model.employee.EmployeeColumns;
import com.tpom6oh.employees.model.json.CountryInfo;
import com.tpom6oh.employees.model.json.EmployeesJsonParser;
import com.tpom6oh.employees.model.json.IParseListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EmployeesDataLoaderService extends IntentService implements IParseListener {

    public static final String BASE_ORGANIZATION_JSON_FILE_NAME = "base.organization.json";
    public static final String URL = "https://s3.amazonaws.com/quizup-mirror/organization.json";
    public static final String ETAG_VALUE_LABEL = "ETag value";
    private static final String TAG = "EmployeesDataLoaderService.class";
    private String currentETag;

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

    }

    @Override
    public void onParseDataStart() {

    }

    @Override
    public void onAllCountriesDataParsed() {
        saveNewETagToPreferences(currentETag);
    }

    @Override
    public void onEmployeeInfoReceive(EmployeeInfo employeeInfo) {

    }

    @Override
    public void onCountryInfoReceive(CountryInfo country) {

    }
}
