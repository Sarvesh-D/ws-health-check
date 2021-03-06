package com.ds.ws.health.mail;

import com.ds.ws.health.mail.MailServiceFactory.MailServiceProvider;

/**
 * Root interface to be implemented by any Mail Service Provider. After writing
 * its implementation, register your implementation(Mail Service Provider) in
 * {@link MailServiceProvider}
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 * @see MailServiceFactory.MailServiceProvider
 * @see DefaultMailSenderService
 * @see HtmlMailSenderService
 */
public interface MailService {

    /**
     * Send mail using information provided in arguments
     * 
     * @param from
     *            sender's email address
     * @param to
     *            receiptent's email addresses
     * @param subject
     *            of the mail
     * @param body
     *            of the mail
     */
    void sendMail(String from, String[] to, String subject, String body);

    /**
     * Overloaded method to send mail using information provided in arguments.
     * uses default sender's email address configured in messageTempalte
     * 
     * @param to
     *            receiptent's email addresses
     * @param subject
     *            of the mail
     * @param body
     *            of the mail
     */
    void sendMail(String[] to, String subject, String body);

    /**
     * Send MIME mail using information provided in arguments
     * 
     * @param from
     *            sender's email address
     * @param to
     *            receiptent's email addresses
     * @param subject
     *            of the mail
     * @param body
     *            of the mail
     * @param filePaths
     *            path of files to be attached
     */
    void sendMimeMail(String from, String[] to, String subject, String body, String... filePaths);

    /**
     * Overloaded method to send MIME mail using information provided in
     * arguments. uses default sender's email address configured in
     * messageTempalte
     * 
     * @param to
     *            receiptent's email addresses
     * @param subject
     *            of the mail
     * @param body
     *            of the mail
     * @param filePaths
     *            path of files to be attached
     */
    void sendMimeMail(String[] to, String subject, String body, String... filePaths);

}
