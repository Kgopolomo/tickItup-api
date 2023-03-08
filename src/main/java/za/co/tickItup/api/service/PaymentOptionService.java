package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import za.co.tickItup.api.entity.PaymentOption;
import za.co.tickItup.api.entity.UserProfile;
import za.co.tickItup.api.repository.PaymentOptionRepository;
import za.co.tickItup.api.repository.UserProfileRepository;

import java.util.List;

@Service
public class PaymentOptionService {

    @Autowired private UserProfileRepository userProfileRepository;

    @Autowired private PaymentOptionRepository paymentOptionRepository;

    public PaymentOption addPaymentOption(Long userId, PaymentOption paymentOption) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        paymentOption.setUserProfile(user);
        return paymentOptionRepository.save(paymentOption);
    }

    public void deletePaymentOption(Long id) {
        PaymentOption paymentOption = paymentOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Option not found with id: " + id));
        paymentOptionRepository.delete(paymentOption);
    }

    public List<PaymentOption> getPaymentOptionsByUser(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getPaymentOptions();
    }
}
