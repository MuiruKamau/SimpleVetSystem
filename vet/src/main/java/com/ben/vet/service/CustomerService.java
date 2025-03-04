package com.ben.vet.service;

import com.ben.vet.dto.CustomerRequestDTO;
import com.ben.vet.dto.CustomerResponseDTO;
import com.ben.vet.model.Customer;
import com.ben.vet.repository.CustomerRepository;
import com.ben.vet.service.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService { // Removed "implements com.ben.vet.service.CustomerService"

    private final CustomerRepository customerRepository;
    private final DogService dogService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, DogService dogService) {
        this.customerRepository = customerRepository;
        this.dogService = dogService;
    }



    public CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO) { // Changed to public access
        Customer customer = new Customer();
        customer.setName(customerRequestDTO.getName());
        customer.setPhoneNumber(customerRequestDTO.getPhoneNumber());
        customer.setAddress(customerRequestDTO.getAddress());
        customer.setEmail(customerRequestDTO.getEmail());

        Customer savedCustomer = customerRepository.save(customer);
        return mapToCustomerResponseDTO(savedCustomer);
    }

    public void deactivateCustomer(Long customerId) { // Changed to public access
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        customer.setActive(false);
        customerRepository.save(customer);
        dogService.deactivateDogsByCustomer(customerId);
    }

    public CustomerResponseDTO getCustomerById(Long customerId) { // Changed to public access
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        return mapToCustomerResponseDTO(customer);
    }

    private CustomerResponseDTO mapToCustomerResponseDTO(Customer customer) {
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setCustomerId(customer.getCustomerId());
        responseDTO.setName(customer.getName());
        responseDTO.setActive(customer.isActive());
        responseDTO.setPhoneNumber(customer.getPhoneNumber());
        responseDTO.setAddress(customer.getAddress());
        responseDTO.setEmail(customer.getEmail());
        return responseDTO;
    }

    public CustomerResponseDTO updateCustomer(Long customerId, CustomerRequestDTO updateRequestDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found for update: " + customerId));

        // Update fields if provided in the DTO
        if (updateRequestDTO.getName() != null) {
            customer.setName(updateRequestDTO.getName());
        }
        if (updateRequestDTO.getPhoneNumber() != null) {
            customer.setPhoneNumber(updateRequestDTO.getPhoneNumber());
        }
        if (updateRequestDTO.getAddress() != null) {
            customer.setAddress(updateRequestDTO.getAddress());
        }
        if (updateRequestDTO.getEmail() != null) {
            customer.setEmail(updateRequestDTO.getEmail());
        }
        // isActive is not updatable via this endpoint for simplicity

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToCustomerResponseDTO(updatedCustomer);
    }

    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found for deletion: " + customerId));
        customerRepository.delete(customer);
        // Consider what to do with dogs of a deleted customer - cascade delete or deactivate?
        // For now, assuming cascade or separate dog deactivation logic is handled elsewhere if needed.
    }
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::mapToCustomerResponseDTO)
                .collect(Collectors.toList());
    }
}