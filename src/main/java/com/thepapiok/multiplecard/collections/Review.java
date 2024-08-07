package com.thepapiok.multiplecard.collections;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {
    private String description;
    private int rating;
    private LocalDateTime createdAt;
}
