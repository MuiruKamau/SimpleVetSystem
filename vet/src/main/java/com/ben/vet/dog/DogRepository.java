package com.ben.vet.dog;

import com.ben.vet.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
           SELECT d FROM Dog d WHERE (:breed IS NULL OR d.breed LIKE %:breed%) AND
           (:customerName IS NULL OR d.customer.name LIKE %:customerName% )
            """)
    Page<Dog> searchDogs(@Param("customerName") String customerName, @Param("breed") String breed, Pageable pageable);

    // Custom query to fetch all dogs and eagerly load their customers using JOIN FETCH
    @Query("SELECT d FROM Dog d JOIN FETCH d.customer")
    List<Dog> findAllWithCustomer(); // New method to fetch all dogs with customer

    // For paged findAll, if you need it (optional, but good practice for larger datasets)
    @Query("SELECT d FROM Dog d JOIN FETCH d.customer")
    Page<Dog> findAllWithCustomer(Pageable pageable);
}