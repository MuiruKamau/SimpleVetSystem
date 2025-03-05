package com.ben.vet.dog;

import com.ben.vet.model.Customer;


public record DogRegistrationRequestDTO (
    String name,
    Integer age, // Can be null, default will be set in service
    String breed,
    Long customerId){
    // To associate the dog with a customer

    public static Dog getDogFromRequest(DogRegistrationRequestDTO dogRegistrationRequestDTO, Customer customer){
        return Dog.builder()
                .age(dogRegistrationRequestDTO.age())
                .name(dogRegistrationRequestDTO.name())
                .breed(dogRegistrationRequestDTO.breed())
                .customer(customer).build();
    }
}