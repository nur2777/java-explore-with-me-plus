package ru.practicum.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDTO {

    /**
     * Текст комментария
     */
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 1000, message = "Комментарий должен быть максимум 1000 символов")
    @Size(min = 1, message = "Длина комментария должна быть минимум 1 символ")
    private String text;
}
