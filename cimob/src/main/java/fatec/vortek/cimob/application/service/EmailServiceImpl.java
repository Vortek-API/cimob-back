package fatec.vortek.cimob.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import fatec.vortek.cimob.domain.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@cimob.com}")
    private String fromEmail;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = "http://localhost:3000/redefinir-senha?token=" + token;
        String subject = "Redefinição de Senha - CIMOB";
        String body = "Olá,\n\nVocê solicitou a redefinição de sua senha.\nClique no link abaixo para redefinir:\n\n" + resetLink + "\n\nSe você não solicitou isso, ignore este e-mail.";

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                System.out.println("E-mail enviado para: " + to);
            } catch (Exception e) {
                System.err.println("Erro ao enviar e-mail: " + e.getMessage());
                logEmail(to, subject, body);
            }
        } else {
            logEmail(to, subject, body);
        }
    }

    private void logEmail(String to, String subject, String body) {
        System.out.println("----- MOCK EMAIL SERVICE -----");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + body);
        System.out.println("------------------------------");
    }
}
