package com.softj.itple.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix="mail")
@Data
public class MailProperties {
	private String mailHost;
	private Integer mailPort;
	private String mailUserName;
	private String mailPassword;
	private String mailTransportProtocol;
	private String mailSmtpAuth;
	private String mailSmtpStarttlsEnable;
	private String mailDebug;
}
