package com.ds.ws.health.mail;

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
    private String htmlMailBody = "<b>Testing HTML mail Service</b>";
    private String plainMailBody = "<b>Testing Plain Text Mail</b>";
    private String mailWithAttachmentBody = "Testing Mail Attachments</b>";

    @Before
    public void setUp() throws Exception {
	mailServiceFactory = rootContext.getBean(MailServiceFactory.class);
	mailService = mailServiceFactory.getInstance(MailServiceProvider.HTML_MAIL_SERVICE_PROVIDER);
    }

    @Test
    public final void testSendMail() {
	mailService.sendMail(new String[] { to }, subject, htmlMailBody);
	mailServiceFactory.getInstance(MailServiceProvider.DEFAULT_MAIL_SERVICE_PROVIDER).sendMail(new String[] { to },
		subject, plainMailBody);
	mailServiceFactory.getInstance(null).sendMail(new String[] { to }, subject, plainMailBody);
    }

    @Test
    public final void testSendMailWithAttachments() {
	String filePath = new ClassPathResource("src/main/resources/mail_template.vm").getPath();
	mailService.sendMimeMail(from, new String[] { to }, subject, mailWithAttachmentBody, new String[] { filePath });
	mailServiceFactory.getInstance(MailServiceProvider.DEFAULT_MAIL_SERVICE_PROVIDER).sendMimeMail(from,
		new String[] { to }, subject, mailWithAttachmentBody, new String[] { filePath });
    }

    @Test(expected = HealthCheckException.class)
    public final void testSendMimeMail() {
	String filePath = new ClassPathResource("MailServiceTest.java").getPath();
	mailService.sendMimeMail(from, new String[] { to }, subject, mailWithAttachmentBody, new String[] { filePath });
    }

}
