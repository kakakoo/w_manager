package com.i4uworks.weys.vbank;

public class VbankVO {

	private String icmNm;
	private String msg;
	private int getAmnt;
	private int rsvId;
	private String chkSt;
	private String encKey;
	public String getIcmNm() {
		return icmNm;
	}
	public void setIcmNm(String icmNm) {
		this.icmNm = icmNm;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getGetAmnt() {
		return getAmnt;
	}
	public void setGetAmnt(int getAmnt) {
		this.getAmnt = getAmnt;
	}
	public int getRsvId() {
		return rsvId;
	}
	public void setRsvId(int rsvId) {
		this.rsvId = rsvId;
	}
	public String getChkSt() {
		return chkSt;
	}
	public void setChkSt(String chkSt) {
		this.chkSt = chkSt;
	}
	public String getEncKey() {
		return encKey;
	}
	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}
	@Override
	public String toString() {
		return "VbankVO [icmNm=" + icmNm + ", msg=" + msg + ", getAmnt=" + getAmnt + ", rsvId=" + rsvId + ", chkSt="
				+ chkSt + ", encKey=" + encKey + "]";
	}
	
}
