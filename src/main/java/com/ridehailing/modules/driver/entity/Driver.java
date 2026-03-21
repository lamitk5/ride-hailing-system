package com.ridehailing.modules.driver.entity;

import com.ridehailing.common.entity.BaseEntity;
import com.ridehailing.modules.account.entity.Account;
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
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends BaseEntity {

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "identity_card", length = 20, unique = true, nullable = false)
    private String identityCard;

    @Column(name = "status", length = 20)
    private String status; // OFFLINE, AVAILABLE, ON_RIDE

    @Column(name = "last_known_location", columnDefinition = "Geometry(Point,4326)")
    private Point lastKnownLocation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true)
    private Account account;
}
