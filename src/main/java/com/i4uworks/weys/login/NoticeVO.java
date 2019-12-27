package com.i4uworks.weys.login;

public class NoticeVO {

	private int anId;
	private String anTitle;
	private String anDesc;
	private String anRead;
	private String regDttm;
	public int getAnId() {
		return anId;
	}
	public void setAnId(int anId) {
		this.anId = anId;
	}
	public String getAnTitle() {
		return anTitle;
	}
	public void setAnTitle(String anTitle) {
		this.anTitle = anTitle;
	}
	public String getAnDesc() {
		return anDesc;
	}
	public void setAnDesc(String anDesc) {
		this.anDesc = anDesc;
	}
	public String getAnRead() {
		return anRead;
	}
	public void setAnRead(String anRead) {
		this.anRead = anRead;
	}
	public String getRegDttm() {
		return regDttm;
	}
	public void setRegDttm(String regDttm) {
		this.regDttm = regDttm;
	}
	@Override
	public String toString() {
		return "NoticeVO [anId=" + anId + ", anTitle=" + anTitle + ", anDesc=" + anDesc + ", anRead=" + anRead
				+ ", regDttm=" + regDttm + "]";
	}
	
}
