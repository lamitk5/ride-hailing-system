package com.ridehailing.modules.payment.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequest {
    private UUID rideId;
    private String paymentMethod; // CASH, CREDIT_CARD, VNPAY
    private BigDecimal amount;
}
