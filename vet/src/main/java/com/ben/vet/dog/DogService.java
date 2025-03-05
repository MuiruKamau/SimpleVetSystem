package com.ben.vet.dog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DogService {
    DogResponseDTO registerDog(DogRegistrationRequestDTO registrationRequestDTO);

    // Add declarations for the methods used in DogController
    Page<Dog> searchDogss(String customerName, String breed, Pageable pageable); // Matches controller's usage
    DogResponseDTO getDogById(Long dogId);
    DogResponseDTO updateDog(Long dogId, DogRegistrationRequestDTO updateRequestDTO);
    void deleteDog(Long dogId);
    List<DogResponseDTO> getAllDogs();
    void deactivateDogsByCustomer(Long customerId);
}