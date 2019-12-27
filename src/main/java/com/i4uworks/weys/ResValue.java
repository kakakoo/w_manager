package com.i4uworks.weys;

import java.util.HashMap;

public class ResValue {
	private int resCode = 200;
	private String resMsg = "";
	private Object resData = new HashMap<String, Object>();
	
	public int getResCode() {
		return resCode;
	}
	public void setResCode(int resCode) {
		this.resCode = resCode;
		if(resCode != 200)
			this.resData = "";
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	public Object getResData() {
		return resData;
	}
	public void setResData(Object resData) {
		this.resData = resData;
	}
	@Override
	public String toString() {
		return "ResValue [resCode=" + resCode + ", resMsg=" + resMsg + ", resData=" + resData.toString() + "]";
	}
}
