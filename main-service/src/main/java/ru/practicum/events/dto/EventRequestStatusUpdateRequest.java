package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.RequestStatus;

import java.util.List;

/**
 * Новый статус для заявок на участие в событии текущего пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    /**
     * Идентификаторы запросов на участие в событии текущего пользователя
     */
    private List<Long> requestIds;

    /**
     * Новый статус запроса на участие в событии текущего пользователя
     */
    private RequestStatus status;
}
