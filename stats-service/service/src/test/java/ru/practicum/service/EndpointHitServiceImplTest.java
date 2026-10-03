package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dao.EndpointHitRepository;
import ru.practicum.dto.HitDto;
import jakarta.validation.ValidationException;
import ru.practicum.dto.StatDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EndpointHitServiceImplTest {

    @Mock
    private EndpointHitRepository endpointHitRepository;

    @InjectMocks
    private EndpointHitServiceImpl endpointHitService;

    @Test
    void addHitShouldSaveHit() {
        HitDto hitDto = new HitDto();
        hitDto.setApp("main-service");
        hitDto.setUri("/events/1");
        hitDto.setIp("192.168.0.1");
        hitDto.setTimestamp(
                LocalDateTime.of(2026, 10, 2, 12, 0)
        );

        endpointHitService.addHit(hitDto);

        verify(endpointHitRepository).save(any(EndpointHit.class));
    }

    @Test
    void addHitShouldThrowWhenHitDtoIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> endpointHitService.addHit(null)
        );

        assertEquals(
                "Отсутствует структура с добавляемым запросом hitDTO",
                exception.getMessage()
        );

        verify(endpointHitRepository, never()).save(any());
    }

    @Test
    void getStatsShouldThrowWhenStartIsNull() {
        LocalDateTime end = LocalDateTime.now();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> endpointHitService.getStats(
                        null,
                        end,
                        null,
                        false
                )
        );

        assertEquals(
                "Не указана дата начала диапазона поиска",
                exception.getMessage()
        );

        verifyNoInteractions(endpointHitRepository);
    }

    @Test
    void getStatsShouldThrowWhenEndIsNull() {
        LocalDateTime start = LocalDateTime.now();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> endpointHitService.getStats(
                        start,
                        null,
                        null,
                        false
                )
        );

        assertEquals(
                "Не указана дата окончания диапазона поиска",
                exception.getMessage()
        );

        verifyNoInteractions(endpointHitRepository);
    }

    @Test
    void getStatsShouldThrowWhenStartIsAfterEnd() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> endpointHitService.getStats(
                        start,
                        end,
                        null,
                        false
                )
        );

        assertEquals(
                "Неверное значение даты старта. Оно позже даты окончания",
                exception.getMessage()
        );

        verifyNoInteractions(endpointHitRepository);
    }

    @Test
    void getStatsShouldReturnUniqueStatsWithUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<String> uris = List.of("/events/1");

        List<StatDto> expected = List.of(
                new StatDto("main-service", "/events/1", 5L)
        );

        when(endpointHitRepository
                .getCountHitsByDatetimeWithUrisUniqueIP(start, end, uris))
                .thenReturn(expected);

        List<StatDto> result =
                endpointHitService.getStats(start, end, uris, true);

        assertEquals(expected, result);

        verify(endpointHitRepository)
                .getCountHitsByDatetimeWithUrisUniqueIP(start, end, uris);
    }

    @Test
    void getStatsShouldReturnStatsWithUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<String> uris = List.of("/events/1");

        List<StatDto> expected = List.of(
                new StatDto("main-service", "/events/1", 5L)
        );

        when(endpointHitRepository
                .getCountHitsByDatetimeWithUris(start, end, uris))
                .thenReturn(expected);

        List<StatDto> result =
                endpointHitService.getStats(start, end, uris, false);

        assertEquals(expected, result);

        verify(endpointHitRepository)
                .getCountHitsByDatetimeWithUris(start, end, uris);
    }

    @Test
    void getStatsShouldReturnUniqueStatsWithoutUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<StatDto> expected = List.of(
                new StatDto("main-service", "/events/1", 5L)
        );

        when(endpointHitRepository
                .getCountHitsByDatetimeWithoutUrisUniqueIP(start, end))
                .thenReturn(expected);

        List<StatDto> result =
                endpointHitService.getStats(start, end, null, true);

        assertEquals(expected, result);

        verify(endpointHitRepository)
                .getCountHitsByDatetimeWithoutUrisUniqueIP(start, end);
    }

    @Test
    void getStatsShouldReturnStatsWithoutUris() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<StatDto> expected = List.of(
                new StatDto("main-service", "/events/1", 5L)
        );

        when(endpointHitRepository
                .getCountHitsByDatetimeWithoutUris(start, end))
                .thenReturn(expected);

        List<StatDto> result =
                endpointHitService.getStats(start, end, null, false);

        assertEquals(expected, result);

        verify(endpointHitRepository)
                .getCountHitsByDatetimeWithoutUris(start, end);
    }

    @Test
    void getStatsShouldReturnStatsWithoutUrisWhenUrisIsEmpty() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<String> uris = List.of();

        List<StatDto> expected = List.of(
                new StatDto("main-service", "/events/1", 5L)
        );

        when(endpointHitRepository
                .getCountHitsByDatetimeWithoutUris(start, end))
                .thenReturn(expected);

        List<StatDto> result =
                endpointHitService.getStats(start, end, uris, false);

        assertEquals(expected, result);

        verify(endpointHitRepository)
                .getCountHitsByDatetimeWithoutUris(start, end);
    }
}