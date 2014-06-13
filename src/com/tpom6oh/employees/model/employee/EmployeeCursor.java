package com.tpom6oh.employees.model.employee;

import java.util.Date;

import android.database.Cursor;

import com.tpom6oh.employees.model.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code employee} table.
 */
public class EmployeeCursor extends AbstractCursor {
    public EmployeeCursor(Cursor cursor) {
        super(cursor);
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
     * Get the {@code employement_year} value.
     */
    public int getEmployementYear() {
        return getIntegerOrNull(EmployeeColumns.EMPLOYMENT_YEAR);
    }

    /**
     * Get the {@code employement_date} value.
     * Cannot be {@code null}.
     */
    public Date getEmployementDate() {
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
