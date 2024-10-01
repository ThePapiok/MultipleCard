package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ShopConverterTest {
  @Mock private AddressConverter addressConverter;
  private ShopConverter shopConverter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    shopConverter = new ShopConverter(addressConverter);
  }

  @Test
  public void shouldSuccessAtGetEntity() {
    final String name = "shop";
    final String accountNumber = "1231231234314534252345";
    List<AddressDTO> addressDTOList = List.of(new AddressDTO(), new AddressDTO());
    List<Address> addresses = List.of(new Address(), new Address());
    RegisterShopDTO registerShopDTO = new RegisterShopDTO();
    registerShopDTO.setAddress(addressDTOList);
    registerShopDTO.setName(name);
    registerShopDTO.setAccountNumber(accountNumber);
    Shop expectedShop = new Shop();
    expectedShop.setName(name);
    expectedShop.setAccountNumber(accountNumber);
    expectedShop.setImageUrl("");
    expectedShop.setPoints(addresses);

    when(addressConverter.getEntities(addressDTOList)).thenReturn(addresses);

    assertEquals(expectedShop, shopConverter.getEntity(registerShopDTO));
  }
}
