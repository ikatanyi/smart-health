package io.smarthealth.administration.app.data;

import io.smarthealth.administration.app.domain.Address;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AddressData {

    private Long id;
    private String type;
    private String line1;
    private String line2;
    private String town;
    private String county;
    private String country;
    private String postalCode;
    private String email;
    private String phone;
    private String website;

    public static AddressData map(Address address) {
        AddressData data = new AddressData();
        data.setId(address.getId());
        if (address.getType() != null) {
            data.setType(address.getType().name());
        }
        data.setLine1(address.getLine1());
        data.setLine2(address.getLine2());
        data.setTown(address.getTown());
        data.setCounty(address.getCounty());
        data.setCountry(address.getCountry());
        data.setPostalCode(address.getPostalCode());
        data.setWebsite(address.getWebsite());
        data.setPhone(address.getPhone());
        data.setEmail(address.getEmail());
        return data;

    }

    public static Address map(AddressData addressData) {
        Address address = new Address();
        if(addressData.getId()!=null){
        address.setId(addressData.getId());
        }
        if (addressData.getType() != null) {
            address.setType(Address.Type.valueOf(addressData.getType()));
        } else {
            address.setType(Address.Type.Office);
        }
        address.setLine1(addressData.getLine1());
        address.setLine2(addressData.getLine2());
        address.setTown(addressData.getTown());
        address.setCounty(addressData.getCounty());
        address.setCountry(addressData.getCountry());
        address.setPostalCode(addressData.getPostalCode());
        address.setWebsite(addressData.getWebsite());
        address.setPhone(addressData.getPhone());
        address.setEmail(addressData.getEmail());
        return address;

    }
}
