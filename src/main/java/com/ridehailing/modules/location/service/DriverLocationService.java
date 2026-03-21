package com.ridehailing.modules.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverLocationService {

    private final StringRedisTemplate redisTemplate;
    
    // Key danh sách tọa độ của tất cả tài xế đang OLINE
    private static final String DRIVER_GEO_KEY = "drivers_location";

    // 1. Cập nhật vị trí tài xế bằng lệnh GEOADD của Redis
    public void updateDriverLocation(String driverId, double longitude, double latitude) {
        redisTemplate.opsForGeo().add(DRIVER_GEO_KEY, new Point(longitude, latitude), driverId);
        log.debug("Cập nhật vị trí tài xế trên Redis: {} -> {}, {}", driverId, longitude, latitude);
    }

    // 2. Tìm danh sách tài xế gần nhất trong bán kính N km (GEORADIUS)
    public List<String> findNearbyDrivers(double pickupLng, double pickupLat, double radiusInKm) {
        Distance radius = new Distance(radiusInKm, Metrics.KILOMETERS);
        Circle circle = new Circle(new Point(pickupLng, pickupLat), radius);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending() // Order drivers nearest first
                .limit(5);       // Find Top 5 nearest

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(DRIVER_GEO_KEY, circle, args);

        if (results == null) return List.of();

        return results.getContent().stream()
                .map(result -> result.getContent().getName())
                .collect(Collectors.toList());
    }

    // 3. Xóa tài xế khỏi bản đồ Redis khi Offline / Ngắt kết nối
    public void removeDriverLocation(String driverId) {
        redisTemplate.opsForGeo().remove(DRIVER_GEO_KEY, driverId);
    }
}
