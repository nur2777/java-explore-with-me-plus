package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

/**
 * Результат подтверждения/отклонения заявок на участие в событии
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {

    /**
     * Подтвержденные заявки на участие
     */
    private List<ParticipationRequestDto> confirmedRequests;

    /**
     * Отклонённые заявки на участие
     */
    private List<ParticipationRequestDto> rejectedRequests;
}
