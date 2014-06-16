package com.tpom6oh.employees.util;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.test.ProviderTestCase2;

import com.tpom6oh.employees.data.EmployeesDataLoaderService;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public class TestEmployeesDataLoaderService extends EmployeesDataLoaderService {
    private ProviderTestCase2 providerTestCase2;

    private boolean parseInputStreamCalled;
    private boolean startServiceCalled;
    private boolean newETagSavedToPreferences;

    public TestEmployeesDataLoaderService(ProviderTestCase2 providerTestCase2) {
        this.providerTestCase2 = providerTestCase2;
    }

    @Override
    public AssetManager getAssets() {
        return providerTestCase2.getContext().getAssets();
    }

    @Override
    public ContentResolver getContentResolver() {
        return providerTestCase2.getMockContentResolver();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return new SharedPreferences() {
            @Override
            public Map<String, ?> getAll() {
                return null;
            }

            @Override
            public String getString(String s, String s2) {
                return "";
            }

            @Override
            public Set<String> getStringSet(String s, Set<String> strings) {
                return null;
            }

            @Override
            public int getInt(String s, int i) {
                return 0;
            }

            @Override
            public long getLong(String s, long l) {
                return 0;
            }

            @Override
            public float getFloat(String s, float v) {
                return 0;
            }

            @Override
            public boolean getBoolean(String s, boolean b) {
                return false;
            }

            @Override
            public boolean contains(String s) {
                return false;
            }

            @Override
            public Editor edit() {
                return new Editor() {
                    @Override
                    public Editor putString(String s, String s2) {
                        if (s.equals(ETAG_VALUE_LABEL)) {
                            newETagSavedToPreferences = true;
                        }
                        return null;
                    }

                    @Override
                    public Editor putStringSet(String s, Set<String> strings) {
                        return null;
                    }

                    @Override
                    public Editor putInt(String s, int i) {
                        return null;
                    }

                    @Override
                    public Editor putLong(String s, long l) {
                        return null;
                    }

                    @Override
                    public Editor putFloat(String s, float v) {
                        return null;
                    }

                    @Override
                    public Editor putBoolean(String s, boolean b) {
                        return null;
                    }

                    @Override
                    public Editor remove(String s) {
                        return null;
                    }

                    @Override
                    public Editor clear() {
                        return null;
                    }

                    @Override
                    public boolean commit() {
                        return false;
                    }

                    @Override
                    public void apply() {

                    }
                };
            }

            @Override
            public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

            }

            @Override
            public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {}
        };
    }

    @Override
    public ComponentName startService(Intent service) {
        startServiceCalled = true;
        return null;
    }

    @Override
    protected void parseInputStream(InputStream in) {
        parseInputStreamCalled = true;
    }

    public boolean wasParseInputStreamCalled() {
        boolean result = parseInputStreamCalled;
        parseInputStreamCalled = false;
        return result;
    }

    public boolean wasNewETagSavedToPreferences() {
        boolean result = newETagSavedToPreferences;
        newETagSavedToPreferences = false;
        return result;
    }

    public boolean wasStartServiceCalled() {
        boolean result = startServiceCalled;
        startServiceCalled = false;
        return result;
    }
}
