package com.ds.ws.health.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ds.ws.health.dao.UserDao;
import com.ds.ws.health.mail.MailServiceFactory;
import com.ds.ws.health.mail.MailServiceFactory.MailServiceProvider;
import com.ds.ws.health.mail.MailUtils;
import com.ds.ws.health.report.WSHealthReportGeneratorUtils;
import com.ds.ws.health.service.EnvDetailsFetchMode;
import com.ds.ws.health.service.WSHealthService;

/**
 * Scheduler class for sending report mails 
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@Component
public class WSHealthReportMailSender {
	
	private static final Logger logger = LoggerFactory.getLogger(WSHealthReportMailSender.class);
	
	@Autowired
	@Qualifier("wSHealthServiceImpl")
	private WSHealthService wsHealthService;

	@Autowired
	private MailServiceFactory mailServiceFactory;

	@Autowired
	private WSHealthReportGeneratorUtils reportUtils;
	
	@Autowired
	private MailUtils mailUtils;
	
	@Autowired
	private Properties mailProperties;
	
	@Autowired
	private Properties reportProperties;
	
	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	
	@Scheduled(cron = "${mail.interval}")
	private void sendWSHealthReportMail() {
		logger.debug("Mail Triggered on time {}",LocalDateTime.now());
		final String from = mailProperties.getProperty("report.mail.from");
		final String[] to = userDao.findAll().stream().map(user -> user.getEmail()).toArray(String[]::new);
		final String subject = mailProperties.getProperty("report.mail.subject");
		
		final LocalDate reportDate = LocalDate.now().minusDays(1);

		Map<String,Object> templateParams = new HashMap<>();
		templateParams.put("reportDate", reportDate);
		templateParams.put("dashboardLink", reportProperties.getProperty("report.footer.link"));
		templateParams.put("environments", wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.DAILY));
		templateParams.put("attachment", true);
		
		final String body = mailUtils.transformTemplateBody(mailProperties.getProperty("report.mail.body.template"),templateParams);

		mailServiceFactory.getInstance(MailServiceProvider.HTML_MAIL_SERVICE_PROVIDER).
		sendMimeMail(from, to, subject, body, new String[] {reportUtils.getReportFileForDate(reportDate)});
	}
	
	@Scheduled(cron = "${hourly.mail.interval}")
	private void sendWSHealthHourlyMail() {
		logger.debug("Mail Triggered on time {}",LocalDateTime.now());
		final String from = mailProperties.getProperty("report.mail.from");
		final String[] to = userDao.findAll().stream().map(user -> user.getEmail()).toArray(String[]::new);
		final String subject = mailProperties.getProperty("report.mail.subject");
		
		final LocalDate reportDate = LocalDate.now();

		Map<String,Object> templateParams = new HashMap<>();
		templateParams.put("reportDate", reportDate);
		templateParams.put("dashboardLink", reportProperties.getProperty("report.footer.link"));
		templateParams.put("environments", wsHealthService.getEnvHealthDetails(EnvDetailsFetchMode.HOURLY));
		templateParams.put("attachment", false);
		
		final String body = mailUtils.transformTemplateBody(mailProperties.getProperty("report.mail.body.template"),templateParams);

		mailServiceFactory.getInstance(MailServiceProvider.HTML_MAIL_SERVICE_PROVIDER).
		sendMail(from, to, subject, body);
		
	}

}
