package com.ridehailing.modules.ride.service;

import com.ridehailing.modules.ride.dto.RideRequestDTO;
import com.ridehailing.modules.ride.dto.RideResponseDTO;
import com.ridehailing.modules.ride.entity.Ride;
import com.ridehailing.modules.ride.repository.RideRepository;
import com.ridehailing.modules.user.entity.User;
import com.ridehailing.modules.vehicle.entity.VehicleType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {

    // 1. Phép thuật Mockito: Bắt chước (Giả mạo) DB để test không bị phụ thuộc vào
    // PosgreSQL
    @Mock
    private RideRepository rideRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private RidePublisherService ridePublisherService;

    // Lớp cần test sẽ được tiêm các Mock giả ở trên vào
    @InjectMocks
    private RideService rideService;

    @Test
    public void testRequestRide_TinhTienVaKhoangCachChuanXac() {
        // --- BƯỚC A: CHUẨN BỊ DỮ LIỆU ĐẦU VÀO (ARRANGE) ---
        UUID userId = UUID.randomUUID();
        UUID vehicleTypeId = UUID.randomUUID();

        // Giả lập Khách hàng đặt xe từ vị trí X đến Y
        RideRequestDTO request = new RideRequestDTO();
        request.setUserId(userId);
        request.setVehicleTypeId(vehicleTypeId);
        request.setPickupLat(21.028511); // Tọa độ (Vd: Hà Nội)
        request.setPickupLng(105.804817);
        request.setDropoffLat(21.033333); // Điểm đến
        request.setDropoffLng(105.799999);

        // Giả lập Loại xe có giá cước như sau:
        VehicleType mockType = new VehicleType();
        mockType.setBaseFare(BigDecimal.valueOf(15000)); // Mở cửa: 15k
        mockType.setPricePerKm(BigDecimal.valueOf(10000)); // 10k mỗi km

        // Dặn Mockito: Khi Service gọi DB tìm User và VehicleType thì hãy nhả ra cục
        // Mock này
        when(entityManager.getReference(User.class, userId)).thenReturn(new User());
        when(entityManager.getReference(VehicleType.class, vehicleTypeId)).thenReturn(mockType);

        // Dặn Mockito: Khi nào lưu cuốc xe (save), nhả lại đúng cuốc đó (có ID)
        when(rideRepository.save(any(Ride.class))).thenAnswer(i -> {
            Ride ride = i.getArgument(0);
            ride.setId(UUID.randomUUID());
            return ride;
        });

        // --- BƯỚC B: CHẠY THỰC TẾ HÀM (ACT) ---
        RideResponseDTO response = rideService.requestRide(request);

        // --- BƯỚC C: KIỂM TRA TRẠNG THÁI (ASSERT) ---
        assertNotNull(response.getRideId(), "Cuốc xe phải được tạo thành công có chứa ID");
        assertEquals("REQUESTED", response.getStatus(), "Trạng thái cuốc phải là REQUESTED");
        System.out.println("Kết quả ước tính tiền: " + response.getActualFare() + " VNĐ");
        // 1. Máy tính Casio nhẩm tính: 15k mở cửa + (~0.735km * 10k/km) = Khoảng 22350
        // VNĐ
        // Vì số thập phân có thể xê dịch, mình sẽ kiểm tra xem tiền cước có CỤ THỂ
        // rơi vào khoảng 22.000đ đến 23.000đ không.

        BigDecimal actualFare = response.getActualFare();

        boolean isFareCorrect = actualFare.compareTo(new BigDecimal("22000")) > 0 &&
                actualFare.compareTo(new BigDecimal("23000")) < 0;

        // Triệu hồi Thẩm Phán: "Tiền cước CÓ NẰM TRONG KHOẢNG ĐÓ KHÔNG (TRUE/FALSE)?"
        assertEquals(true, isFareCorrect, "TIỀN CƯỚC TÍNH SAI BÉT RỒI SẾP ƠI!");

        System.out.println("Khoảng cách ước tính: " + response.getEstimatedDistance() + " KM");
    }
}
