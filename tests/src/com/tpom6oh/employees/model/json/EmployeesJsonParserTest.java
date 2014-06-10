package com.tpom6oh.employees.model.json;

import android.content.res.AssetManager;
import android.test.AndroidTestCase;

import com.tpom6oh.employees.model.EmployeeInfo;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

public class EmployeesJsonParserTest extends AndroidTestCase {

    public void testAssetsParse() {
        IParseListener parseListener = Mockito.mock(IParseListener.class);
        EmployeesJsonParser parser = new EmployeesJsonParser(parseListener);
        InputStream in;
        try {
            AssetManager manager = getContext().getAssets();
            in = manager.open("base.organization.json", AssetManager.ACCESS_STREAMING);
            parser.parseEnterpriseCountryJson(in);
            Mockito.verify(parseListener).onParseDataStart();
            Mockito.verify(parseListener).onAllCountriesDataParsed();
            Mockito.verify(parseListener).onAllEmployeesDataParsed();
            Mockito.verify(parseListener, Mockito.times(15)).
                           onEmployeeInfoReceive(Matchers.<EmployeeInfo>any());
            Mockito.verify(parseListener).onCountryInfoReceive(Matchers.<CountryInfo>any());
        } catch (IOException e) {
            fail("Failed to open assets file");
        }
    }
}
