package za.co.tickItup.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import za.co.tickItup.api.entity.Event;
import za.co.tickItup.api.entity.TicketPurchaseHistory;
import za.co.tickItup.api.entity.UserProfile;

import java.util.List;
@Repository
public interface TicketPurchaseHistoryRepository extends JpaRepository<TicketPurchaseHistory, Long>, JpaSpecificationExecutor<TicketPurchaseHistory> {
    List<TicketPurchaseHistory> findByUserProfile(UserProfile userProfile);

    TicketPurchaseHistory findByUserProfileAndEvent(UserProfile user, Event event);

}
