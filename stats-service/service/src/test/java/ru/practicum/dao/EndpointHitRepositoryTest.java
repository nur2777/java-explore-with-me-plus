package ru.practicum.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.dto.StatDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class EndpointHitRepositoryTest {

    @Autowired
    private EndpointHitRepository endpointHitRepository;


    @Test
    void shouldCountHitsWithoutUris() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 3, 0, 0);

        EndpointHit hit1 = new EndpointHit();
        hit1.setApp("main-service");
        hit1.setUri("/events/1");
        hit1.setIp("192.168.0.1");
        hit1.setHitDatetime(LocalDateTime.of(2026, 10, 2, 10, 0));

        EndpointHit hit2 = new EndpointHit();
        hit2.setApp("main-service");
        hit2.setUri("/events/1");
        hit2.setIp("192.168.0.2");
        hit2.setHitDatetime(LocalDateTime.of(2026, 10, 2, 11, 0));

        EndpointHit hit3 = new EndpointHit();
        hit3.setApp("main-service");
        hit3.setUri("/events/1");
        hit3.setIp("192.168.0.1");
        hit3.setHitDatetime(LocalDateTime.of(2026, 10, 2, 12, 0));

        endpointHitRepository.saveAll(
                List.of(hit1, hit2, hit3)
        );

        List<StatDto> result =
                endpointHitRepository.getCountHitsByDatetimeWithoutUris(
                        start,
                        end
                );

        assertEquals(1, result.size());

        StatDto stat = result.getFirst();

        assertEquals("main-service", stat.getApp());
        assertEquals("/events/1", stat.getUri());
        assertEquals(3L, stat.getHits());
    }

    @Test
    void shouldCountUniqueHitsWithoutUris() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 3, 0, 0);

        EndpointHit hit1 = new EndpointHit();
        hit1.setApp("main-service");
        hit1.setUri("/events/1");
        hit1.setIp("192.168.0.1");
        hit1.setHitDatetime(LocalDateTime.of(2026, 10, 2, 10, 0));

        EndpointHit hit2 = new EndpointHit();
        hit2.setApp("main-service");
        hit2.setUri("/events/1");
        hit2.setIp("192.168.0.2");
        hit2.setHitDatetime(LocalDateTime.of(2026, 10, 2, 11, 0));

        EndpointHit hit3 = new EndpointHit();
        hit3.setApp("main-service");
        hit3.setUri("/events/1");
        hit3.setIp("192.168.0.1");
        hit3.setHitDatetime(LocalDateTime.of(2026, 10, 2, 12, 0));

        endpointHitRepository.saveAll(
                List.of(hit1, hit2, hit3)
        );

        List<StatDto> result =
                endpointHitRepository.getCountHitsByDatetimeWithoutUrisUniqueIP(
                        start,
                        end
                );

        assertEquals(1, result.size());

        StatDto stat = result.getFirst();

        assertEquals("main-service", stat.getApp());
        assertEquals("/events/1", stat.getUri());

        // Вот здесь главное отличие от предыдущего теста:
        assertEquals(2L, stat.getHits());
    }

    @Test
    void shouldCountHitsWithUris() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 3, 0, 0);

        EndpointHit hit1 = new EndpointHit();
        hit1.setApp("main-service");
        hit1.setUri("/events/1");
        hit1.setIp("192.168.0.1");
        hit1.setHitDatetime(LocalDateTime.of(2026, 10, 2, 10, 0));

        EndpointHit hit2 = new EndpointHit();
        hit2.setApp("main-service");
        hit2.setUri("/events/1");
        hit2.setIp("192.168.0.2");
        hit2.setHitDatetime(LocalDateTime.of(2026, 10, 2, 11, 0));

        EndpointHit hit3 = new EndpointHit();
        hit3.setApp("main-service");
        hit3.setUri("/events/2");
        hit3.setIp("192.168.0.3");
        hit3.setHitDatetime(LocalDateTime.of(2026, 10, 2, 12, 0));

        // Этот специально НЕ запрашиваем
        EndpointHit hit4 = new EndpointHit();
        hit4.setApp("main-service");
        hit4.setUri("/events/3");
        hit4.setIp("192.168.0.4");
        hit4.setHitDatetime(LocalDateTime.of(2026, 10, 2, 13, 0));

        endpointHitRepository.saveAll(
                List.of(hit1, hit2, hit3, hit4)
        );

        List<String> uris = List.of("/events/1", "/events/2");

        List<StatDto> result =
                endpointHitRepository.getCountHitsByDatetimeWithUris(
                        start,
                        end,
                        uris
                );

        assertEquals(2, result.size());

        assertEquals("/events/1", result.get(0).getUri());
        assertEquals(2L, result.get(0).getHits());

        assertEquals("/events/2", result.get(1).getUri());
        assertEquals(1L, result.get(1).getHits());
    }
}