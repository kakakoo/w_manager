package com.i4uworks.weys.mail;

import java.util.Map;

public class EmailVO {

	private String subject;
	private String receiver;
	private String from;
	private String veloTmp;
	private Map<String, Object> emailMap;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getVeloTmp() {
		return veloTmp;
	}
	public void setVeloTmp(String veloTmp) {
		this.veloTmp = veloTmp;
	}
	public Map<String, Object> getEmailMap() {
		return emailMap;
	}
	public void setEmailMap(Map<String, Object> emailMap) {
		this.emailMap = emailMap;
	}
	@Override
	public String toString() {
		return "EmailVO [subject=" + subject + ", receiver=" + receiver + ", from=" + from 
				+ ", veloTmp=" + veloTmp + ", emailMap=" + emailMap + "]";
	}
	
}
