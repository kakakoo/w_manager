package com.i4uworks.weys.common;

public class VersionCheck {

public static int checkVersion(String version, int startVer){
		
		int ver = 0;
		
		try{
			if(!version.contains("v"))
				return 0;
			
			ver = Integer.parseInt(version.replace("v", ""));
		} catch (Exception e) {
			return 0;
		}
		
		if(ver >= startVer && ver <= Constant.I_SERVER_VERSION){
			return ver;
		}
		
		return 0;
	}
}
