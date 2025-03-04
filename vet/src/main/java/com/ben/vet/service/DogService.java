package com.ben.vet.service;

import com.ben.vet.dto.DogRegistrationRequestDTO;
import com.ben.vet.dto.DogResponseDTO;
import com.ben.vet.dto.DogSearchRequestDTO;
import com.ben.vet.model.Customer;
import com.ben.vet.model.Dog;
import com.ben.vet.repository.CustomerRepository;
import com.ben.vet.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DogService { // Removed "implements com.ben.vet.service.DogService"

    private final DogRepository dogRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public DogService(DogRepository dogRepository, CustomerRepository customerRepository) {
        this.dogRepository = dogRepository;
        this.customerRepository = customerRepository;
    }

    public DogResponseDTO registerDog(DogRegistrationRequestDTO registrationRequestDTO) {
        Dog dog = new Dog();
        dog.setName(registrationRequestDTO.getName());
        dog.setAge(registrationRequestDTO.getAge());
        dog.setBreed(registrationRequestDTO.getBreed());
        dog.ensureAgeIsSet();

        Long customerId = registrationRequestDTO.getCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        // **NEW LOGIC: Check if customer is active**
        if (!customer.isActive()) {
            throw new IllegalStateException("Cannot register dog for inactive customer: " + customer.getName() + " (ID: " + customerId + ")");
        }

        dog.setCustomer(customer);
        Dog savedDog = dogRepository.save(dog);
        return mapToDogResponseDTO(savedDog);
    }

    public Page<DogResponseDTO> searchDogs(DogSearchRequestDTO searchRequestDTO) { // Changed to public access
        Pageable pageable = PageRequest.of(searchRequestDTO.getPageNo(), searchRequestDTO.getPageSize());
        String breedNameFilter = searchRequestDTO.getBreedName();
        String customerNameFilter = searchRequestDTO.getCustomerName();

        Page<Dog> dogPage;
        if (breedNameFilter != null && customerNameFilter != null) {
            dogPage = dogRepository.findByBreedIgnoreCaseContainingAndCustomer_NameIgnoreCaseContaining(breedNameFilter, customerNameFilter, pageable);
        } else if (breedNameFilter != null) {
            dogPage = dogRepository.findByBreedIgnoreCaseContaining(breedNameFilter, pageable);
        } else if (customerNameFilter != null) {
            dogPage = dogRepository.findByCustomer_NameIgnoreCaseContaining(customerNameFilter, pageable);
        } else {
            dogPage = dogRepository.findAll(pageable);
        }
        return dogPage.map(this::mapToDogResponseDTO);
    }

    public void deactivateDogsByCustomer(Long customerId) { // Changed to public access
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        List<Dog> dogsToDeactivate = dogRepository.findByCustomer(customer);
        dogsToDeactivate.forEach(dog -> {
            dog.setActive(false);
            dogRepository.save(dog);
        });
    }

    public DogResponseDTO getDogById(Long dogId) { // Changed to public access
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new RuntimeException("Dog not found: " + dogId));
        return mapToDogResponseDTO(dog);
    }

    public DogResponseDTO updateDog(Long dogId, DogRegistrationRequestDTO updateRequestDTO) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new RuntimeException("Dog not found for update: " + dogId));

        // Update fields if provided in the DTO
        if (updateRequestDTO.getName() != null) {
            dog.setName(updateRequestDTO.getName());
        }
        if (updateRequestDTO.getAge() != null) {
            dog.setAge(updateRequestDTO.getAge());
        }
        if (updateRequestDTO.getBreed() != null) {
            dog.setBreed(updateRequestDTO.getBreed());
        }
        // Customer cannot be updated via this endpoint for simplicity, can be added if needed

        Dog updatedDog = dogRepository.save(dog);
        return mapToDogResponseDTO(updatedDog);
    }

    public void deleteDog(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new RuntimeException("Dog not found for deletion: " + dogId));
        dogRepository.delete(dog);
    }

    public List<DogResponseDTO> getAllDogs() {
        List<Dog> dogs = dogRepository.findAll();
        return dogs.stream()
                .map(this::mapToDogResponseDTO)
                .collect(Collectors.toList());
    }

    private DogResponseDTO mapToDogResponseDTO(Dog dog) {
        DogResponseDTO responseDTO = new DogResponseDTO();
        responseDTO.setDogId(dog.getDogId());
        responseDTO.setName(dog.getName());
        responseDTO.setAge(dog.getAge());
        responseDTO.setBreed(dog.getBreed());
        responseDTO.setActive(dog.isActive());
        responseDTO.setCustomerId(dog.getCustomer().getCustomerId());
        return responseDTO;
    }
}