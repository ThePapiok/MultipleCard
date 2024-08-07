package com.thepapiok.multiplecard.collections;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "countries")
public class Country {
    @Id
    private String id;
    private String name;
    private String code;
}
