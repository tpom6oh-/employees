package com.tpom6oh.employees.data;

import android.content.Intent;
import android.database.Cursor;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tpom6oh.employees.json.CountryInfo;
import com.tpom6oh.employees.model.EmployeeInfo;
import com.tpom6oh.employees.model.EmployeesProvider;
import com.tpom6oh.employees.model.employee.EmployeeColumns;
import com.tpom6oh.employees.model.employee.EmployeeContentValues;
import com.tpom6oh.employees.util.TestEmployeesDataLoaderService;

import java.util.Date;

public class EmployeesDataLoaderServiceTest extends ProviderTestCase2<EmployeesProvider> {

    private MockContentResolver contentResolver;
    private TestEmployeesDataLoaderService service;

    public EmployeesDataLoaderServiceTest() {
        super(EmployeesProvider.class, EmployeeColumns.CONTENT_URI.getAuthority());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        contentResolver = getMockContentResolver();
        service = new TestEmployeesDataLoaderService(this);
        contentResolver.delete(EmployeeColumns.CONTENT_URI, null, null);
    }


    public void testCountryInfoReceive() {
        insertRowToContentProvider();
        String country_test = "country_test";

        CountryInfo country = new CountryInfo();
        country.setCountryName(country_test);
        country.setCountryId(1);
        service.onCountryInfoReceive(country);

        Cursor cursor = contentResolver.query(EmployeeColumns.CONTENT_URI, null,
                                              EmployeeColumns.COUNTRY_NAME + " = ?",
                                              new String[]{country_test}, null);

        assertNotNull(cursor);
        assertTrue("Country was not added to the database", cursor.moveToFirst());
        assertEquals("Country was added to the database more than one time", cursor.getCount(), 1);
    }

    public void testEmployeeInfoReceive() {
        EmployeeInfo employeeInfo = getEmployeeInfoStub();

        service.onEmployeeInfoReceive(employeeInfo);

        Cursor cursor = contentResolver.query(EmployeeColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertFalse("Employee was added straight to the db instead of a buffer",
                    cursor.moveToFirst());

        for (int i = 0; i < 498; i++) {
            service.onEmployeeInfoReceive(employeeInfo);
        }
        cursor = contentResolver.query(EmployeeColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertFalse("Buffer size is less than 500", cursor.moveToFirst());

        service.onEmployeeInfoReceive(employeeInfo);
        cursor = contentResolver.query(EmployeeColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertTrue("Buffer with 500 elements was not written to the database",
                   cursor.moveToFirst());
        assertEquals("Not all employees written to database", cursor.getCount(), 500);
    }

    public void testOnHandleIntent() {
        service.onHandleIntent(new Intent());
        assertFalse("Assets JSON ignored", service.isParsingFromWeb());
        assertTrue("Assets JSON parse was not called", service.wasParseInputStreamCalled());

        insertRowToContentProvider();

        service.onHandleIntent(new Intent());
        assertTrue("Web JSON ignored", service.isParsingFromWeb());
        assertTrue("Web JSON parse was not called", service.wasParseInputStreamCalled());
    }

    public void testOnAllEmployeesDataParsed() {
        EmployeeInfo employeeInfo = getEmployeeInfoStub();
        for (int i = 0; i < 5; i++) {
            service.onEmployeeInfoReceive(employeeInfo);
        }

        Cursor cursor = contentResolver.query(EmployeeColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertFalse("Employee was added straight to the db instead of a buffer",
                    cursor.moveToFirst());

        service.onAllEmployeesDataParsed();
        cursor = contentResolver.query(EmployeeColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertTrue("Buffer not written to database on all employees parsed",
                   cursor.moveToFirst());
        assertEquals("Not all employees written to database", cursor.getCount(), 5);
    }

    public void testOnParseDataComplete() {
        service.onHandleIntent(new Intent());
        assertFalse("Assets JSON ignored", service.isParsingFromWeb());

        service.onParseDataComplete();
        assertTrue("Service was not restarted to load data from the web",
                   service.wasStartServiceCalled());

        insertRowToContentProvider();

        service.onHandleIntent(new Intent());
        assertTrue("Web JSON ignored", service.isParsingFromWeb());

        service.onParseDataComplete();
        assertTrue("New etag value was not saved", service.wasNewETagSavedToPreferences());
    }

    private void insertRowToContentProvider() {
        EmployeeContentValues contentValues = new EmployeeContentValues()
                .putCountryId(1)
                .putName("")
                .putCompany("")
                .putCountryNameNull()
                .putTeam("")
                .putDivision("")
                .putEmploymentDate(0)
                .putEmploymentYear(0)
                .putSalary(0)
                .putEnterprise("")
                .putImageUrl("");
        getMockContentResolver().insert(EmployeeColumns.CONTENT_URI, contentValues.values());
    }

    private EmployeeInfo getEmployeeInfoStub() {
        return new EmployeeInfo("", "", "", 0, new Date(), "", 0, "", "");
    }
}
