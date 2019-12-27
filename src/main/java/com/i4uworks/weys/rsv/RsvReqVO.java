package com.i4uworks.weys.rsv;

public class RsvReqVO {
	private int adminKey;
	private int rsvId;
	private String rsvQr;
	private String startDt;
	private String endDt;
	private String rsvDt;
	private String rsvTm;
	private String searchTxt;
	private String listTp;
	private String encKey;
	private String memo;
	private String admin;
	private String barcode;
	private String codeTp;
	public String getRsvQr() {
		return rsvQr;
	}
	public void setRsvQr(String rsvQr) {
		this.rsvQr = rsvQr;
	}
	public String getStartDt() {
		return startDt;
	}
	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}
	public String getEndDt() {
		return endDt;
	}
	public void setEndDt(String endDt) {
		this.endDt = endDt;
	}
	public String getSearchTxt() {
		return searchTxt;
	}
	public void setSearchTxt(String searchTxt) {
		this.searchTxt = searchTxt;
	}
	public String getEncKey() {
		return encKey;
	}
	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}
	public String getRsvDt() {
		return rsvDt;
	}
	public void setRsvDt(String rsvDt) {
		this.rsvDt = rsvDt;
	}
	public int getAdminKey() {
		return adminKey;
	}
	public void setAdminKey(int adminKey) {
		this.adminKey = adminKey;
	}
	public String getListTp() {
		return listTp;
	}
	public void setListTp(String listTp) {
		this.listTp = listTp;
	}
	public String getRsvTm() {
		return rsvTm;
	}
	public void setRsvTm(String rsvTm) {
		this.rsvTm = rsvTm;
	}
	public int getRsvId() {
		return rsvId;
	}
	public void setRsvId(int rsvId) {
		this.rsvId = rsvId;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getCodeTp() {
		return codeTp;
	}
	public void setCodeTp(String codeTp) {
		this.codeTp = codeTp;
	}
	@Override
	public String toString() {
		return "RsvReqVO [adminKey=" + adminKey + ", rsvId=" + rsvId + ", rsvQr=" + rsvQr + ", startDt=" + startDt
				+ ", endDt=" + endDt + ", rsvDt=" + rsvDt + ", rsvTm=" + rsvTm + ", searchTxt=" + searchTxt
				+ ", listTp=" + listTp + ", encKey=" + encKey + ", memo=" + memo + ", admin=" + admin + "]";
	}
	public boolean checkMemo(){
		if(memo == null ||
				admin == null){
			return false;
		}
		return true;
	}
}
