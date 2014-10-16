package com.edifecs.servicemanager.email.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

public class EmailCommandHandler extends AbstractCommandHandler implements IEmailCommandHandler {

	private String username;
	private String password;
	private Properties props = new Properties();

	private Logger logger = LoggerFactory.getLogger(getClass());

	public EmailCommandHandler(Properties resourceProperties) {
		super();

		if (null == resourceProperties || resourceProperties.isEmpty())
			throw new RuntimeException("Invalid Resource for Email Service");

		this.username = resourceProperties.getProperty("username");
		this.password = resourceProperties.getProperty("password");

		resourceProperties.remove("username");
		resourceProperties.remove("password");

		props.put("mail.smtp.auth", resourceProperties.getProperty("auth"));
		props.put("mail.smtp.host", resourceProperties.getProperty("host"));
		props.put("mail.smtp.port", resourceProperties.getProperty("port"));

		String protocol = resourceProperties.getProperty("protocol");
		if (protocol.equalsIgnoreCase("SSL")) {
			props.put("mail.smtp.socketFactory.port",
					resourceProperties.getProperty("socketFactoryPort"));
			props.put("mail.smtp.socketFactory.class",
					resourceProperties.getProperty("socketFactoryClass"));
		} else {
			// TLS
			props.put("mail.smtp.starttls.enable", "true");
		}

		logger.debug("email service configured sucessfully");

//		for (Entry<Object, Object> map : props.entrySet())
//			logger.debug("\t  {} : {}", map.getKey(), map.getValue());

	}

	public boolean sendMail(String to, String from, String subject, String msg) {

		try {

			if (username == null || password == null || props.isEmpty())
				throw new RuntimeException(
						"Please configure the SMTP server before sending email.");

			Session session = Session.getInstance(props, new Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					PasswordAuthentication p = new PasswordAuthentication(
							username, password);
					return p;
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(msg, "text/html; charset=utf-8");

			Transport.send(message);

			logger.debug("message sent sucessfully to : {} ", to);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error ", e);
			throw new RuntimeException(e);
		}

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

}
