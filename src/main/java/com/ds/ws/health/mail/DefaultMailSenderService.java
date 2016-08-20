package com.ds.ws.health.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class DefaultMailSenderService implements MailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SimpleMailMessage messageTemplate;

	@Override
	public void sendMail(String to, String subject, String body) {
		sendMail(messageTemplate.getFrom(), to, subject, body);
	}

	@Override
	public void sendMail(String from, String to, String subject, String body) {
		sendMimeMail(from, to, subject, body, new String[0]);
	}

	@Override
	public void sendMimeMail(String to, String subject, String body, String... filePaths) {
		sendMimeMail(messageTemplate.getFrom(), to, subject, body, filePaths);
	}

	@Override
	public void sendMimeMail(String from, String to, String subject, String body, String... filePaths) {
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

			mailSender.send(helper.getMimeMessage());

		} catch (Exception e) {
			System.out.println(e.getMessage());
			// TODO Handle it
		}
	}
}
