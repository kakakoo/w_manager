package com.i4uworks.weys.config;

public class TokenValues {

	private int adminKey;
	private String adminId;
	private String stores;
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
	public String getStores() {
		return stores;
	}
	public void setStores(String stores) {
		this.stores = stores;
	}
	@Override
	public String toString() {
		return "TokenValues [adminKey=" + adminKey + ", adminId=" + adminId + ", stores=" + stores + "]";
	}
}
