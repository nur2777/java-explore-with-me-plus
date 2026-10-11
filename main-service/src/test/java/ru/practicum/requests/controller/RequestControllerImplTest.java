
package ru.practicum.requests.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RequestControllerImplTest {

    private MockMvc mockMvc;
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        requestService = mock(RequestService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new RequestControllerImpl(requestService))
                .build();
    }

    private ParticipationRequestDto createRequestDto() {
        return ParticipationRequestDto.builder()
                .id(100L)
                .event(10L)
                .requester(1L)
                .status("PENDING")
                .build();
    }

    @Test
    void addRequestShouldReturnCreated() throws Exception {
        when(requestService.addNewRequest(1L, 10L))
                .thenReturn(createRequestDto());

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.event").value(10))
                .andExpect(jsonPath("$.requester").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(requestService).addNewRequest(1L, 10L);
    }

    @Test
    void addRequestShouldRejectMissingEventId() throws Exception {
        mockMvc.perform(post("/users/1/requests"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestService);
    }

    @Test
    void addRequestShouldRejectInvalidEventId() throws Exception {
        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestService);
    }

    @Test
    void addRequestShouldRejectInvalidUserId() throws Exception {
        mockMvc.perform(post("/users/abc/requests")
                        .param("eventId", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestService);
    }

    @Test
    void cancelRequestShouldReturnOk() throws Exception {
        ParticipationRequestDto dto = createRequestDto();
        dto.setStatus("CANCELED");

        when(requestService.cancelRequest(1L, 100L))
                .thenReturn(dto);

        mockMvc.perform(patch("/users/1/requests/100/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("CANCELED"));

        verify(requestService).cancelRequest(1L, 100L);
    }

    @Test
    void cancelRequestShouldRejectInvalidRequestId() throws Exception {
        mockMvc.perform(patch("/users/1/requests/abc/cancel"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestService);
    }

    @Test
    void cancelRequestShouldRejectInvalidUserId() throws Exception {
        mockMvc.perform(patch("/users/abc/requests/100/cancel"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestService);
    }

    @Test
    void getRequestsShouldReturnList() throws Exception {
        when(requestService.getRequests(1L))
                .thenReturn(List.of(createRequestDto()));

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(requestService).getRequests(1L);
    }

    @Test
    void getRequestsShouldReturnEmptyList() throws Exception {
        when(requestService.getRequests(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(requestService).getRequests(1L);
    }
}
