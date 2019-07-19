package io.smarthealth.organization.person.mapper;

import io.smarthealth.organization.person.domain.PersonAddress;
import lombok.Data;
//import org.smarthealth.patient.domain.Address;

/**
 *
 * @author Kelsas
 */
@Data
public final class AddressDTO {

    private String line1;
    private String line2;
    private String town;
    private String County;
    private String postalCode;
    private String Country;

    public static PersonAddress map(final AddressDTO address) {
        final PersonAddress addressEntity = new PersonAddress();
        addressEntity.setLine1(address.getLine1());
        addressEntity.setLine2(address.getLine2());
        addressEntity.setTown(address.getTown());
        addressEntity.setCounty(address.getCounty());
        addressEntity.setPostalCode(address.getPostalCode());
        addressEntity.setCountry(address.getCountry());
        return addressEntity;
    }

    public static AddressDTO map(final PersonAddress addressEntity) {
        final AddressDTO address = new AddressDTO();
        address.setCountry(addressEntity.getCountry());
        address.setCounty(addressEntity.getCounty());
        address.setLine1(addressEntity.getLine2());
        address.setLine2(addressEntity.getLine2());
        address.setPostalCode(addressEntity.getPostalCode());
        address.setTown(addressEntity.getTown());
        return address;
    }

}
