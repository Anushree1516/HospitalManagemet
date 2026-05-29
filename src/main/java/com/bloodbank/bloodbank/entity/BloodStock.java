package com.bloodbank.bloodbank.entity;

import com.bloodbank.bloodbank.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_stocks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hospital_id", "blood_group"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false)
    private BloodGroup bloodGroup;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "units_available")
    private Integer unitsAvailable;

    // Expiry tracking for blood units
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
