package com.ben.vet.dog;

import lombok.Data;

@Data
public class DogResponseDTO {
    private Long dogId;
    private String name;
    private Integer age;
    private String breed;
    private boolean isActive;
    private Long customerId; // To show which customer owns the dog (or CustomerResponseDTO if you want more customer info)
}
