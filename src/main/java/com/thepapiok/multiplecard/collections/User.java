package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String countryId;
    private String phone;
    private String city;
    private String postalCode;
    private String street;
    private int houseNumber;
    private int apartamentNumber;
    private String password;
    private Card card;
    private boolean isActive;
    private int points;
    private String verificationNumber;
    private Role role;
    private Review review;

}
