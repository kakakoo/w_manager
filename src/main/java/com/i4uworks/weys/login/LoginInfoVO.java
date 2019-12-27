package com.i4uworks.weys.login;

import java.util.Date;

public class LoginInfoVO {

	private int adminKey;
	private String adminId;
	private String adminPw;
	private String adminName;
	private String adminTp;
	private int storeId;
	private String storeNm;
	private String stores;
	private String tokenAdm;
	private Date tokenExpireDttm;
	private String uuid;
	private String os = "A";
	public int getAdminKey() {
		return adminKey;
	}
	public void setAdminKey(int adminKey) {
		this.adminKey = adminKey;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getAdminPw() {
		return adminPw;
	}
	public void setAdminPw(String adminPw) {
		this.adminPw = adminPw;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public int getStoreId() {
		return storeId;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public Date getTokenExpireDttm() {
		return tokenExpireDttm;
	}
	public void setTokenExpireDttm(Date tokenExpireDttm) {
		this.tokenExpireDttm = tokenExpireDttm;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getTokenAdm() {
		return tokenAdm;
	}
	public void setTokenAdm(String tokenAdm) {
		this.tokenAdm = tokenAdm;
	}
	public String getStoreNm() {
		return storeNm;
	}
	public void setStoreNm(String storeNm) {
		this.storeNm = storeNm;
	}
	public String getStores() {
		return stores;
	}
	public void setStores(String stores) {
		this.stores = stores;
	}
	public String getAdminTp() {
		return adminTp;
	}
	public void setAdminTp(String adminTp) {
		this.adminTp = adminTp;
	}
	@Override
	public String toString() {
		return "LoginInfoVO [adminKey=" + adminKey + ", adminId=" + adminId + ", adminPw=" + adminPw + ", adminName="
				+ adminName + ", storeId=" + storeId + ", tokenAdm=" + tokenAdm + ", tokenExpireDttm=" + tokenExpireDttm
				+ ", uuid=" + uuid + ", os=" + os + "]";
	}
	public boolean checkLogin(){
		if(adminId == null
				|| adminPw == null
				|| uuid == null){
			return false;
		}
		return true;
	}
}
