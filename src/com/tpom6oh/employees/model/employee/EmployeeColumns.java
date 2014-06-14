package com.tpom6oh.employees.model.employee;

import android.net.Uri;
import android.provider.BaseColumns;

import com.tpom6oh.employees.model.EmployeesProvider;

/**
 * Columns for the {@code employee} table.
 */
public interface EmployeeColumns extends BaseColumns {
    String TABLE_NAME = "employee";
    Uri CONTENT_URI = Uri.parse(EmployeesProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    String _ID = BaseColumns._ID;
    String NAME = "name";
    String DIVISION = "division";
    String TEAM = "team";
    String SALARY = "salary";
    String COMPANY = "company";
    String EMPLOYMENT_YEAR = "employment_year";
    String EMPLOYMENT_DATE = "employment_date";
    String COUNTRY_ID = "country_id";
    String COUNTRY_NAME = "country_name";
    String ENTERPRISE = "enterprise";
    String IMAGE_URL = "image_url";

    String DEFAULT_ORDER = _ID;

	// @formatter:off
    String[] FULL_PROJECTION = new String[] {
            _ID,
            NAME,
            DIVISION,
            TEAM,
            SALARY,
            COMPANY,
            EMPLOYMENT_YEAR,
            EMPLOYMENT_DATE,
            COUNTRY_ID,
            COUNTRY_NAME,
            ENTERPRISE,
            IMAGE_URL
    };
    // @formatter:on
}