package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Модель данных для запроса
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "endpoint_hits")
public class EndpointHit {

    /**
     * Идентификатор записи в таблице
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Идентификатор сервиса для которого записывается информация
     */
    @Column(name = "app")
    private String app;

    /**
     * URI для которого был осуществлен запрос
     */
    @Column(name = "uri")
    private String uri;

    /**
     * IP-адрес пользователя, осуществившего запрос
     */
    @Column(name = "ip")
    private String ip;

    /**
     * Дата и время, когда был совершен запрос к эндпоинту
     */
    @Column(name = "hit_datetime")
    private LocalDateTime hitDatetime;
}
