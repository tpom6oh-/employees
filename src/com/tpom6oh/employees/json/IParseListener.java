package com.tpom6oh.employees.json;

import com.tpom6oh.employees.model.EmployeeInfo;

public interface IParseListener {
    /**
     * Callback on employees data parse finish at {@link com.tpom6oh.employees.json
     * .EmployeesJsonParser}
     */
    void onAllEmployeesDataParsed();
    /**
     * Callback on data parse start at {@link com.tpom6oh.employees.json.EmployeesJsonParser}
     */
    void onParseDataStart();
    /**
     * Callback on data parse end at {@link com.tpom6oh.employees.json.EmployeesJsonParser}
     */
    void onParseDataEnd();
    /**
     * Callback on one employee's data parse at {@link com.tpom6oh.employees.json
     * .EmployeesJsonParser}
     */
    void onEmployeeInfoReceive(EmployeeInfo employeeInfo);
    /**
     * Callback on one country's data parse at {@link com.tpom6oh.employees.json
     * .EmployeesJsonParser}
     */
    void onCountryInfoReceive(CountryInfo country);
}
