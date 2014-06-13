package com.tpom6oh.employees.model.employee;

import android.content.ContentResolver;
import android.net.Uri;

import com.tpom6oh.employees.model.base.AbstractContentValues;

import java.util.Date;

/**
 * Content values wrapper for the {@code employee} table.
 */
public class EmployeeContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return EmployeeColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     * 
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, EmployeeSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public EmployeeContentValues putName(String value) {
        if (value == null) throw new IllegalArgumentException("value for name must not be null");
        mContentValues.put(EmployeeColumns.NAME, value);
        return this;
    }



    public EmployeeContentValues putDivision(String value) {
        if (value == null) throw new IllegalArgumentException("value for division must not be null");
        mContentValues.put(EmployeeColumns.DIVISION, value);
        return this;
    }



    public EmployeeContentValues putSalary(int value) {
        mContentValues.put(EmployeeColumns.SALARY, value);
        return this;
    }



    public EmployeeContentValues putCompany(String value) {
        if (value == null) throw new IllegalArgumentException("value for company must not be null");
        mContentValues.put(EmployeeColumns.COMPANY, value);
        return this;
    }



    public EmployeeContentValues putEmployementYear(int value) {
        mContentValues.put(EmployeeColumns.EMPLOYMENT_YEAR, value);
        return this;
    }



    public EmployeeContentValues putEmployementDate(Date value) {
        if (value == null) throw new IllegalArgumentException("value for employementDate must not be null");
        mContentValues.put(EmployeeColumns.EMPLOYMENT_DATE, value.getTime());
        return this;
    }


    public EmployeeContentValues putEmployementDate(long value) {
        mContentValues.put(EmployeeColumns.EMPLOYMENT_DATE, value);
        return this;
    }


    public EmployeeContentValues putCountryId(long value) {
        mContentValues.put(EmployeeColumns.COUNTRY_ID, value);
        return this;
    }



    public EmployeeContentValues putCountryName(String value) {
        mContentValues.put(EmployeeColumns.COUNTRY_NAME, value);
        return this;
    }

    public EmployeeContentValues putCountryNameNull() {
        mContentValues.putNull(EmployeeColumns.COUNTRY_NAME);
        return this;
    }


    public EmployeeContentValues putEnterprise(String value) {
        if (value == null) throw new IllegalArgumentException("value for enterprise must not be null");
        mContentValues.put(EmployeeColumns.ENTERPRISE, value);
        return this;
    }



    public EmployeeContentValues putImageUrl(String value) {
        mContentValues.put(EmployeeColumns.IMAGE_URL, value);
        return this;
    }

    public EmployeeContentValues putImageUrlNull() {
        mContentValues.putNull(EmployeeColumns.IMAGE_URL);
        return this;
    }

}
