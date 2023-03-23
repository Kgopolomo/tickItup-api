package za.co.tickItup.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import za.co.tickItup.api.entity.PaymentOption;
import za.co.tickItup.api.entity.UserProfile;
import za.co.tickItup.api.repository.PaymentOptionRepository;
import za.co.tickItup.api.repository.UserProfileRepository;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

@Service
public class PaymentOptionService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES";

    private static final int KEY_SIZE = 256;
    @Value("${aes.key}")
    private String aesKey;



    private static Key generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            SecureRandom secureRandom = new SecureRandom();
            keyGenerator.init(KEY_SIZE, secureRandom);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }



    private static final Key ENCRYPTION_KEY = generateKey();

    @Autowired private UserProfileRepository userProfileRepository;

    @Autowired private PaymentOptionRepository paymentOptionRepository;

    public PaymentOption addPaymentOption(PaymentOption paymentOption) {

        UserProfile user = userProfileRepository.findById(5L)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + 5L));


        paymentOption.setCardNumber(paymentOption.getCardNumber());
        paymentOption.setCvv(paymentOption.getCvv());
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

    private String encrypt(String input) {
        try {
            Key key = new SecretKeySpec(ENCRYPTION_KEY.getEncoded(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }
    public String decrypt(String input) {
        try {
            Key key = new SecretKeySpec(ENCRYPTION_KEY.getEncoded(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(input));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }





}
