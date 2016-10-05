package com.barclays.solveit.ws.health.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 * @see MailService
 */
@Service
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class DefaultMailSenderService implements MailService {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultMailSenderService.class);

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	private SimpleMailMessage messageTemplate;
	
	private Map<String,String> mailProps = new HashMap<>();

	@Override
	public void sendMail(String[] to, String subject, String body) {
		sendMail(messageTemplate.getFrom(), to, subject, body);
	}

	@Override
	public void sendMail(String from, String[] to, String subject, String body) {
		sendMimeMail(from, to, subject, body, new String[0]);
	}

	@Override
	public void sendMimeMail(String[] to, String subject, String body, String... filePaths) {
		sendMimeMail(messageTemplate.getFrom(), to, subject, body, filePaths);
	}

	@Override
	public void sendMimeMail(String from, String[] to, String subject, String body, String... filePaths) {
		try{
			MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);

			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body);

			for (String filePath : filePaths) {
				FileSystemResource file = new FileSystemResource(filePath);
				helper.addAttachment(file.getFilename(), file);
			}

			logMailProps(from, to, subject, body, filePaths);
			
			mailSender.send(helper.getMimeMessage());
			logger.debug("Mail Sent");

		} catch (Exception e) {
			logger.error("Error occured while sending mail : {}",e.getMessage());
		}
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
		for (Entry<String,String> mailProp : mailProps.entrySet()) {
			builder.append(mailProp.getKey() +"\t-->\t"+ mailProp.getValue()+"\n");
		}
		logger.debug(builder.toString());
	}
}
