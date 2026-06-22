package com.lula.service;

import com.lula.dto.PaymentRequest;
import com.lula.dto.PaymentResponse;
import com.lula.entity.Business;
import com.lula.entity.Customer;
import com.lula.entity.Payment;
import com.lula.enums.PaymentMethod;
import com.lula.enums.PaymentStatus;
import com.lula.repository.BusinessRepository;
import com.lula.repository.CustomerRepository;
import com.lula.repository.PaymentRepository;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final BusinessService businessService;

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request, String customerEmail, String customerName) {
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

        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .method(PaymentMethod.valueOf(request.getMethod().toUpperCase()))
                .status(PaymentStatus.PENDING_CONFIRMATION)
                .paidAt(LocalDateTime.now())
                .notes(request.getNotes())
                .business(business)
                .customer(customer)
                .build();

        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    public List<PaymentResponse> getBusinessPayments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());
        return paymentRepository.findByBusinessIdOrderByPaidAtDesc(business.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<PaymentResponse> getCustomerPayments(String customerEmail) {
        return paymentRepository.findByCustomerEmailOrderByPaidAtDesc(customerEmail)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse confirmPayment(Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Business business = businessService.getBusinessByOwner(userDetails.getId());

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getBusiness().getId().equals(business.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setConfirmedAt(LocalDateTime.now());

        Customer customer = payment.getCustomer();
        customer.setBalance(customer.getBalance().subtract(payment.getAmount()));
        if (customer.getBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
            customer.setBalance(java.math.BigDecimal.ZERO);
        }
        customerRepository.save(customer);

        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .amount(p.getAmount())
                .method(p.getMethod().name())
                .status(p.getStatus().name())
                .paidAt(p.getPaidAt())
                .confirmedAt(p.getConfirmedAt())
                .notes(p.getNotes())
                .customerName(p.getCustomer() != null ? p.getCustomer().getName() : null)
                .businessName(p.getBusiness() != null ? p.getBusiness().getBizName() : null)
                .build();
    }
}
