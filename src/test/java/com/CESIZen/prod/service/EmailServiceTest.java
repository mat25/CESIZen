package com.CESIZen.prod.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> emailCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendResetPasswordEmail_shouldSendEmailWithCorrectContent() {
        String email = "test@example.com";
        String token = "dummy-token";

        emailService.sendResetPasswordEmail(email, token);

        verify(mailSender).send(emailCaptor.capture());
        SimpleMailMessage sentEmail = emailCaptor.getValue();

        String expectedResetUrl = "http://localhost:5173/reset-password?token=" + token;
        String expectedText = "Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + expectedResetUrl;

        assert sentEmail.getTo()[0].equals(email);
        assert sentEmail.getSubject().equals("Réinitialisation de votre mot de passe");
        assert sentEmail.getText().equals(expectedText);
    }
}
