package com.bloodbank.bloodbank.entity;

import com.bloodbank.bloodbank.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_expiry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodExpiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false)
    private BloodGroup bloodGroup;

    @Column(name = "units", nullable = false)
    private Integer units;

    @Column(name = "collection_date")
    private LocalDate collectionDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    // Blood expires in 42 days (standard)
    @Column(name = "bag_number", length = 50)
    private String bagNumber;

    @Column(name = "donor_name", length = 100)
    private String donorName;

    @Column(name = "is_expired")
    @Builder.Default
    private Boolean isExpired = false;

    @Column(name = "is_discarded")
    @Builder.Default
    private Boolean isDiscarded = false;

    @Column(name = "discarded_at")
    private LocalDateTime discardedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
