package com.ben.vet.dog;

import com.ben.vet.model.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dogs")
@Data
@Builder// Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Generates no-argument constructor
@AllArgsConstructor // Lombok: Generates all-argument constructor
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dogId;

    @Column(nullable = false)
    private String name;

    private Integer age; // Age in months. Can be null if not specified.

    private String breed;

    private boolean isActive = true; // Default to active

    @ManyToOne // Many dogs to one customer, FetchType.LAZY for performance
   @JoinColumn(name = "customer_id", nullable = false) // Foreign key column in 'dogs' table
    private Customer customer;

    // Method to set default age if age is null during registration
    @PrePersist
    public void ensureAgeIsSet() {
        if (this.age == null) {
            this.age = 6; // Default age of six months
        }
    }
}
