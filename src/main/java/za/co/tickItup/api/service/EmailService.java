package za.co.tickItup.api.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    public EmailService(JavaMailSender mailSender, Configuration freemarkerConfig) {
        this.mailSender = mailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    public void sendNewUserConfirmationEmail(String to, String subject, Map<String, Object> model) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);

            // Use Freemarker to generate the email content from a template
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfig.getTemplate("/email/templates/new_user_confirmation.ftl"), model);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | IOException | TemplateException e) {
            // Handle exceptions
        }
    }
}
