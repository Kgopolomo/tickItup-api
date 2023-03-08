package za.co.tickItup.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import za.co.tickItup.api.entity.Event;
import za.co.tickItup.api.entity.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    Optional <Ticket> findByCode(String code);
    List<Ticket> findAllByEvent(Event event);
}
