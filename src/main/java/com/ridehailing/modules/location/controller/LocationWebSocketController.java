package com.ridehailing.modules.location.controller;

import com.ridehailing.modules.location.service.DriverLocationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LocationWebSocketController {

    private final DriverLocationService locationService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * API WebSocket: Client (Tài xế) đẩy message lên qua topic "/app/driver/location"
     */
    @MessageMapping("/driver/location")
    public void updateLocation(@Payload LocationPayload payload) {
        // 1. Lưu ngay vào Redis cache thay vì Database để tránh quá tải DB
        locationService.updateDriverLocation(payload.getDriverId(), payload.getLng(), payload.getLat());
        
        // 2. Nếu tài xế đang chạy 1 chuyến xe (rideId != null), lập tức phát sóng về cho người dùng qua WebSockets
        if (payload.getRideId() != null) {
            String destinationTopic = "/topic/ride/" + payload.getRideId();
            messagingTemplate.convertAndSend(destinationTopic, payload);
            log.debug("Broadcasted real-time driver {} location to ride {}", payload.getDriverId(), payload.getRideId());
        }
    }

    @Data
    public static class LocationPayload {
        private String driverId;
        private String rideId; // Nullable (nếu rảnh không chạy chuyến, chỉ cập nhật vị trí)
        private double lat;
        private double lng;
    }
}
