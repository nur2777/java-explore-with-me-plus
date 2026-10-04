package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(EndpointHitControllerImpl.class)
class EndpointHitControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EndpointHitService endpointHitService;

    @Test
    void addHitShouldReturnCreated() throws Exception {
        HitDto hitDto = new HitDto();
        hitDto.setApp("main-service");
        hitDto.setUri("/events/1");
        hitDto.setIp("192.168.0.1");
        hitDto.setTimestamp(
                LocalDateTime.of(2026, 10, 2, 12, 0)
        );

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hitDto)))
                .andExpect(status().isCreated());

        verify(endpointHitService).addHit(any(HitDto.class));
    }

    @Test
    void addHitShouldReturnBadRequestWhenHitDtoIsInvalid() throws Exception {
        HitDto hitDto = new HitDto();
        hitDto.setApp("");
        hitDto.setUri("/events/1");
        hitDto.setIp("192.168.0.1");
        hitDto.setTimestamp(
                LocalDateTime.of(2026, 10, 2, 12, 0)
        );

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hitDto)))
                .andExpect(status().isBadRequest());

        verify(endpointHitService, never())
                .addHit(any(HitDto.class));
    }

    @Test
    void getStatsShouldReturnStats() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 3, 0, 0);

        List<String> uris = List.of("/events/1", "/events/2");

        List<StatDto> stats = List.of(
                new StatDto("main-service", "/events/1", 5L),
                new StatDto("main-service", "/events/2", 3L)
        );

        when(endpointHitService.getStats(
                start,
                end,
                uris,
                true
        )).thenReturn(stats);

        mockMvc.perform(get("/stats")
                        .param("start", "2026-10-01 00:00:00")
                        .param("end", "2026-10-03 00:00:00")
                        .param("uris", "/events/1", "/events/2")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("main-service"))
                .andExpect(jsonPath("$[0].uri").value("/events/1"))
                .andExpect(jsonPath("$[0].hits").value(5))
                .andExpect(jsonPath("$[1].app").value("main-service"))
                .andExpect(jsonPath("$[1].uri").value("/events/2"))
                .andExpect(jsonPath("$[1].hits").value(3));

        verify(endpointHitService).getStats(
                start,
                end,
                uris,
                true
        );
    }

    @Test
    void getStatsShouldUseFalseWhenUniqueIsNotSpecified() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 3, 0, 0);

        List<StatDto> stats = List.of(
                new StatDto("main-service", "/events/1", 5L)
        );

        when(endpointHitService.getStats(
                start,
                end,
                null,
                false
        )).thenReturn(stats);

        mockMvc.perform(get("/stats")
                        .param("start", "2026-10-01 00:00:00")
                        .param("end", "2026-10-03 00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("main-service"))
                .andExpect(jsonPath("$[0].uri").value("/events/1"))
                .andExpect(jsonPath("$[0].hits").value(5));

        verify(endpointHitService).getStats(
                start,
                end,
                null,
                false
        );
    }
}
