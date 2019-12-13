package io.smarthealth.organization.person.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.organization.person.domain.PersonAddress;
import lombok.Data;
//import org.smarthealth.patient.domain.Address;

/**
 *
 * @author Kelsas
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public final class AddressData {
    private Long id;

    private String line1;
    private String line2;
    private String town;
    private String county;
    private String postalCode;
    private String country;

    public static PersonAddress map(final AddressData address) {
        final PersonAddress addressEntity = new PersonAddress();
        addressEntity.setLine1(address.getLine1());
        addressEntity.setLine2(address.getLine2());
        addressEntity.setTown(address.getTown());
        addressEntity.setCounty(address.getCounty());
        addressEntity.setPostalCode(address.getPostalCode());
        addressEntity.setCountry(address.getCountry());
        return addressEntity;
    }

    public static AddressData map(final PersonAddress addressEntity) {
        final AddressData address = new AddressData();
        address.setCountry(addressEntity.getCountry());
        address.setCounty(addressEntity.getCounty());
        address.setLine1(addressEntity.getLine2());
        address.setLine2(addressEntity.getLine2());
        address.setPostalCode(addressEntity.getPostalCode());
        address.setTown(addressEntity.getTown());
        address.setId(addressEntity.getId());
        return address;
    }

}
