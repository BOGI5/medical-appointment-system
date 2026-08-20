package com.medical.appointments.timeslot;

import com.medical.appointments.database.DbNames;
import com.medical.appointments.profiles.doctor.DoctorProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = DbNames.TIME_SLOTS)
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DbNames.DOCTOR_ID, nullable = false)
    private DoctorProfile doctor;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    public TimeSlot(DoctorProfile doctor, LocalDateTime startAt, LocalDateTime endAt) {
        if (doctor == null) {
            throw new IllegalArgumentException("doctor cannot be null");
        }
        if (startAt == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endAt == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }

        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        this.doctor = doctor;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
