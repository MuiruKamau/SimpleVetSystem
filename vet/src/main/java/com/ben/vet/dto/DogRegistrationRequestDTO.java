package com.ben.vet.dto;

import lombok.Data;

@Data
public class DogRegistrationRequestDTO {
    private String name;
    private Integer age; // Can be null, default will be set in service
    private String breed;
    private Long customerId; // To associate the dog with a customer
}