package com.ds.ws.health.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to get instance of <code>MailService</code>
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 * @see MailServiceProvider
 * @see MailService
 */
@Service
@Slf4j
public class MailServiceFactory {

    /**
     * Mail Service Providers corresponding to bean names of implementations of
     * <code>MailService</code>
     * 
     * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
     *
     */
    public static enum MailServiceProvider {

	DEFAULT_MAIL_SERVICE_PROVIDER("defaultMailSenderService"), HTML_MAIL_SERVICE_PROVIDER("htmlMailSenderService");

	private String provider;

	private MailServiceProvider(String provider) {
	    this.provider = provider;
	}

	public String valueOf() {
	    return this.provider;
	}

    }

    @Autowired
    private ApplicationContext context;

    /**
     * Factory method to get instance of <code>MailService</code> depending on
     * the provider specified
     * 
     * @param provider
     *            MailServiceProvider instance stating which implementation of
     *            <code>MailService</code> should be used to send mails
     * @return <b>MailService</b> implementation of <code>MailService</code>.
     *         {@link DefaultMailSenderService} if no Provider is specified.
     */
    public MailService getInstance(MailServiceProvider provider) {
	log.debug("Getting Instance of Mail Provider {}", provider);
	MailService mailService = null;
	if (null != provider)
	    mailService = context.getBean(provider.valueOf(), MailService.class);
	else
	    mailService = context.getBean(MailServiceProvider.DEFAULT_MAIL_SERVICE_PROVIDER.valueOf(),
		    MailService.class);
	return mailService;
    }

}
