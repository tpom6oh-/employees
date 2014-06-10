// Copyright (c) 2014 Yandex LLC. All rights reserved.
// Author: Alexey Verein <tpom6oh@yandex-team.ru>

package com.tpom6oh.employees.model.json;

import com.tpom6oh.employees.model.EmployeeInfo;

public interface IParseListener {
    void onAllEmployeesDataParsed();

    void onParseDataStart();

    void onAllCountriesDataParsed();

    void onEmployeeInfoReceive(EmployeeInfo employeeInfo);

    void onCountryInfoReceive(CountryInfo country);
}
