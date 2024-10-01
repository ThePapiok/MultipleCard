package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.dto.AddressDTO;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AddressConverter {

  // TODO - add this to register user

  public Address getEntity(AddressDTO addressDTO) {
    Address address = new Address();
    address.setProvince(addressDTO.getProvince());
    address.setCity(addressDTO.getCity());
    address.setStreet(addressDTO.getStreet());
    address.setPostalCode(addressDTO.getPostalCode());
    address.setHouseNumber(addressDTO.getHouseNumber());
    String apartmentNumber = addressDTO.getApartmentNumber();
    if ("".equals(apartmentNumber)) {
      address.setApartmentNumber(null);
    } else {
      address.setApartmentNumber(Integer.parseInt(apartmentNumber));
    }
    address.setCountry(addressDTO.getCountry());
    return address;
  }

  public List<Address> getEntities(List<AddressDTO> list) {
    return list.stream().map(this::getEntity).toList();
  }
}
