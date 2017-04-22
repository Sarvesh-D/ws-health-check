package com.ds.ws.health.mail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ds.ws.health.exception.HealthCheckException;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the default mail sender service implementation.
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 * @see MailService
 */
@Service
@Slf4j
public class DefaultMailSenderService implements MailService {

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
		    throw new HealthCheckException(String.format(
			    "Exception occurerd while adding attachemets. Detailed Message is : %s", e.getMessage()));
		}
	    });

	    logMailProps(from, to, subject, body, filePaths);

	    mailSender.send(helper.getMimeMessage());
	    log.debug("Mail Sent");

	} catch (Exception e) {
	    throw new HealthCheckException(String.format("Error occured while sending mail : %s", e.getMessage()));
	}
    }

    @Override
    public void sendMimeMail(String[] to, String subject, String body, String... filePaths) {
	sendMimeMail(messageTemplate.getFrom(), to, subject, body, filePaths);
    }

    void logMailProps(String from, String[] to, String subject, String body, String[] filePaths) {
	mailProps.put("From", from);
	mailProps.put("To", Arrays.toString(to));
	mailProps.put("Subject", subject);
	mailProps.put("Attachments", Arrays.toString(filePaths));
	prettyPrint();
    }

    private void prettyPrint() {
	StringBuilder builder = new StringBuilder("\nSending Mail\n");
	builder.append(mailProps.entrySet().stream().map(entry -> entry.getKey() + "\t-->\t" + entry.getValue())
		.collect(Collectors.joining("\n")));
	log.debug(builder.toString());
    }
}
