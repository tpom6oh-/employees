package com.tpom6oh.employees.model.json;

import com.tpom6oh.employees.model.EmployeeInfo;

public interface IParseListener {
    void onAllEmployeesDataParsed();

    void onParseDataStart();

    void onAllCountriesDataParsed();

    void onEmployeeInfoReceive(EmployeeInfo employeeInfo);

    void onCountryInfoReceive(CountryInfo country);
}
