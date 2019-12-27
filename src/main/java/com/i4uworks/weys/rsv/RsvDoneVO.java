package com.i4uworks.weys.rsv;

public class RsvDoneVO {

	private int adminKey;
	private int rsvId;
	private String rsvSign;
	private String signDttm;
	private String facePic;
	private String asIs;
	private String toBe;
	private String encKey;
	private String grpSt;
	public int getAdminKey() {
		return adminKey;
	}
	public void setAdminKey(int adminKey) {
		this.adminKey = adminKey;
	}
	public int getRsvId() {
		return rsvId;
	}
	public void setRsvId(int rsvId) {
		this.rsvId = rsvId;
	}
	public String getRsvSign() {
		return rsvSign;
	}
	public void setRsvSign(String rsvSign) {
		this.rsvSign = rsvSign;
	}
	public String getAsIs() {
		return asIs;
	}
	public void setAsIs(String asIs) {
		this.asIs = asIs;
	}
	public String getToBe() {
		return toBe;
	}
	public void setToBe(String toBe) {
		this.toBe = toBe;
	}
	public String getEncKey() {
		return encKey;
	}
	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}
	public String getSignDttm() {
		return signDttm;
	}
	public void setSignDttm(String signDttm) {
		this.signDttm = signDttm;
	}
	public String getFacePic() {
		return facePic;
	}
	public void setFacePic(String facePic) {
		this.facePic = facePic;
	}
	public String getGrpSt() {
		return grpSt;
	}
	public void setGrpSt(String grpSt) {
		this.grpSt = grpSt;
	}
	@Override
	public String toString() {
		return "RsvDoneVO [adminKey=" + adminKey + ", rsvId=" + rsvId + ", rsvSign=" + rsvSign + ", signDttm="
				+ signDttm + ", facePic=" + facePic + ", asIs=" + asIs + ", toBe=" + toBe + ", encKey=" + encKey
				+ ", grpSt=" + grpSt + "]";
	}
	public boolean checkTransfer() {
		
		if(!(this.grpSt.equals("D") || this.grpSt.equals("S"))){
			return false;
		}
		
		if(this.rsvSign == null || this.facePic == null){
			return false;
		}
		return true;
	}
}
