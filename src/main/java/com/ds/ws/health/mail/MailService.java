package com.ds.ws.health.mail;

public interface MailService {
	
	void sendMail(String to, String subject, String body);
	
	void sendMail(String from, String to, String subject, String body);
	
	void sendMimeMail(String to, String subject, String body, String... filePaths);
	
	void sendMimeMail(String from, String to, String subject, String body, String... filePaths);

}
