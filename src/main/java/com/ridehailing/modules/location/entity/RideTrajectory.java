package com.ridehailing.modules.location.entity;

import com.ridehailing.common.entity.BaseEntity;
import com.ridehailing.modules.ride.entity.Ride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "ride_trajectories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideTrajectory extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", unique = true, nullable = false)
    private Ride ride;

    @Column(name = "route_path", columnDefinition = "Geometry(LineString,4326)")
    private LineString routePath;
}
