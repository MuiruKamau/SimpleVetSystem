package com.ben.vet.controller;

import com.ben.vet.dto.DogRegistrationRequestDTO;
import com.ben.vet.dto.DogResponseDTO;
import com.ben.vet.dto.DogSearchRequestDTO;
import com.ben.vet.service.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dogs")
public class DogController {

    private final DogService dogService;

    @Autowired
    public DogController(DogService dogService) {
        this.dogService = dogService;
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerDog(@RequestBody DogRegistrationRequestDTO registrationRequestDTO) {
        try {
            DogResponseDTO dogResponseDTO = dogService.registerDog(registrationRequestDTO);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Dog registered successfully");
            responseMap.put("status", HttpStatus.CREATED.value());
            responseMap.put("data", dogResponseDTO);
            return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
        } catch (IllegalStateException e) { // **Catch the specific IllegalStateException**
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", e.getMessage()); // Use the exception message
            errorResponseMap.put("status", HttpStatus.BAD_REQUEST.value()); // Or HttpStatus.CONFLICT (409)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseMap); // Return 400 Bad Request
        } catch (RuntimeException e) { // Catch other RuntimeExceptions (like "Customer not found")
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Error during dog registration");
            errorResponseMap.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseMap);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDogs(DogSearchRequestDTO searchRequestDTO) {
        Page<DogResponseDTO> dogPage = dogService.searchDogs(searchRequestDTO);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Dog search results");
        responseMap.put("status", HttpStatus.OK.value());
        responseMap.put("data", dogPage); // Include Page<DogResponseDTO> as "data"
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @GetMapping("/{dogId}")
    public ResponseEntity<Map<String, Object>> getDogById(@PathVariable Long dogId) {
        try {
            DogResponseDTO dogResponseDTO = dogService.getDogById(dogId);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Dog found");
            responseMap.put("status", HttpStatus.OK.value());
            responseMap.put("data", dogResponseDTO); // Include DogResponseDTO as "data"
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Dog not found");
            errorResponseMap.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseMap);
        }
    }

    @PutMapping("/{dogId}")
    public ResponseEntity<Map<String, Object>> updateDog(@PathVariable Long dogId, @RequestBody DogRegistrationRequestDTO updateRequestDTO) {
        try {
            DogResponseDTO dogResponseDTO = dogService.updateDog(dogId, updateRequestDTO);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Dog updated successfully");
            responseMap.put("status", HttpStatus.OK.value());
            responseMap.put("data", dogResponseDTO);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Dog not found for update");
            errorResponseMap.put("status", HttpStatus.NOT_FOUND.value()); // 404 for update failure (not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseMap);
        }
    }

    @DeleteMapping("/{dogId}")
    public ResponseEntity<Map<String, Object>> deleteDog(@PathVariable Long dogId) {
        try {
            dogService.deleteDog(dogId);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Dog deleted successfully");
            responseMap.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(responseMap, HttpStatus.OK); // 200 OK for successful deletion
        } catch (RuntimeException e) {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Dog not found for deletion");
            errorResponseMap.put("status", HttpStatus.NOT_FOUND.value()); // 404 for delete failure (not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseMap);
        }
    }

    @GetMapping // GET /api/dogs (at the base path)
    public ResponseEntity<Map<String, Object>> getAllDogs() {
        List<DogResponseDTO> dogResponseDTOList = dogService.getAllDogs();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "All dogs retrieved successfully");
        responseMap.put("status", HttpStatus.OK.value());
        responseMap.put("data", dogResponseDTOList); // Include List<DogResponseDTO> as "data"
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }





}
