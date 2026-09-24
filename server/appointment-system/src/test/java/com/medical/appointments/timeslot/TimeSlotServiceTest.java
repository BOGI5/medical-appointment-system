package com.medical.appointments.timeslot;

import com.medical.appointments.config.properties.TimeSlotProperties;
import com.medical.appointments.exception.InvalidTimeOrderException;
import com.medical.appointments.exception.NoAccessToTimeSlotException;
import com.medical.appointments.exception.ProfileNotFoundException;
import com.medical.appointments.exception.TimeRangeNotDivisibleException;
import com.medical.appointments.exception.TimeSlotNotFoundException;
import com.medical.appointments.exception.TimeSlotRangeAlreadyExistsException;
import com.medical.appointments.exception.TimeSlotRangeInPastException;
import com.medical.appointments.profiles.doctor.DoctorProfile;
import com.medical.appointments.profiles.doctor.DoctorProfileService;
import com.medical.appointments.timeslot.dto.CreateTimeSlotsRequest;
import com.medical.appointments.timeslot.dto.TimeSlotResponse;
import com.medical.appointments.timeslot.mapper.TimeSlotMapper;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceTest {

    private static final Long DOCTOR_ID = 101L;
    private static final Long SPECIALIZATION_ID = 201L;
    private static final Long SLOT_ID = 301L;
    private static final int SLOT_DURATION = 15;
    private static final LocalDateTime NOW = LocalDateTime.of(2030, 1, 15, 12, 0);
    private static final LocalDateTime START_AT = LocalDateTime.of(2030, 1, 16, 9, 0);
    private static final LocalDateTime END_AT = START_AT.plusHours(1);

    @Mock private TimeSlotMapper mapper;
    @Mock private TimeSlotRepository repository;
    @Mock private DoctorProfileService doctorProfileService;

    @Captor
    private ArgumentCaptor<List<TimeSlot>> slotsCaptor;

    private TimeSlotService service;
    private MockedStatic<LocalDateTime> currentTime;
    private User doctorUser;
    private DoctorProfile doctor;

    @BeforeEach
    void setup() {
        service = new TimeSlotService(
                mapper, repository, new TimeSlotProperties(SLOT_DURATION), doctorProfileService
        );
        doctorUser = createUser(DOCTOR_ID, Role.DOCTOR);
        doctor = new DoctorProfile(doctorUser);
        ReflectionTestUtils.setField(doctor, "id", DOCTOR_ID);

        // Freeze now() without changing date arithmetic or the production service.
        currentTime = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS);
        currentTime.when(LocalDateTime::now).thenReturn(NOW);
    }

    @AfterEach
    void tearDown() {
        currentTime.close();
    }

    // ===== CREATE =====

    @ParameterizedTest
    @CsvSource({"15, 1", "20, 3"})
    void createSlots_savesConsecutiveSlotsWithConfiguredDuration(int duration, int slotCount) {
        service = new TimeSlotService(
                mapper, repository, new TimeSlotProperties(duration), doctorProfileService
        );
        LocalDateTime endAt = START_AT.plusMinutes((long) duration * slotCount);
        when(doctorProfileService.findEntityById(DOCTOR_ID)).thenReturn(doctor);
        when(mapper.toResponse(any(TimeSlot.class)))
                .thenAnswer(invocation -> responseFor(invocation.getArgument(0)));

        List<TimeSlotResponse> result = service.createSlots(createRequest(START_AT, endAt), doctorUser);

        verify(repository).existsOverlappingTimeSlots(DOCTOR_ID, START_AT, endAt);
        verify(repository).saveAll(slotsCaptor.capture());
        List<TimeSlot> savedSlots = slotsCaptor.getValue();
        assertEquals(slotCount, savedSlots.size());
        for (int index = 0; index < slotCount; index++) {
            TimeSlot slot = savedSlots.get(index);
            LocalDateTime expectedStart = START_AT.plusMinutes((long) duration * index);
            assertSame(doctor, slot.getDoctor());
            assertEquals(expectedStart, slot.getStartAt());
            assertEquals(expectedStart.plusMinutes(duration), slot.getEndAt());
        }
        assertEquals(endAt, savedSlots.getLast().getEndAt());
        assertEquals(savedSlots.stream().map(this::responseFor).toList(), result);
    }

    @ParameterizedTest
    @MethodSource("invalidCreationRanges")
    void createSlots_invalidTimeRange(
            LocalDateTime startAt, LocalDateTime endAt, Class<? extends RuntimeException> exception
    ) {
        assertThrows(exception, () -> service.createSlots(createRequest(startAt, endAt), doctorUser));

        verifyNoInteractions(repository);
    }

    @Test
    void createSlots_durationNotDivisible() {
        CreateTimeSlotsRequest request = createRequest(START_AT, START_AT.plusMinutes(16));

        assertThrows(TimeRangeNotDivisibleException.class, () -> service.createSlots(request, doctorUser));

        verifyNoInteractions(repository);
    }

    @Test
    void createSlots_partialMinuteIsNotDivisible() {
        CreateTimeSlotsRequest request = createRequest(
                START_AT, START_AT.plusMinutes(SLOT_DURATION).plusSeconds(30)
        );

        assertThrows(TimeRangeNotDivisibleException.class, () -> service.createSlots(request, doctorUser));

        verifyNoInteractions(repository);
    }

    @Test
    void createSlots_overlappingRange() {
        when(repository.existsOverlappingTimeSlots(DOCTOR_ID, START_AT, END_AT)).thenReturn(true);

        assertThrows(TimeSlotRangeAlreadyExistsException.class,
                () -> service.createSlots(createRequest(START_AT, END_AT), doctorUser));

        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void createSlots_doctorNotFound() {
        when(doctorProfileService.findEntityById(DOCTOR_ID)).thenThrow(new ProfileNotFoundException());

        assertThrows(ProfileNotFoundException.class,
                () -> service.createSlots(createRequest(START_AT, END_AT), doctorUser));

        verify(repository, never()).saveAll(anyList());
    }

    // ===== FIND =====

    @Test
    void findById_success() {
        TimeSlot slot = createSlot(START_AT);
        TimeSlotResponse response = responseFor(slot);
        when(repository.findById(SLOT_ID)).thenReturn(Optional.of(slot));
        when(mapper.toResponse(slot)).thenReturn(response);

        assertSame(response, service.findById(SLOT_ID));
    }

    @Test
    void findById_notFound() {
        when(repository.findById(SLOT_ID)).thenReturn(Optional.empty());

        assertThrows(TimeSlotNotFoundException.class, () -> service.findById(SLOT_ID));
    }

    @Test
    void findById_alreadyStarted() {
        when(repository.findById(SLOT_ID)).thenReturn(Optional.of(createSlot(NOW.minusMinutes(1))));

        assertThrows(TimeSlotNotFoundException.class, () -> service.findById(SLOT_ID));
    }

    @ParameterizedTest
    @MethodSource("validSearchRanges")
    void findAll_usesRequestedRangeAndReturnsResponses(
            LocalDateTime startAt, LocalDateTime endAt, LocalDateTime expectedStart
    ) {
        TimeSlot slot = createSlot(START_AT);
        TimeSlotResponse response = responseFor(slot);
        when(mapper.toResponse(slot)).thenReturn(response);
        if (endAt == null) {
            when(repository.findByStartAt(expectedStart)).thenReturn(List.of(slot));
        } else {
            when(repository.findByTimeRange(expectedStart, endAt)).thenReturn(List.of(slot));
        }

        assertEquals(List.of(response), service.findAll(startAt, endAt));
    }

    @Test
    void findAll_noSlots() {
        when(repository.findByStartAt(START_AT)).thenReturn(List.of());

        assertTrue(service.findAll(START_AT, null).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidSearchRanges")
    void findAll_invalidTimeRange(
            LocalDateTime startAt, LocalDateTime endAt, Class<? extends RuntimeException> exception
    ) {
        assertThrows(exception, () -> service.findAll(startAt, endAt));

        verifyNoInteractions(repository);
    }

    @Test
    void findByDoctorId_withTimeRange() {
        service.findByDoctorId(DOCTOR_ID, START_AT, END_AT);

        verify(repository).findByDoctorIdAndTimeRange(DOCTOR_ID, START_AT, END_AT);
    }

    @Test
    void findByDoctorId_withoutTimeRange() {
        service.findByDoctorId(DOCTOR_ID, null, null);

        verify(repository).findByDoctorIdAndStartAt(DOCTOR_ID, NOW);
    }

    @Test
    void findByDoctorId_invalidTimeRange() {
        assertThrows(InvalidTimeOrderException.class,
                () -> service.findByDoctorId(DOCTOR_ID, START_AT, START_AT));

        verifyNoInteractions(repository);
    }

    @Test
    void findBySpecializationId_withTimeRange() {
        service.findBySpecializationId(SPECIALIZATION_ID, START_AT, END_AT);

        verify(repository).findBySpecializationIdAndTimeRange(SPECIALIZATION_ID, START_AT, END_AT);
    }

    @Test
    void findBySpecializationId_withoutTimeRange() {
        service.findBySpecializationId(SPECIALIZATION_ID, null, null);

        verify(repository).findBySpecializationIdAndStartAt(SPECIALIZATION_ID, NOW);
    }

    @Test
    void findBySpecializationId_invalidTimeRange() {
        assertThrows(InvalidTimeOrderException.class,
                () -> service.findBySpecializationId(SPECIALIZATION_ID, START_AT, START_AT));

        verifyNoInteractions(repository);
    }

    // ===== DELETE =====

    @Test
    void deleteById_ownSlot() {
        when(repository.findById(SLOT_ID)).thenReturn(Optional.of(createSlot(START_AT)));

        service.deleteById(SLOT_ID, doctorUser);

        verify(repository).deleteById(SLOT_ID);
    }

    @Test
    void deleteById_adminCanDeleteAnotherDoctorsSlot() {
        User admin = createUser(102L, Role.ADMIN);
        when(repository.findById(SLOT_ID)).thenReturn(Optional.of(createSlot(START_AT)));

        service.deleteById(SLOT_ID, admin);

        verify(repository).deleteById(SLOT_ID);
    }

    @Test
    void deleteById_anotherDoctorHasNoAccess() {
        User anotherDoctor = createUser(102L, Role.DOCTOR);
        when(repository.findById(SLOT_ID)).thenReturn(Optional.of(createSlot(START_AT)));

        assertThrows(NoAccessToTimeSlotException.class, () -> service.deleteById(SLOT_ID, anotherDoctor));

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_notFound() {
        when(repository.findById(SLOT_ID)).thenReturn(Optional.empty());

        assertThrows(TimeSlotNotFoundException.class, () -> service.deleteById(SLOT_ID, doctorUser));

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deleteByTimeRange_deletesFoundSlotsForIssuer() {
        List<TimeSlot> slots = List.of(
                new TimeSlot(doctor, START_AT, START_AT.plusMinutes(SLOT_DURATION)),
                new TimeSlot(doctor, START_AT.plusMinutes(SLOT_DURATION), START_AT.plusMinutes(2 * SLOT_DURATION))
        );
        when(repository.findByDoctorIdAndTimeRange(DOCTOR_ID, START_AT, END_AT)).thenReturn(slots);

        service.deleteByTimeRange(START_AT, END_AT, doctorUser);

        verify(repository).deleteAll(slots);
    }

    @Test
    void deleteByTimeRange_noSlots() {
        when(repository.findByDoctorIdAndTimeRange(DOCTOR_ID, START_AT, END_AT)).thenReturn(List.of());

        assertDoesNotThrow(() -> service.deleteByTimeRange(START_AT, END_AT, doctorUser));
    }

    @Test
    void deleteByTimeRange_invalidTimeRange() {
        assertThrows(InvalidTimeOrderException.class,
                () -> service.deleteByTimeRange(START_AT, START_AT, doctorUser));

        verifyNoInteractions(repository);
    }

    // ===== HELPERS =====

    private static Stream<Arguments> validSearchRanges() {
        return Stream.of(
                Arguments.of(START_AT, END_AT, START_AT),
                Arguments.of(START_AT, null, START_AT),
                Arguments.of(null, END_AT, NOW),
                Arguments.of(null, null, NOW)
        );
    }

    private static Stream<Arguments> invalidCreationRanges() {
        return Stream.of(
                Arguments.of(START_AT, START_AT.minusMinutes(1), InvalidTimeOrderException.class),
                Arguments.of(START_AT, START_AT, InvalidTimeOrderException.class),
                Arguments.of(NOW.minusMinutes(1), NOW.plusHours(1), TimeSlotRangeInPastException.class)
        );
    }

    private static Stream<Arguments> invalidSearchRanges() {
        return Stream.of(
                Arguments.of(START_AT, START_AT, InvalidTimeOrderException.class),
                Arguments.of(NOW.minusMinutes(1), END_AT, TimeSlotRangeInPastException.class),
                Arguments.of(null, NOW, InvalidTimeOrderException.class),
                Arguments.of(NOW.minusMinutes(1), null, TimeSlotRangeInPastException.class)
        );
    }

    private CreateTimeSlotsRequest createRequest(LocalDateTime startAt, LocalDateTime endAt) {
        return new CreateTimeSlotsRequest(startAt.toLocalDate(), startAt.toLocalTime(), endAt.toLocalTime());
    }

    private User createUser(Long id, Role role) {
        User user = User.builder()
                .email("user" + id + "@example.com")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(role))
                .activeRole(role)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private TimeSlot createSlot(LocalDateTime startAt) {
        TimeSlot slot = new TimeSlot(doctor, startAt, startAt.plusMinutes(SLOT_DURATION));
        ReflectionTestUtils.setField(slot, "id", SLOT_ID);
        return slot;
    }

    private TimeSlotResponse responseFor(TimeSlot slot) {
        return new TimeSlotResponse(slot.getId(), slot.getStartAt(), slot.getEndAt(), null);
    }
}
