package com.ds.ws.health.mail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.ClassPathResource;

import com.ds.ws.health.BaseTest;
import com.ds.ws.health.exception.HealthCheckException;
import com.ds.ws.health.mail.MailServiceFactory.MailServiceProvider;

@RunWith(JUnit4.class)
public class MailServiceTest extends BaseTest {

    private MailService mailService;
    private MailServiceFactory mailServiceFactory;
    private String from = "dubz1708@gmail.com";
    private String to = "dubz1708@gmail.com";
    private String subject = "test";
    private String body = "<b>Testing HTML mail Service</b>";

    @Before
    public void setUp() throws Exception {
	mailServiceFactory = rootContext.getBean(MailServiceFactory.class);
	mailService = mailServiceFactory.getInstance(MailServiceProvider.HTML_MAIL_SERVICE_PROVIDER);
    }

    @Test(expected=HealthCheckException.class)
    public final void testSendMimeMail() {
	String filePath = new ClassPathResource("MailServiceTest.java").getPath();
	mailService.sendMimeMail(from, new String[] {to}, subject, body, new String[] {filePath});
    }

    @Test
    public final void testSendMail() {
	mailService.sendMimeMail(from, new String[] {to}, subject, body);
    }

}
