package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.dto.AddressDTO;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AddressConverter {
  public AddressDTO getDTO(Address address) {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCountry(address.getCountry());
    addressDTO.setCity(address.getCity());
    addressDTO.setProvince(address.getProvince());
    addressDTO.setStreet(address.getStreet());
    addressDTO.setHouseNumber(address.getHouseNumber());
    addressDTO.setPostalCode(address.getPostalCode());
    String apartmentNumber = String.valueOf(address.getApartmentNumber());
    if ("null".equals(apartmentNumber)) {
      apartmentNumber = "";
    }
    addressDTO.setApartmentNumber(apartmentNumber);
    return addressDTO;
  }

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

  public List<AddressDTO> getDTOs(List<Address> list) {
    return list.stream().map(this::getDTO).toList();
  }
}
