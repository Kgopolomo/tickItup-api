package za.co.tickItup.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import za.co.tickItup.api.entity.TicketType;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long>, JpaSpecificationExecutor<TicketType> {
    TicketType save (TicketType ticketType);
}
