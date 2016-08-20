package com.ds.ws.health.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class MailServiceFactory {
	
	@Autowired
	private ApplicationContext context;
	
	public static enum MailServiceProvider {
		
		DEFAULT_MAIL_SERVICE_PROVIDER("defaultMailSenderService");
		
		private String provider;
		
		private MailServiceProvider(String provider) {
			this.provider = provider;
		}
		
		public String valueOf() {
			return this.provider;
		}
		
	}
	
	public MailService getInstance(MailServiceProvider provider) {
		MailService mailService = null;
		if(null != provider)
			mailService = context.getBean(provider.valueOf(), MailService.class);
		else
			mailService = context.getBean(MailServiceProvider.DEFAULT_MAIL_SERVICE_PROVIDER.valueOf(),
					MailService.class);
		return mailService;
	}

}
