package com.ben.vet.dog;

import com.ben.vet.model.Customer;
import com.ben.vet.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final CustomerRepository customerRepository;


    @Override
    public DogResponseDTO registerDog(DogRegistrationRequestDTO registrationRequestDTO) {
        Customer customer = customerRepository.findById(registrationRequestDTO.customerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " ));
        if (!customer.isActive()) {
            throw new IllegalStateException("Cannot register dog for inactive customer: " + customer.getName());
        }
        Dog savedDog = dogRepository.save(DogRegistrationRequestDTO.getDogFromRequest(registrationRequestDTO, customer));
        return mapToDogResponseDTO(savedDog);
    }

    public Page<DogResponseDTO> searchDogs(DogSearchRequestDTO searchRequestDTO) {
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
            dogPage = dogRepository.findAllWithCustomer(pageable); // Use findAllWithCustomer for paging if needed
        }
        return dogPage.map(this::mapToDogResponseDTO);
    }

    public void deactivateDogsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        List<Dog> dogsToDeactivate = dogRepository.findByCustomer(customer);
        dogsToDeactivate.forEach(dog -> {
            dog.setActive(false);
            dogRepository.save(dog);
        });
    }

    public DogResponseDTO getDogById(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new RuntimeException("Dog not found: " + dogId));
        return mapToDogResponseDTO(dog);
    }

    public DogResponseDTO updateDog(Long dogId, DogRegistrationRequestDTO updateRequestDTO) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new RuntimeException("Dog not found for update: " + dogId));
        if (updateRequestDTO.name() != null) {
            dog.setName(updateRequestDTO.name());
        }
        if (updateRequestDTO.age() != null) {
            dog.setAge(updateRequestDTO.age());
        }
        if (updateRequestDTO.breed() != null) {
            dog.setBreed(updateRequestDTO.breed());
        }
        Dog updatedDog = dogRepository.save(dog);
        return mapToDogResponseDTO(updatedDog);
    }

    public void deleteDog(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new RuntimeException("Dog not found for deletion: " + dogId));
        dogRepository.delete(dog);
    }

    @Override
    public List<DogResponseDTO> getAllDogs() {
        List<Dog> dogs = dogRepository.findAllWithCustomer(); // Use findAllWithCustomer here
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
        // Now dog.getCustomer() will not be null because of JOIN FETCH
        responseDTO.setCustomerId(dog.getCustomer().getCustomerId());
        return responseDTO;
    }

    public Page<Dog> searchDogss(String customerName, String breed, Pageable pageable) {
        return dogRepository.searchDogs(customerName, breed, pageable);
    }
}


//package com.ben.vet.dog;
//
//import com.ben.vet.model.Customer;
//import com.ben.vet.repository.CustomerRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class DogServiceImpl implements DogService { // Removed "implements com.ben.vet.dog.DogService"
//
//    private final DogRepository dogRepository;
//    private final CustomerRepository customerRepository;
//
////    @Autowired
////    public DogServiceImpl(DogRepository dogRepository, CustomerRepository customerRepository) {
////        this.dogRepository = dogRepository;
////        this.customerRepository = customerRepository;
////    }
//
//    @Override
//    public DogResponseDTO registerDog(DogRegistrationRequestDTO registrationRequestDTO) {
//
//        Customer customer = customerRepository.findById(registrationRequestDTO.customerId())
//                .orElseThrow(() -> new RuntimeException("Customer not found: " ));
//
//        // **NEW LOGIC: Check if customer is active**
//        if (!customer.isActive()) {
//            throw new IllegalStateException("Cannot register dog for inactive customer: " + customer.getName());
//        }
//
//        Dog savedDog = dogRepository.save(DogRegistrationRequestDTO.getDogFromRequest(registrationRequestDTO, customer));
//        return mapToDogResponseDTO(savedDog);
//    }
//
//    public Page<DogResponseDTO> searchDogs(DogSearchRequestDTO searchRequestDTO) { // Changed to public access
//        Pageable pageable = PageRequest.of(searchRequestDTO.getPageNo(), searchRequestDTO.getPageSize());
//        String breedNameFilter = searchRequestDTO.getBreedName();
//        String customerNameFilter = searchRequestDTO.getCustomerName();
//
//        Page<Dog> dogPage;
//        if (breedNameFilter != null && customerNameFilter != null) {
//            dogPage = dogRepository.findByBreedIgnoreCaseContainingAndCustomer_NameIgnoreCaseContaining(breedNameFilter, customerNameFilter, pageable);
//        } else if (breedNameFilter != null) {
//            dogPage = dogRepository.findByBreedIgnoreCaseContaining(breedNameFilter, pageable);
//        } else if (customerNameFilter != null) {
//            dogPage = dogRepository.findByCustomer_NameIgnoreCaseContaining(customerNameFilter, pageable);
//        } else {
//            dogPage = dogRepository.findAll(pageable);
//        }
//        return dogPage.map(this::mapToDogResponseDTO);
//    }
//
//    public void deactivateDogsByCustomer(Long customerId) { // Changed to public access
//        Customer customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
//        List<Dog> dogsToDeactivate = dogRepository.findByCustomer(customer);
//        dogsToDeactivate.forEach(dog -> {
//            dog.setActive(false);
//            dogRepository.save(dog);
//        });
//    }
//
//    public DogResponseDTO getDogById(Long dogId) { // Changed to public access
//        Dog dog = dogRepository.findById(dogId)
//                .orElseThrow(() -> new RuntimeException("Dog not found: " + dogId));
//        return mapToDogResponseDTO(dog);
//    }
//
//    public DogResponseDTO updateDog(Long dogId, DogRegistrationRequestDTO updateRequestDTO) {
//        Dog dog = dogRepository.findById(dogId)
//                .orElseThrow(() -> new RuntimeException("Dog not found for update: " + dogId));
//
//        // Update fields if provided in the DTO
//        if (updateRequestDTO.name() != null) {
//            dog.setName(updateRequestDTO.name());
//        }
//        if (updateRequestDTO.age() != null) {
//            dog.setAge(updateRequestDTO.age());
//        }
//        if (updateRequestDTO.breed() != null) {
//            dog.setBreed(updateRequestDTO.breed());
//        }
//        // Customer cannot be updated via this endpoint for simplicity, can be added if needed
//
//        Dog updatedDog = dogRepository.save(dog);
//        return mapToDogResponseDTO(updatedDog);
//    }
//
//    public void deleteDog(Long dogId) {
//        Dog dog = dogRepository.findById(dogId)
//                .orElseThrow(() -> new RuntimeException("Dog not found for deletion: " + dogId));
//        dogRepository.delete(dog);
//    }
//
//    public List<DogResponseDTO> getAllDogs() {
//        List<Dog> dogs = dogRepository.findAll();
//        return dogs.stream()
//                .map(this::mapToDogResponseDTO)
//                .collect(Collectors.toList());
//    }
//
//    private DogResponseDTO mapToDogResponseDTO(Dog dog) {
//        DogResponseDTO responseDTO = new DogResponseDTO();
//        responseDTO.setDogId(dog.getDogId());
//        responseDTO.setName(dog.getName());
//        responseDTO.setAge(dog.getAge());
//        responseDTO.setBreed(dog.getBreed());
//        responseDTO.setActive(dog.isActive());
//        responseDTO.setCustomerId(dog.getCustomer().getCustomerId());
//        return responseDTO;
//    }
//
//    public Page<Dog> searchDogss(String customerName, String breed, Pageable pageable) {
//        return dogRepository.searchDogs(customerName, breed, pageable);
//    }
//}