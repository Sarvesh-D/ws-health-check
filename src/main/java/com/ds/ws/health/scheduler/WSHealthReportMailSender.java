package com.ds.ws.health.scheduler;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.ws.health.mail.MailServiceFactory;
import com.ds.ws.health.mail.MailServiceFactory.MailServiceProvider;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;

@Component
public class WSHealthReportMailSender {
	
	@Autowired
	private MailServiceFactory mailServiceFactory;
	
	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;
	
	@Autowired
	private Properties mailProperties;
	
	@Scheduled(cron = "${mail.interval}")
	private void sendReportMail() {
		final String from = mailProperties.getProperty("report.mail.from");
		final String to = mailProperties.getProperty("report.mail.to");
		final String subject = mailProperties.getProperty("report.mail.subject");
		final String body = mailProperties.getProperty("report.mail.body");
		
		mailServiceFactory.getInstance(MailServiceProvider.DEFAULT_MAIL_SERVICE_PROVIDER).
		sendMimeMail(from, to, subject, body, new String[] {reportUtils.getReportFileName()});
		
	}

}
