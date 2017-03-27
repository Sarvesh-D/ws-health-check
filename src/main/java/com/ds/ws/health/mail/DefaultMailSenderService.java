package com.ds.ws.health.mail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * This is the default mail sender service implementation.
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 * @see MailService
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DefaultMailSenderService implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMailSenderService.class);

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage messageTemplate;

    private Map<String, String> mailProps = new HashMap<>();

    @Override
    public void sendMail(String from, String[] to, String subject, String body) {
	sendMimeMail(from, to, subject, body, new String[0]);
    }

    @Override
    public void sendMail(String[] to, String subject, String body) {
	sendMail(messageTemplate.getFrom(), to, subject, body);
    }

    @Override
    public void sendMimeMail(String from, String[] to, String subject, String body, String... filePaths) {
	try {
	    MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);

	    helper.setFrom(from);
	    helper.setTo(to);
	    helper.setSubject(subject);
	    helper.setText(body);

	    Arrays.stream(filePaths).map(FileSystemResource::new).forEach(file -> {
		try {
		    helper.addAttachment(file.getFilename(), file);
		} catch (MessagingException e) {
		    e.printStackTrace();
		}
	    });

	    logMailProps(from, to, subject, body, filePaths);

	    mailSender.send(helper.getMimeMessage());
	    logger.debug("Mail Sent");

	} catch (Exception e) {
	    logger.error("Error occured while sending mail : {}", e.getMessage());
	}
    }

    @Override
    public void sendMimeMail(String[] to, String subject, String body, String... filePaths) {
	sendMimeMail(messageTemplate.getFrom(), to, subject, body, filePaths);
    }

    void logMailProps(String from, String[] to, String subject, String body, String[] filePaths) {
	mailProps.put("From", from);
	mailProps.put("To", to.toString());
	mailProps.put("Subject", subject);
	mailProps.put("Body", body);
	mailProps.put("Attachments", filePaths.toString());

	prettyPrint();
    }

    private void prettyPrint() {
	StringBuilder builder = new StringBuilder("\nSending Mail\n");
	builder.append(mailProps.entrySet().stream().map(entry -> entry.getKey() + "\t-->\t" + entry.getValue())
		.collect(Collectors.joining("\n")));
	logger.debug(builder.toString());
    }
}
