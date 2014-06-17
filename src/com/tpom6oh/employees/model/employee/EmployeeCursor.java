package com.tpom6oh.employees.model.employee;

import android.database.Cursor;

import com.tpom6oh.employees.model.base.AbstractCursor;

import java.util.Date;
import java.util.HashMap;

/**
 * Cursor wrapper for the {@code employee} table.
 */
public class EmployeeCursor extends AbstractCursor {

    public EmployeeCursor(Cursor cursor) {
        super(cursor);
    }

    public EmployeeCursor(Cursor cursor, HashMap<String, Integer> columnIndexes) {
        super(cursor);
        mColumnIndexes = columnIndexes;
    }

    /**
     * Get the {@code name} value.
     * Cannot be {@code null}.
     */
    public String getName() {
        Integer index = getCachedColumnIndexOrThrow(EmployeeColumns.NAME);
        return getString(index);
    }

    /**
     * Get the {@code division} value.
     * Cannot be {@code null}.
     */
    public String getDivision() {
        Integer index = getCachedColumnIndexOrThrow(EmployeeColumns.DIVISION);
        return getString(index);
    }

    /**
     * Get the {@code team} value.
     * Cannot be {@code null}.
     */
    public String getTeam() {
        Integer index = getCachedColumnIndexOrThrow(EmployeeColumns.TEAM);
        return getString(index);
    }

    /**
     * Get the {@code salary} value.
     */
    public int getSalary() {
        return getIntegerOrNull(EmployeeColumns.SALARY);
    }

    /**
     * Get the {@code company} value.
     * Cannot be {@code null}.
     */
    public String getCompany() {
        Integer index = getCachedColumnIndexOrThrow(EmployeeColumns.COMPANY);
        return getString(index);
    }

    /**
     * Get the {@code employment_year} value.
     */
    public int getEmploymentYear() {
        return getIntegerOrNull(EmployeeColumns.EMPLOYMENT_YEAR);
    }

    /**
     * Get the {@code employment_date} value.
     * Cannot be {@code null}.
     */
    public Date getEmploymentDate() {
        return getDate(EmployeeColumns.EMPLOYMENT_DATE);
    }

    /**
     * Get the {@code country_id} value.
     */
    public long getCountryId() {
        return getLongOrNull(EmployeeColumns.COUNTRY_ID);
    }

    /**
     * Get the {@code country_name} value.
     * Can be {@code null}.
     */
    public String getCountryName() {
        Integer index = getCachedColumnIndexOrThrow(EmployeeColumns.COUNTRY_NAME);
        return getString(index);
    }

    /**
     * Get the {@code enterprise} value.
     * Cannot be {@code null}.
     */
    public String getEnterprise() {
        Integer index = getCachedColumnIndexOrThrow(EmployeeColumns.ENTERPRISE);
        return getString(index);
    }

    /**
     * Get the {@code image_url} value.
     * Can be {@code null}.
     */
    public String getImageUrl() {
        Integer index = getCachedColumnIndexOrThrow(EmployeeColumns.IMAGE_URL);
        return getString(index);
    }
}
