package com.tpom6oh.employees.model;

import java.util.Date;

public class EmployeeInfo {
    private String employeeName;
    private String divisionName;
    private String teamName;
    private int monthlySalary;
    private Date employmentDate;
    private String companyName;
    private long countryId;
    private String enterpriseName;

    public EmployeeInfo(String employeeName, String divisionName, String teamName,
                        int monthlySalary, Date employmentDate, String companyName,
                        long countryId, String enterpriseName) {

        this.employeeName = employeeName;
        this.divisionName = divisionName;
        this.teamName = teamName;
        this.monthlySalary = monthlySalary;
        this.employmentDate = employmentDate;
        this.companyName = companyName;
        this.countryId = countryId;
        this.enterpriseName = enterpriseName;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(int monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }
}
