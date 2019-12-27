package com.i4uworks.weys.login;

public class SmsVO {
	private int smsId;
	private int rsvId;
	private int adminKey;
	private String encKey;
	public int getSmsId() {
		return smsId;
	}
	public void setSmsId(int smsId) {
		this.smsId = smsId;
	}
	public int getRsvId() {
		return rsvId;
	}
	public void setRsvId(int rsvId) {
		this.rsvId = rsvId;
	}
	public int getAdminKey() {
		return adminKey;
	}
	public void setAdminKey(int adminKey) {
		this.adminKey = adminKey;
	}
	public String getEncKey() {
		return encKey;
	}
	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}
	@Override
	public String toString() {
		return "SmsVO [smsId=" + smsId + ", rsvId=" + rsvId + "]";
	}
	public boolean checkVal(){
		if(smsId == 0 || rsvId == 0){
			return false;
		}
		return true;
	}
}
