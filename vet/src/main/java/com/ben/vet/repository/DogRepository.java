package com.ben.vet.repository;

import com.ben.vet.model.Dog;
import com.ben.vet.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {


    //  Filter by breed name (ignoring case)
    Page<Dog> findByBreedIgnoreCaseContaining(String breedName, Pageable pageable);

    // Filter by customer name (using join and ignoring case)
    Page<Dog> findByCustomer_NameIgnoreCaseContaining(String customerName, Pageable pageable);

    // Filter by both breed and customer name (using AND and ignoring case)
    Page<Dog> findByBreedIgnoreCaseContainingAndCustomer_NameIgnoreCaseContaining(String breedName, String customerName, Pageable pageable);

    // Find dogs by customer for deactivation
    List<Dog> findByCustomer(Customer customer);
}