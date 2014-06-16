package com.tpom6oh.employees.data;

import android.test.AndroidTestCase;

import com.tpom6oh.employees.data.EmployeesDataLoaderService;

import java.io.InputStream;

public class DataLoadTest extends AndroidTestCase {
    public void testGetWebJsonStream() throws Exception {
        InputStream in = EmployeesDataLoaderService.getWebJsonStream();
        assertNotNull("Failed to get input stream from web JSON", in);
    }

    public void testGetDefaultJsonStream() throws Exception {
        InputStream in = EmployeesDataLoaderService.getDefaultJsonStream(getContext());
        assertNotNull("Failed to get input stream from assets JSON", in);
    }
}
