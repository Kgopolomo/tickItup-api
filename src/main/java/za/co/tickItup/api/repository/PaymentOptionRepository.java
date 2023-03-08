package za.co.tickItup.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import za.co.tickItup.api.entity.PaymentOption;
import za.co.tickItup.api.entity.Role;

@Repository
public interface PaymentOptionRepository extends JpaRepository<PaymentOption, Long>, JpaSpecificationExecutor<PaymentOption> {
}
