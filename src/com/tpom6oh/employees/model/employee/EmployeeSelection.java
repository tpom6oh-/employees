package com.tpom6oh.employees.model.employee;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.tpom6oh.employees.model.base.AbstractSelection;

import java.util.Date;

/**
 * Selection for the {@code employee} table.
 */
public class EmployeeSelection extends AbstractSelection<EmployeeSelection> {
    @Override
    public Uri uri() {
        return EmployeeColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code EmployeeCursor} object, which is positioned before the first entry, or null.
     */
    public EmployeeCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new EmployeeCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null}.
     */
    public EmployeeCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null}.
     */
    public EmployeeCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public EmployeeSelection id(long... value) {
        addEquals(EmployeeColumns._ID, toObjectArray(value));
        return this;
    }


    public EmployeeSelection name(String... value) {
        addEquals(EmployeeColumns.NAME, value);
        return this;
    }

    public EmployeeSelection nameNot(String... value) {
        addNotEquals(EmployeeColumns.NAME, value);
        return this;
    }

    public EmployeeSelection nameLike(String... value) {
        addLike(EmployeeColumns.NAME, value);
        return this;
    }

    public EmployeeSelection division(String... value) {
        addEquals(EmployeeColumns.DIVISION, value);
        return this;
    }

    public EmployeeSelection divisionNot(String... value) {
        addNotEquals(EmployeeColumns.DIVISION, value);
        return this;
    }

    public EmployeeSelection divisionLike(String... value) {
        addLike(EmployeeColumns.DIVISION, value);
        return this;
    }

    public EmployeeSelection team(String... value) {
        addEquals(EmployeeColumns.TEAM, value);
        return this;
    }

    public EmployeeSelection teamNot(String... value) {
        addNotEquals(EmployeeColumns.TEAM, value);
        return this;
    }

    public EmployeeSelection teamLike(String... value) {
        addLike(EmployeeColumns.TEAM, value);
        return this;
    }

    public EmployeeSelection salary(int... value) {
        addEquals(EmployeeColumns.SALARY, toObjectArray(value));
        return this;
    }

    public EmployeeSelection salaryNot(int... value) {
        addNotEquals(EmployeeColumns.SALARY, toObjectArray(value));
        return this;
    }

    public EmployeeSelection salaryGt(int value) {
        addGreaterThan(EmployeeColumns.SALARY, value);
        return this;
    }

    public EmployeeSelection salaryGtEq(int value) {
        addGreaterThanOrEquals(EmployeeColumns.SALARY, value);
        return this;
    }

    public EmployeeSelection salaryLt(int value) {
        addLessThan(EmployeeColumns.SALARY, value);
        return this;
    }

    public EmployeeSelection salaryLtEq(int value) {
        addLessThanOrEquals(EmployeeColumns.SALARY, value);
        return this;
    }

    public EmployeeSelection salaryBetween(int min, int max) {
        addBetween(EmployeeColumns.SALARY, min, max);
        return this;
    }

    public EmployeeSelection company(String... value) {
        addEquals(EmployeeColumns.COMPANY, value);
        return this;
    }

    public EmployeeSelection companyNot(String... value) {
        addNotEquals(EmployeeColumns.COMPANY, value);
        return this;
    }

    public EmployeeSelection companyLike(String... value) {
        addLike(EmployeeColumns.COMPANY, value);
        return this;
    }

    public EmployeeSelection employmentYear(int... value) {
        addEquals(EmployeeColumns.EMPLOYMENT_YEAR, toObjectArray(value));
        return this;
    }

    public EmployeeSelection employmentYearNot(int... value) {
        addNotEquals(EmployeeColumns.EMPLOYMENT_YEAR, toObjectArray(value));
        return this;
    }

    public EmployeeSelection employmentYearGt(int value) {
        addGreaterThan(EmployeeColumns.EMPLOYMENT_YEAR, value);
        return this;
    }

    public EmployeeSelection employmentYearGtEq(int value) {
        addGreaterThanOrEquals(EmployeeColumns.EMPLOYMENT_YEAR, value);
        return this;
    }

    public EmployeeSelection employmentYearLt(int value) {
        addLessThan(EmployeeColumns.EMPLOYMENT_YEAR, value);
        return this;
    }

    public EmployeeSelection employmentYearLtEq(int value) {
        addLessThanOrEquals(EmployeeColumns.EMPLOYMENT_YEAR, value);
        return this;
    }

    public EmployeeSelection employmentDate(Date... value) {
        addEquals(EmployeeColumns.EMPLOYMENT_DATE, value);
        return this;
    }

    public EmployeeSelection employmentDateNot(Date... value) {
        addNotEquals(EmployeeColumns.EMPLOYMENT_DATE, value);
        return this;
    }

    public EmployeeSelection employmentDate(long... value) {
        addEquals(EmployeeColumns.EMPLOYMENT_DATE, toObjectArray(value));
        return this;
    }

    public EmployeeSelection employmentDateAfter(Date value) {
        addGreaterThan(EmployeeColumns.EMPLOYMENT_DATE, value);
        return this;
    }

    public EmployeeSelection employmentDateAfterEq(Date value) {
        addGreaterThanOrEquals(EmployeeColumns.EMPLOYMENT_DATE, value);
        return this;
    }

    public EmployeeSelection employmentDateBefore(Date value) {
        addLessThan(EmployeeColumns.EMPLOYMENT_DATE, value);
        return this;
    }

    public EmployeeSelection employmentDateBeforeEq(Date value) {
        addLessThanOrEquals(EmployeeColumns.EMPLOYMENT_DATE, value);
        return this;
    }

    public EmployeeSelection countryId(long... value) {
        addEquals(EmployeeColumns.COUNTRY_ID, toObjectArray(value));
        return this;
    }

    public EmployeeSelection countryIdNot(long... value) {
        addNotEquals(EmployeeColumns.COUNTRY_ID, toObjectArray(value));
        return this;
    }

    public EmployeeSelection countryIdGt(long value) {
        addGreaterThan(EmployeeColumns.COUNTRY_ID, value);
        return this;
    }

    public EmployeeSelection countryIdGtEq(long value) {
        addGreaterThanOrEquals(EmployeeColumns.COUNTRY_ID, value);
        return this;
    }

    public EmployeeSelection countryIdLt(long value) {
        addLessThan(EmployeeColumns.COUNTRY_ID, value);
        return this;
    }

    public EmployeeSelection countryIdLtEq(long value) {
        addLessThanOrEquals(EmployeeColumns.COUNTRY_ID, value);
        return this;
    }

    public EmployeeSelection countryName(String... value) {
        addEquals(EmployeeColumns.COUNTRY_NAME, value);
        return this;
    }

    public EmployeeSelection countryNameNot(String... value) {
        addNotEquals(EmployeeColumns.COUNTRY_NAME, value);
        return this;
    }

    public EmployeeSelection countryNameLike(String... value) {
        addLike(EmployeeColumns.COUNTRY_NAME, value);
        return this;
    }

    public EmployeeSelection enterprise(String... value) {
        addEquals(EmployeeColumns.ENTERPRISE, value);
        return this;
    }

    public EmployeeSelection enterpriseNot(String... value) {
        addNotEquals(EmployeeColumns.ENTERPRISE, value);
        return this;
    }

    public EmployeeSelection enterpriseLike(String... value) {
        addLike(EmployeeColumns.ENTERPRISE, value);
        return this;
    }

    public EmployeeSelection imageUrl(String... value) {
        addEquals(EmployeeColumns.IMAGE_URL, value);
        return this;
    }

    public EmployeeSelection imageUrlNot(String... value) {
        addNotEquals(EmployeeColumns.IMAGE_URL, value);
        return this;
    }

    public EmployeeSelection imageUrlLike(String... value) {
        addLike(EmployeeColumns.IMAGE_URL, value);
        return this;
    }
}
