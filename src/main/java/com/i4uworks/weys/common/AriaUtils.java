package com.i4uworks.weys.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.i4uworks.weys.common.aria.ARIACipher;


public class AriaUtils {
	
	static ARIACipher CIPHER = null;
	
	/**
	 * ARIA 암호화 메소드
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String ariaEncryptString(String data) throws Exception{
		String encStr = "";
		try {
			
			if(CIPHER == null) CIPHER = new ARIACipher();
			
			if(data == null){
				data = "";
			}
			encStr = CIPHER.encryptString(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ARIA Encrypt Exception");
		}
		return encStr;
	}
	
	/**
	 * ARIA 복호화 메소드
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String ariaDecryptString(String data) throws Exception{
		String decStr = "";
		try {
			if(CIPHER == null) CIPHER = new ARIACipher();
			
			decStr = CIPHER.decryptString(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("ARIA Decrypt Exception");
		}
		return decStr;
	}
	
	

	/**
	 * 데이터를 암호화하는 기능
	 *
	 * @param byte[] data 암호화할 데이터
	 * @return String result 암호화된 데이터
	 * @exception Exception
	 */
	public static String encodeBinary(byte[] data) throws Exception {
		if (data == null) {
			return "";
		}

		return new String(Base64.encodeBase64(data));
	}

	/**
	 * 데이터를 암호화하는 기능
	 *
	 * @param String data 암호화할 데이터
	 * @return String result 암호화된 데이터
	 * @exception Exception
	 */
	public static String encode(String data) throws Exception {
		return encodeBinary(data.getBytes());
	}

	/**
	 * 데이터를 복호화하는 기능
	 *
	 * @param String data 복호화할 데이터
	 * @return String result 복호화된 데이터
	 * @exception Exception
	 */
	public static byte[] decodeBinary(String data) throws Exception {
		return Base64.decodeBase64(data.getBytes());
	}

	/**
	 * 데이터를 복호화하는 기능
	 *
	 * @param String data 복호화할 데이터
	 * @return String result 복호화된 데이터
	 * @exception Exception
	 */
	public static String decode(String data) throws Exception {
		return new String(decodeBinary(data));
	}

    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용).
     * 
     * deprecated : 보안 강화를 위하여 salt로 ID를 지정하는 encryptPassword(password, id) 사용
     *
     * @param String data 암호화할 비밀번호
     * @return String result 암호화된 비밀번호
     * @exception Exception
     */
    @Deprecated
    public static String encryptPassword(String data) throws Exception {

		if (data == null) {
		    return "";
		}
	
		byte[] plainText = null; // 평문
		byte[] hashValue = null; // 해쉬값
		plainText = data.getBytes();
	
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		// 변경 시 기존 hash 값에 검증 불가.. => deprecated 시키고 유지
		/*	
	    // Random 방식의 salt 추가
	    SecureRandom ng = new SecureRandom();
	    byte[] randomBytes = new byte[16];
	    ng.nextBytes(randomBytes);
	    
	    md.reset();
	    md.update(randomBytes);
	    
		*/		
		hashValue = md.digest(plainText);
		
		/*
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(hashValue);
		*/
		return new String(Base64.encodeBase64(hashValue));
    }
    
    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
     * 
     * @param password 암호화될 패스워드
     * @param id salt로 사용될 사용자 ID 지정
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws Exception
     */
    public static String encryptPassword(String password, String id) throws Exception {

		if (password == null) {
		    return "";
		}
	
		byte[] hashValue = null; // 해쉬값
	
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		md.reset();
		md.update(id.getBytes());
		
		hashValue = md.digest(password.getBytes());
	
		return new String(Base64.encodeBase64(hashValue));
    }
    
    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
     * @param data 암호화할 비밀번호
     * @param salt Salt
     * @return 암호화된 비밀번호
     * @throws Exception
     */
    public static String encryptPassword(String data, byte[] salt) throws Exception {

		if (data == null) {
		    return "";
		}
	
		byte[] hashValue = null; // 해쉬값
	
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		md.reset();
		md.update(salt);
		
		hashValue = md.digest(data.getBytes());
	
		return new String(Base64.encodeBase64(hashValue));
    }
    
    /**
     * 비밀번호를 암호화된 패스워드 검증(salt가 사용된 경우만 적용).
     * 
     * @param data 원 패스워드
     * @param encoded 해쉬처리된 패스워드(Base64 인코딩)
     * @return
     * @throws Exception
     */
    public static boolean checkPassword(String data, String encoded, byte[] salt) throws Exception {
    	byte[] hashValue = null; // 해쉬값
    	
    	MessageDigest md = MessageDigest.getInstance("SHA-256");
    	
    	md.reset();
    	md.update(salt);
    	hashValue = md.digest(data.getBytes());
    	
    	return MessageDigest.isEqual(hashValue, Base64.decodeBase64(encoded.getBytes()));
    }
	
	public static void main(String [] args){
		
		String testPassword = "12341234";
		String testId = "1";
		//비밀번호 암호화
		try {
			String enpassword = AriaUtils.encryptPassword(testPassword, testId);
			
			System.out.println(testPassword+ ">>>>"+enpassword);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
