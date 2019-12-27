package com.i4uworks.weys.common.aria;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ARIACipher {
	
	private static final int BLOCK_SIZE 	= 16;
	private static final String MASTER_KEY 	= "sehmariakey";

	private ARIAEngine engine = null;
	
	public ARIACipher() throws InvalidKeyException {
		init(createKey(MASTER_KEY));
	}
	
/*	public ARIACipher(byte[] key) throws InvalidKeyException {
		init(key);
	}
	
	public ARIACipher(String key) throws InvalidKeyException {
		init(createKey(key));
	}*/
	
	private void init(byte[] key) throws InvalidKeyException {
		engine = new ARIAEngine(key.length * 8);
		engine.setKey(key);
		engine.setupRoundKeys();		
	}
	
	/**
	 * ARIA는 key 사이즈가 128, 192, 256 bit 중에 하나 이어야만 한다.
	 * 아래는 단순 MD5를 돌려서 128 bit 키를 만들어낸다. 이 부분을 필요에 따라
	 * 상속하여 문자열에서 적절한 key를 생성하도록 변경하면 된다. (그냥 써도 무방하다)
	 * @param key
	 * @return key 사이즈 규격에 맞는 byte 배
	 * @throws InvalidKeyException
	 */
	protected byte[] createKey(String key) throws InvalidKeyException {
		MessageDigest algorithm = null;

        try {
			algorithm = MessageDigest.getInstance("MD5");
	        algorithm.reset();
	        algorithm.update(key.getBytes());
	        byte[] messageDigest = algorithm.digest();
	        return messageDigest;
		} catch (NoSuchAlgorithmException e) {
			throw new InvalidKeyException(e);
		}
	}
	
	public byte[] encrypt(byte[] data) throws InvalidKeyException {
		byte[] in = PKCS5Padding.pad(data, BLOCK_SIZE);
		byte[] out = new byte[in.length];
		
	    for (int i = 0; i < in.length; i += BLOCK_SIZE) {
	    	engine.encrypt(in, i, out, i);
	    }
	    
	    return out;
	}
	
	public byte[] decrypt(byte[] data) throws InvalidKeyException {
		byte[] out = new byte[data.length];
		
	    for (int i = 0; i < data.length; i += BLOCK_SIZE) {
	    	engine.decrypt(data, i, out, i);
	    }
	    
	    return PKCS5Padding.unpad(out, BLOCK_SIZE);
	}
	
	public String encryptString(String data, String charsetName) throws InvalidKeyException, UnsupportedEncodingException {
		byte[] bytes = encrypt(data.getBytes(charsetName));
		return Base64.encode(bytes);
	}
	
	public String decryptString(String data, String charsetName) throws InvalidKeyException, UnsupportedEncodingException {
		byte[] bytes = decrypt(Base64.decode(data));
		if (bytes == null)
			return null;
		
		return new String(bytes, charsetName);
	}

	public String encryptString(String data) throws InvalidKeyException {
		byte[] bytes = encrypt(data.getBytes());
		return Base64.encode(bytes);
	}
	
	public String decryptString(String data) throws InvalidKeyException {
		byte[] bytes = decrypt(Base64.decode(data));
		if (bytes == null)
			return null;
		
		return new String(bytes);
	}
}
