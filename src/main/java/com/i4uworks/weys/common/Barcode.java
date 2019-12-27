package com.i4uworks.weys.common;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class Barcode {

	protected static Logger logger = LoggerFactory.getLogger(Barcode.class);
	
	public static String CreateQRCodePng(String codeNum, String path, String folder){
		
		String barcodeUrl = folder + "/" + codeNum.replace(" ", "") + ".png";
		try{
			File file = new File(path + barcodeUrl);

			// 큐알코드 바코드 생성값
			String codeUrl = new String(codeNum.getBytes("UTF-8"), "iso-8859-1");
			// 큐알코드 색깔
			int qrCodeColor = 0xFF000000;
			int backgroundColor = 0xFFFFFFFF;
			
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(codeUrl, BarcodeFormat.QR_CODE, 350, 350);
			
			MatrixToImageConfig matrix = new MatrixToImageConfig(qrCodeColor, backgroundColor);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrix);
			
			ImageIO.write(bufferedImage, "png", file);
			
		} catch (Exception e) {
			logger.info("QR CODE CREATE ERROR : " + e.getMessage());
			return null;
		}
		
		return barcodeUrl;
	}
	
	public static String CreateQrCode(int index) {

		// 바코드 형식은 0000 0000
		String qr = "";
		for(int i=0 ; i<index ; i++){
			int num = (int)(Math.random() * 10000);
			
			String code = "";
			if(num < 10){
				code = "000" + num;
			} else if(num < 100){
				code = "00" + num;
			} else if(num < 1000){
				code = "0" + num;
			} else {
				code = num + "";
			}
			
			if(i == 0){
				qr = code;
			} else {
				qr = qr + " " + code;
			}
		}
		return qr;
	}
}
