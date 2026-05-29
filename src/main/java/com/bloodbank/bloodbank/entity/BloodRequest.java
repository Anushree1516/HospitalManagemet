package com.bloodbank.bloodbank.entity;

import com.bloodbank.bloodbank.enums.BloodGroup;
import com.bloodbank.bloodbank.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who is requesting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    // Which hospital to fulfill from
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false)
    private BloodGroup bloodGroup;

    @Column(name = "units_required", nullable = false)
    private Integer unitsRequired;

    @Column(name = "patient_name", length = 150)
    private String patientName;

    @Column(name = "patient_age")
    private Integer patientAge;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    // Location of patient for ambulance integration
    @Column(name = "patient_latitude")
    private Double patientLatitude;

    @Column(name = "patient_longitude")
    private Double patientLongitude;

    @Column(name = "patient_address", length = 500)
    private String patientAddress;

    @Column(name = "required_by")
    private LocalDateTime requiredBy;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
