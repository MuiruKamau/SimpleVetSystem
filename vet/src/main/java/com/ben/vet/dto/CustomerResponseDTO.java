package com.ben.vet.dto;


import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long customerId;
    private String name;
    private boolean isActive;
    private String phoneNumber;
    private String address;
    private String email;
}
