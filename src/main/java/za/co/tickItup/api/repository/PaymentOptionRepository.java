package za.co.tickItup.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import za.co.tickItup.api.entity.PaymentOption;
import za.co.tickItup.api.entity.Role;
import za.co.tickItup.api.entity.TicketPurchaseHistory;
import za.co.tickItup.api.entity.UserProfile;

import java.util.List;

@Repository
public interface PaymentOptionRepository extends JpaRepository<PaymentOption, Long>, JpaSpecificationExecutor<PaymentOption> {
    PaymentOption findByUserProfile(UserProfile userProfile);
}
