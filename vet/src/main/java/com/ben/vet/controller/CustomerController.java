package com.ben.vet.controller;

import com.ben.vet.dto.CustomerRequestDTO;
import com.ben.vet.dto.CustomerResponseDTO;
import com.ben.vet.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerService.registerCustomer(customerRequestDTO);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Customer registered successfully");
        responseMap.put("status", HttpStatus.CREATED.value());
        responseMap.put("data", customerResponseDTO); // Include CustomerResponseDTO as "data"
        return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
    }

    @PostMapping("/{customerId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateCustomer(@PathVariable Long customerId) {
        try {
            customerService.deactivateCustomer(customerId);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Customer deactivated successfully");
            responseMap.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Customer not found for deactivation");
            errorResponseMap.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseMap);
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Long customerId) {
        try {
            CustomerResponseDTO customerResponseDTO = customerService.getCustomerById(customerId);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Customer found");
            responseMap.put("status", HttpStatus.OK.value());
            responseMap.put("data", customerResponseDTO); // Include CustomerResponseDTO as "data"
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Customer not found");
            errorResponseMap.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseMap);
        }
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerRequestDTO updateRequestDTO) {
        try {
            CustomerResponseDTO customerResponseDTO = customerService.updateCustomer(customerId, updateRequestDTO);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Customer updated successfully");
            responseMap.put("status", HttpStatus.OK.value());
            responseMap.put("data", customerResponseDTO);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Customer not found for update");
            errorResponseMap.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseMap);
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Long customerId) {
        try {
            customerService.deleteCustomer(customerId);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Customer deleted successfully");
            responseMap.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put("message", "Customer not found for deletion");
            errorResponseMap.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseMap);
        }
    }

    @GetMapping // GET /api/customers (at the base path)
    public ResponseEntity<Map<String, Object>> getAllCustomers() {
        List<CustomerResponseDTO> customerResponseDTOList = customerService.getAllCustomers();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "All customers retrieved successfully");
        responseMap.put("status", HttpStatus.OK.value());
        responseMap.put("data", customerResponseDTOList); // Include List<CustomerResponseDTO> as "data"
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

}