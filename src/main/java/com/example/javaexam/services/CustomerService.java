package com.example.javaexam.services;

import com.example.javaexam.dtos.CustomerRequest;
import com.example.javaexam.dtos.CustomerResponse;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.Customer;
import com.example.javaexam.models.enums.Status;
import com.example.javaexam.repositories.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Customer management (Task 2): registration with duplicate prevention and status control. */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        String email = request.email().trim().toLowerCase();
        if (customerRepository.existsByNationalId(request.nationalId())) {
            throw ApiException.conflict("A customer with National ID '" + request.nationalId() + "' already exists");
        }
        if (customerRepository.existsByEmail(email)) {
            throw ApiException.conflict("A customer with email '" + email + "' already exists");
        }

        Customer customer = Customer.builder()
                .fullName(request.fullName().trim())
                .nationalId(request.nationalId())
                .email(email)
                .phoneNumber(request.phoneNumber().trim())
                .address(request.address() == null ? null : request.address().trim())
                .status(Status.ACTIVE)
                .build();
        customerRepository.save(customer);

        log.info("Registered customer {} (National ID {})", email, request.nationalId());
        return CustomerResponse.from(customer);
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getEntity(id);
        String email = request.email().trim().toLowerCase();

        if (!customer.getNationalId().equals(request.nationalId())
                && customerRepository.existsByNationalId(request.nationalId())) {
            throw ApiException.conflict("A customer with National ID '" + request.nationalId() + "' already exists");
        }
        if (!customer.getEmail().equals(email) && customerRepository.existsByEmail(email)) {
            throw ApiException.conflict("A customer with email '" + email + "' already exists");
        }

        customer.setFullName(request.fullName().trim());
        customer.setNationalId(request.nationalId());
        customer.setEmail(email);
        customer.setPhoneNumber(request.phoneNumber().trim());
        customer.setAddress(request.address() == null ? null : request.address().trim());
        customerRepository.save(customer);
        return CustomerResponse.from(customer);
    }

    @Transactional
    public CustomerResponse updateStatus(Long id, Status status) {
        Customer customer = getEntity(id);
        customer.setStatus(status);
        customerRepository.save(customer);
        log.info("Customer {} status set to {}", id, status);
        return CustomerResponse.from(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> list() {
        return customerRepository.findAll().stream().map(CustomerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        return CustomerResponse.from(getEntity(id));
    }

    /** Loads a customer or throws 404. Shared with other services. */
    public Customer getEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Customer not found: " + id));
    }
}
