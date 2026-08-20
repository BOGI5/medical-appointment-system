package com.medical.appointments.timeslot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    @Query("""
        SELECT COUNT(ts) > 0
        FROM TimeSlot ts
        WHERE ts.doctor.id = :doctorId
            AND ts.startAt < :endAt
            AND ts.endAt > :startAt
""")
    boolean existsOverlappingTimeSlots(
            @Param("doctorId") Long doctorId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
        SELECT ts
        FROM TimeSlot ts
        WHERE ts.startAt >= :startAt
        ORDER BY ts.startAt ASC
""")
    List<TimeSlot> findByStartAt(@Param("startAt") LocalDateTime startAt);

    @Query("""
        SELECT ts
        FROM TimeSlot ts
        WHERE ts.startAt >= :startAt
            AND ts.endAt <= :endAt
        ORDER BY ts.startAt ASC
""")
    List<TimeSlot> findByTimeRange(
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
        SELECT ts
        FROM TimeSlot ts
        WHERE ts.doctor.id = :doctorId
            AND ts.startAt >= :startAt
        ORDER BY ts.startAt ASC
""")
    List<TimeSlot> findByDoctorIdAndStartAt(
            @Param("doctorId") Long doctorId,
            @Param("startAt") LocalDateTime startAt
    );


    @Query("""
        SELECT ts
        FROM TimeSlot ts
        WHERE ts.doctor.id = :doctorId
            AND ts.startAt >= :startAt
            AND ts.endAt <= :endAt
        ORDER BY ts.startAt ASC
""")
    List<TimeSlot> findByDoctorIdAndTimeRange(
            @Param("doctorId") Long doctorId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
        SELECT ts
        FROM TimeSlot ts
        WHERE ts.doctor.specialization.id = :specializationId
            AND ts.startAt >= :startAt
        ORDER BY ts.startAt ASC
""")
    List<TimeSlot> findBySpecializationIdAndStartAt(
            @Param("specializationId") Long specializationId,
            @Param("startAt") LocalDateTime startAt
    );

    @Query("""
        SELECT ts
        FROM TimeSlot ts
        WHERE ts.doctor.specialization.id = :specializationId
            AND ts.startAt >= :startAt
            AND ts.endAt <= :endAt
        ORDER BY ts.startAt ASC
""")
    List<TimeSlot> findBySpecializationIdAndTimeRange(
            @Param("specializationId") Long specializationId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}
