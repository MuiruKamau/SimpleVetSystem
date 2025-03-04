package com.ben.vet.dto;

import lombok.Data;

@Data
public class CustomerRequestDTO {
    private String name;
    private String phoneNumber;
    private String address;
    private String email;
}
