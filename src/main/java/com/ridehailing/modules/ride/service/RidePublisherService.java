package com.ridehailing.modules.ride.service;

import com.ridehailing.modules.ride.event.RideRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RidePublisherService {

    // Thư viện RedisTemplate đã cấu hình sẵn trong dự án của em
    private final RedisTemplate<String, Object> redisTemplate;

    // Tên Kênh Sóng (Topic) trên Redis
    private static final String RIDE_TOPIC = "ride-requests-topic";

    // Hàm thực hiện hành động "Hét lên thông báo có khách"
    public void broadcastNewRide(RideRequestEvent event) {
        log.info("📢 Đang phát sóng tìm tài xế cho cuốc xe ID: {}...", event.getRideId());
        redisTemplate.convertAndSend(RIDE_TOPIC, event);
    }
}
