package com.lula.service;

import com.lula.dto.CreditRequestDto;
import com.lula.dto.CreditRequestResponse;
import com.lula.entity.Business;
import com.lula.entity.CreditRequest;
import com.lula.entity.Customer;
import com.lula.enums.CreditStatus;
import com.lula.repository.BusinessRepository;
import com.lula.repository.CreditRequestRepository;
import com.lula.repository.CustomerRepository;
import com.lula.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditRequestService {

    private final CreditRequestRepository creditRequestRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final BusinessService businessService;

    @Transactional
    public CreditRequestResponse createCreditRequest(CreditRequestDto request, String customerEmail, String customerName) {
        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        Customer customer = customerRepository.findByEmailAndBusinessId(customerEmail, business.getId())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .name(customerName)
                            .email(customerEmail)
                            .business(business)
                            .build();
                    return customerRepository.save(newCustomer);
                });

        CreditRequest cr = CreditRequest.builder()
                .item(request.getItem())
                .amount(request.getAmount())
                .note(request.getNote())
                .status(CreditStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .business(business)
                .customer(customer)
                .build();

        cr = creditRequestRepository.save(cr);
        return mapToResponse(cr);
    }

    public List<CreditRequestResponse> getBusinessCreditRequests(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return creditRequestRepository.findByBusinessIdOrderByRequestedAtDesc(business.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<CreditRequestResponse> getCustomerCreditRequests(String customerEmail) {
        return creditRequestRepository.findByCustomerEmailOrderByRequestedAtDesc(customerEmail)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public CreditRequestResponse handleCreditRequest(Long id, String status, String responseNote, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        CreditRequest cr = creditRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credit request not found"));

        if (!cr.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        CreditStatus newStatus = CreditStatus.valueOf(status.toUpperCase());
        cr.setStatus(newStatus);
        cr.setResponseNote(responseNote);
        cr.setRespondedAt(LocalDateTime.now());

        if (newStatus == CreditStatus.APPROVED) {
            Customer customer = cr.getCustomer();
            customer.setBalance(customer.getBalance().add(cr.getAmount()));
            customerRepository.save(customer);
        }

        cr = creditRequestRepository.save(cr);
        return mapToResponse(cr);
    }

    public long countPendingRequests(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return creditRequestRepository.countByBusinessIdAndStatus(business.getId(), CreditStatus.PENDING);
    }

    private CreditRequestResponse mapToResponse(CreditRequest cr) {
        return CreditRequestResponse.builder()
                .id(cr.getId())
                .item(cr.getItem())
                .amount(cr.getAmount())
                .note(cr.getNote())
                .status(cr.getStatus().name())
                .requestedAt(cr.getRequestedAt())
                .respondedAt(cr.getRespondedAt())
                .responseNote(cr.getResponseNote())
                .customerName(cr.getCustomer() != null ? cr.getCustomer().getName() : null)
                .customerEmail(cr.getCustomer() != null ? cr.getCustomer().getEmail() : null)
                .businessName(cr.getBusiness() != null ? cr.getBusiness().getBizName() : null)
                .businessId(cr.getBusiness() != null ? cr.getBusiness().getId() : null)
                .build();
    }
}
