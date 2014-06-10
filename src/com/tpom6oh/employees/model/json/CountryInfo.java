
package com.tpom6oh.employees.model.json;

import com.google.gson.annotations.Expose;

public class CountryInfo {

    @Expose
    private long countryId;
    @Expose
    private String countryName;

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

}
