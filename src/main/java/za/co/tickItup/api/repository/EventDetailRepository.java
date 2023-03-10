package za.co.tickItup.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import za.co.tickItup.api.entity.EventDetail;

public interface EventDetailRepository extends JpaRepository<EventDetail, Long>, JpaSpecificationExecutor<EventDetail> {
}
