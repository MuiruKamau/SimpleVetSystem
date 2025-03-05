package com.ben.vet.dog;

import lombok.Data;

@Data
public class DogSearchRequestDTO {
    private String breedName; // For filtering by breed name (can be null)
    private String customerName; // For filtering by customer name (can be null)
    private Integer pageNo = 0; // Default page number for pagination
    private Integer pageSize = 10; // Default page size for pagination
}
