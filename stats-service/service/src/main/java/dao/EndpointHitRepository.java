package dao;

import model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EndpointHitRepository extends JpaRepository<EndpointHit,Long> {
}
