package io.smarthealth.report.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestFactory {

    public static Collection<CompanyHeader> generateCompanyHeader(){
        List<CompanyHeader> headerList = new ArrayList<>();
        CompanyHeader header = new CompanyHeader();
        header.setWebsite("www.amtesting.com");
        header.setEmail("healthcare@amtesting.com");
        header.setAddress("P.O.Box 32434");
        header.setCity("Nairobi");
        header.setCountry("Kenya");
        header.setTaxNumber("P02939993");
        header.setStreet("Mango Road");
        header.setCompanyName("McKenze Grand Healthcare");
        header.setPhoneNumber("+254123352222");
        header.setPostalCode("00100");

        headerList.add(header);
        return headerList;
    }
}
