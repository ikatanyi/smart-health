package io.smarthealth.report.data;

import io.smarthealth.administration.app.domain.Address;
import io.smarthealth.organization.facility.domain.Facility;

public class CompanyHeader {
    private String taxNumber;
    private String companyName;
    private String address;
    private String city;
    private String street;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private String website;
    private String country;
    private byte[] companyLogo;

    public static CompanyHeader of(Facility facility){
        CompanyHeader header = new CompanyHeader();
        if(facility!=null){
            header.setCompanyName(facility.getFacilityName());
            header.setWebsite(facility.getOrganization().getWebsite());
            header.setTaxNumber(facility.getOrganization().getTaxNumber());

            if(facility.getOrganization().getAddress()!=null && !facility.getOrganization().getAddress().isEmpty()){
                Address address = facility.getOrganization().getAddress().get(0);
                header.setAddress(address.getLine1());
                header.setCity(address.getTown());
                header.setStreet(address.getLine2());
                header.setPostalCode(address.getPostalCode());
                header.setPhoneNumber(address.getPhone());
                header.setEmail(address.getEmail());
                header.setCountry(address.getCountry());

            }
            if(facility.getCompanyLogo()!=null){
                header.setCompanyLogo(facility.getCompanyLogo().getData());
            }
        }
        return header;
    }
    public String getCompanyAddress(){
       return companyName+" \n"
               +address+" \n"
               +city+", "+street+", "+postalCode+" "+country+" \n"
               +phoneNumber+", "+email+" \n"
               +website;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public byte[] getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(byte[] companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }
}
