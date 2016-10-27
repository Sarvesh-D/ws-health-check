package com.ds.ws.health.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Utility class to get instance of <code>MailService</code>
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 * @see MailServiceProvider
 */
@Service
public class MailServiceFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(MailServiceFactory.class);
	
	@Autowired
	private ApplicationContext context;
	
	/**
	 * Mail Service Providers corresponding to bean names of implementations of <code>MailService</code>
	 * @author G09633463
	 *
	 */
	public static enum MailServiceProvider {
		
		DEFAULT_MAIL_SERVICE_PROVIDER("defaultMailSenderService"),
		HTML_MAIL_SERVICE_PROVIDER("htmlMailSenderService");
		
		private String provider;
		
		private MailServiceProvider(String provider) {
			this.provider = provider;
		}
		
		public String valueOf() {
			return this.provider;
		}
		
	}
	
	/**
	 * Factory method to get instance of <code>MailService</code> depending on the provider specified
	 * @param provider MailServiceProvider instance stating which implementation of <code>MailService</code>
	 * should be used to send mails
	 * @return <b>MailService</b> implementation of <code>MailService</code>.
	 * {@link DefaultMailSenderService} if no Provider is specified.
	 */
	public MailService getInstance(MailServiceProvider provider) {
		logger.debug("Getting Instance of Mail Provider {}", provider);
		MailService mailService = null;
		if(null != provider)
			mailService = context.getBean(provider.valueOf(), MailService.class);
		else
			mailService = context.getBean(MailServiceProvider.DEFAULT_MAIL_SERVICE_PROVIDER.valueOf(),
					MailService.class);
		return mailService;
	}

}
