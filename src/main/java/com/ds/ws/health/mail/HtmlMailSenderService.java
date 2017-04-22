package com.ds.ws.health.mail;

import java.util.Arrays;

import javax.mail.MessagingException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ds.ws.health.exception.HealthCheckException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HtmlMailSenderService extends DefaultMailSenderService {

    @Override
    public void sendMimeMail(String from, String[] to, String subject, String body, String... filePaths) {
	try {
	    MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);

	    helper.setFrom(from);
	    helper.setTo(to);
	    helper.setSubject(subject);
	    helper.setText(body, true);

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

}
