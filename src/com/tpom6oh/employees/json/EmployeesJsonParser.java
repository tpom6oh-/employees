package com.tpom6oh.employees.json;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.tpom6oh.employees.model.EmployeeInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmployeesJsonParser {

    private static final String CHARSET_NAME = "UTF-8";
    private static final String CLASS_TAG = "EmployeesJsonParser.java";

    private EnterpriseInfo currentEnterpriseInfo;
    private CompanyInfo currentCompanyInfo;
    private DivisionInfo currentDivisionInfo;
    private TeamInfo currentTeamInfo;

    private Gson gson;
    private IParseListener parseListener;

    public EmployeesJsonParser(IParseListener parseListener) {
        gson = new Gson();
        this.parseListener = parseListener;
    }

    /**
     * Parses the JSON document with an enterpriseList and a countryList
     * @param inputStream from a JSON document to parse
     * @throws java.io.IOException
     */
    public void parseEnterpriseCountryJson(InputStream inputStream) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, CHARSET_NAME));
        reader.beginObject();
        parseEnterpriseList(reader);
        parseCountries(reader);
    }

    private void parseEnterpriseList(JsonReader reader) throws IOException {
        parseListener.onParseDataStart();
        String enterpriseListName = reader.nextName();
        if (!enterpriseListName.equals("enterpriseList")) {
            throw new IllegalArgumentException("Invalid reader state");
        }
        reader.beginArray();
        currentEnterpriseInfo = new EnterpriseInfo();

        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("enterpriseId")) {
                    currentEnterpriseInfo.enterpriseId = reader.nextLong();
                } else if (name.equals("enterpriseName")) {
                    currentEnterpriseInfo.enterpriseName = reader.nextString();
                } else if (name.equals("enterpriseResponsibleId")) {
                    currentEnterpriseInfo.enterpriseResponsibleId = reader.nextLong();
                } else if (name.equals("companyList")) {
                    parseCompanyList(reader);
                }
            }
            reader.endObject();
        }
        reader.endArray();

        if (parseListener !=null) {
            parseListener.onAllEmployeesDataParsed();
        }
    }

    private void parseCompanyList(JsonReader reader) throws IOException {
        currentCompanyInfo = new CompanyInfo();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("companyId")) {
                    currentCompanyInfo.companyId = reader.nextLong();
                } else if (name.equals("countryId")) {
                    currentCompanyInfo.countryId = reader.nextLong();
                } else if (name.equals("companyName")) {
                    currentCompanyInfo.companyName = reader.nextString();
                } else if (name.equals("companyResponsibleId")) {
                    currentCompanyInfo.companyResponsibleId = reader.nextLong();
                } else if (name.equals("divisionList")) {
                    parseDivisionList(reader);
                }
            }
            reader.endObject();
        }
        reader.endArray();
    }

    private void parseDivisionList(JsonReader reader) throws IOException {
        currentDivisionInfo = new DivisionInfo();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("divisionId")) {
                    currentDivisionInfo.divisionId = reader.nextLong();
                } else if (name.equals("divisionName")) {
                    currentDivisionInfo.divisionName = reader.nextString();
                } else if (name.equals("divisionResponsibleId")) {
                    currentDivisionInfo.divisionResponsibleId = reader.nextLong();
                } else if (name.equals("teamList")) {
                    parseTeamList(reader);
                }
            }
            reader.endObject();
        }
        reader.endArray();
    }

    private void parseTeamList(JsonReader reader) throws IOException {
        currentTeamInfo = new TeamInfo();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("teamId")) {
                    currentTeamInfo.teamId = reader.nextLong();
                } else if (name.equals("teamName")) {
                    currentTeamInfo.teamName = reader.nextString();
                } else if (name.equals("teamResponsibleId")) {
                    currentTeamInfo.teamResponsibleId = reader.nextLong();
                } else if (name.equals("employeeList")) {
                    parseEmployee(reader);
                }
            }
            reader.endObject();
        }
        reader.endArray();
    }

    private void parseEmployee(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            Employee employee = gson.fromJson(reader, Employee.class);
            Date employmentDate = parseEmploymentDate(employee);
            EmployeeInfo employeeInfo = new EmployeeInfo(employee.getEmployeeName(),
                                                         currentDivisionInfo.divisionName,
                                                         currentTeamInfo.teamName,
                                                         employee.getMonthlySalary(),
                                                         employmentDate,
                                                         currentCompanyInfo.companyName,
                                                         currentCompanyInfo.countryId,
                                                         currentEnterpriseInfo.enterpriseName,
                                                         employee.getProfileImage());
            if (parseListener != null) {
                parseListener.onEmployeeInfoReceive(employeeInfo);
            }
        }
        reader.endArray();
    }

    private void parseCountries(JsonReader reader) throws IOException {
        reader.nextName();
        reader.beginArray();
        while (reader.hasNext()) {
            CountryInfo country = gson.fromJson(reader, CountryInfo.class);
            if (parseListener != null) {
                parseListener.onCountryInfoReceive(country);
            }
        }
        reader.endArray();
        reader.close();

        if (parseListener !=null) {
            parseListener.onParseDataComplete();
        }
    }

    private Date parseEmploymentDate(Employee employee) {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd'T'kk:mm:ss.SSSZ",
                                             Locale.ENGLISH);
        Date employmentDate = null;
        try {
            employmentDate = df.parse(employee.getEmploymentDate());
        } catch (ParseException e) {
            Log.e(CLASS_TAG, "Failed to parse employment date");
        }
        return employmentDate;
    }

    private class EnterpriseInfo {
        private long enterpriseId;
        private String enterpriseName;
        private long enterpriseResponsibleId;
    }

    private class CompanyInfo {
        private long companyId;
        private long countryId;
        private String companyName;
        private long companyResponsibleId;
    }

    private class DivisionInfo {
        private long divisionId;
        private String divisionName;
        private long divisionResponsibleId;
    }

    private class TeamInfo {
        private long teamId;
        private String teamName;
        private long teamResponsibleId;
    }
}
