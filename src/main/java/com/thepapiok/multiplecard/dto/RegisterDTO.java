package com.thepapiok.multiplecard.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String firstName;
    private String lastName;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private String postalCode;
    private String city;
    private String country;
    private String phone;
    private String password;
    private String retypedPassword;
}
