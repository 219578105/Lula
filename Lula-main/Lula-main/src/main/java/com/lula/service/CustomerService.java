package com.lula.service;

import com.lula.dto.CustomerRequest;
import com.lula.dto.CustomerResponse;
import com.lula.entity.Business;
import com.lula.entity.Customer;
import com.lula.repository.CustomerRepository;
import com.lula.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BusinessService businessService;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.ZERO)
                .balance(BigDecimal.ZERO)
                .address(request.getAddress())
                .notes(request.getNotes())
                .business(business)
                .build();

        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    public List<CustomerResponse> getMyCustomers(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return customerRepository.findByBusinessId(business.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public CustomerResponse getCustomer(Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (!customer.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (!customer.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setNotes(request.getNotes());
        if (request.getCreditLimit() != null) {
            customer.setCreditLimit(request.getCreditLimit());
        }

        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse adjustBalance(Long id, BigDecimal amount, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (!customer.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        BigDecimal newBalance = customer.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            newBalance = BigDecimal.ZERO;
        }
        customer.setBalance(newBalance);
        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Transactional
    public void deleteCustomer(Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (!customer.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        customerRepository.delete(customer);
    }

    public List<CustomerResponse> getOverLimitCustomers(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return customerRepository.findOverLimitCustomers(business.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private CustomerResponse mapToResponse(Customer c) {
        boolean overLimit = c.getCreditLimit() != null && c.getCreditLimit().compareTo(BigDecimal.ZERO) > 0
                && c.getBalance().compareTo(c.getCreditLimit()) >= 0;

        return CustomerResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .creditLimit(c.getCreditLimit())
                .balance(c.getBalance())
                .address(c.getAddress())
                .notes(c.getNotes())
                .businessId(c.getBusiness() != null ? c.getBusiness().getId() : null)
                .businessName(c.getBusiness() != null ? c.getBusiness().getBizName() : null)
                .overLimit(overLimit)
                .build();
    }
}
