package com.ridehailing.modules.payment.service;

import com.ridehailing.common.exception.BadRequestException;
import com.ridehailing.common.exception.ResourceNotFoundException;
import com.ridehailing.modules.payment.dto.PaymentRequest;
import com.ridehailing.modules.payment.entity.Payment;
import com.ridehailing.modules.payment.repository.PaymentRepository;
import com.ridehailing.modules.ride.entity.Ride;
import com.ridehailing.modules.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi."));

        if (!"COMPLETED".equals(ride.getStatus())) {
            throw new BadRequestException("Chuyến đi chưa hoàn thành, không thể thanh toán.");
        }

        if (paymentRepository.findByRideId(request.getRideId()).isPresent()) {
            throw new BadRequestException("Chuyến đi này đã được thanh toán.");
        }

        // Ở môi trường thực tế, phần này sẽ gọi API VNPAY/Momo để lấy URL thanh toán.
        String transactionRef = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .ride(ride)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status("SUCCESS")
                .transactionRef(transactionRef)
                .build();

        return paymentRepository.save(payment);
    }
}
