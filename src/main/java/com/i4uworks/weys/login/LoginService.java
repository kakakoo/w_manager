package com.i4uworks.weys.login;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.i4uworks.weys.common.ErrCode;
import com.i4uworks.weys.common.AriaUtils;
import com.i4uworks.weys.common.Barcode;
import com.i4uworks.weys.config.TokenHandler;
import com.i4uworks.weys.config.TokenValues;
import com.i4uworks.weys.ResValue;

@Service
public class LoginService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private LoginDao loginDao;

	@Value("#{props['ENC.KEY']}")
	private String ENC_KEY;
	@Value("#{props['IB.SERVICE.ID']}")
	private String IB_SERVICE_ID;
	@Value("#{props['IB.SERVICE.PWD']}")
	private String IB_SERVICE_PWD;
	@Value("#{props['IB.FROM.TEL']}")
	private String IB_FROM_TEL;

	@Value("${UPLOAD.PATH}")
	private String UPLOAD_PATH;
	
	public TokenValues checkToken(HttpServletRequest req, HttpServletResponse res, ResValue resultVal) {
		
		TokenValues result = new TokenValues();
		try{
			// 토큰 인증 
			result = TokenHandler.getTokenValues(req, res);
			String token = (String) req.getAttribute("HeadToken");
			// 해당 토큰 관련된 유저가 있는지 확인, 유저키 불러와서 토큰에 있는 유저키와 맞는지 비교. 
			int tokenCnt = loginDao.selectCheckToken(token);
			
			if(tokenCnt == 0){
				resultVal.setResCode(ErrCode.ACCESS_CRASH);
				resultVal.setResMsg(ErrCode.getMessage(ErrCode.ACCESS_CRASH));
				return null;
			}
		}catch (Exception e) {
			resultVal.setResCode(ErrCode.INVALID_TOKEN);
			resultVal.setResMsg(ErrCode.getMessage(ErrCode.INVALID_TOKEN));
			return null;
		}
		
		return result;
	}

	public ResValue updateLoginResult(LoginInfoVO reqVO) throws Exception {

		ResValue res = new ResValue();
		/**
		 * 아이디 비밀번호 확인
		 */
		LoginInfoVO info = loginDao.selectAdmInfo(reqVO.getAdminId());
		
		if(info.getAdminId() == null){
			/**
			 * 존재하지 않는 아이디
			 */
			res.setResCode(ErrCode.LOGIN_FAILED);
			res.setResMsg(ErrCode.getMessage(ErrCode.LOGIN_FAILED));
		} else {
			String inputPw = reqVO.getAdminPw();
			String originPw = info.getAdminPw();
			inputPw = AriaUtils.encryptPassword(inputPw, info.getAdminId());

			/**
			 * 비밀번호 불일치
			 */
			if(!inputPw.equals(originPw)){
				res.setResCode(ErrCode.LOGIN_FAILED);
				res.setResMsg(ErrCode.getMessage(ErrCode.LOGIN_FAILED));
				return res;
			}
			
			/**
			 * 토큰 생성 및 접속로그 등록
			 */
			String token = TokenHandler.getToken(info); 
			Calendar cal = Calendar.getInstance();
			Date dt = new Date();
			cal.setTime(dt);
			cal.add(Calendar.DATE, 365);
			Date d = cal.getTime();
			
			info.setTokenExpireDttm(d);
			info.setTokenAdm(token);
			info.setOs(reqVO.getOs());
			
			if(reqVO.getUuid() != null)
				info.setUuid(reqVO.getUuid());
			
			int result = loginDao.insertTokeninfo(info);
			
			if(result > 0){
				loginDao.insertLog(info.getAdminKey());
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("tokenAdm", token);
				resMap.put("adminId", info.getAdminId());
				resMap.put("adminNm", info.getAdminName());
				resMap.put("storeNm", info.getStoreNm());
				resMap.put("adminTp", info.getAdminTp());

				res.setResData(resMap);
			} else {
				logger.info("error ::: 토큰 업데이트 실패");
				res.setResCode(ErrCode.UNKNOWN_ERROR);
				res.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			}
		}
		return res;
	}


	public ResValue updateAutoLoginResult(TokenValues value, LoginInfoVO reqVO) throws Exception {

		ResValue res = new ResValue();
		
		reqVO.setAdminKey(value.getAdminKey());
		reqVO.setAdminId(value.getAdminId());
		reqVO.setStores(value.getStores());
		
		/**
		 * 새 토큰등록
		 */
		String token = TokenHandler.getToken(reqVO); 
		reqVO.setTokenAdm(token);
		Calendar cal = Calendar.getInstance();
		Date dt = new Date();
		cal.setTime(dt);
		cal.add(Calendar.DATE, 365);
		Date d = cal.getTime();
		
		reqVO.setTokenExpireDttm(d);
		int result = loginDao.updateTokeninfo(reqVO);
		/**
		 * 로그인 등록
		 */
		if(result > 0){
			loginDao.insertLog(value.getAdminKey());
			
			LoginInfoVO info = loginDao.selectAdmInfo(reqVO.getAdminId());
			
			Map<String, Object> resMap = new HashMap<>();
			resMap.put("tokenAdm", token);
			resMap.put("adminId", value.getAdminId());
			resMap.put("adminNm", info.getAdminName());
			resMap.put("storeNm", info.getStoreNm());
			resMap.put("adminTp", info.getAdminTp());

			res.setResData(resMap);
		} else {
			logger.info("error ::: 토큰 업데이트 실패");
			res.setResCode(ErrCode.UNKNOWN_ERROR);
			res.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}
		
		return res;
	}

	public int updateLogout(int adminKey) {
		return loginDao.updateLogout(adminKey);
	}

	public ResValue selectSmsList() {
		
		List<Map<String, Object>> smsList = loginDao.selectSmsList();
		Map<String, Object> resMap = new HashMap<>();
		resMap.put("dataList", smsList);
		
		ResValue res = new ResValue();
		res.setResData(resMap);
		return res;
	}
	
	public Map<String, Object> insertUsrMember() throws Exception {
		
		List<Integer> usrList = loginDao.selectNonMember();
		
		for(int usrId : usrList){
			boolean checkBarcode = true;
			String barcode = "";
			while(checkBarcode){
				barcode = Barcode.CreateQrCode(2);
				int checkCnt = loginDao.selectMemberBarcodeCnt(barcode);
				if(checkCnt == 0){
					checkBarcode = false;
				}
			}
			// QR CODE 이미지 생성
			String qrCodeUrl = Barcode.CreateQRCodePng(barcode, UPLOAD_PATH, "qrmem");
			if(qrCodeUrl == null){
				throw new Exception("서버 내부 에러");
			}
			
			int joinCost = 0;
			
			Map<String, Object> insertMap = new HashMap<>();
			insertMap.put("usrId", usrId);
			insertMap.put("barcode", barcode);
			insertMap.put("barcodeUrl", qrCodeUrl);
			insertMap.put("endDt", "2019.09.19");
			insertMap.put("cost", joinCost);
			insertMap.put("txt", "신규가입");
			
			int res = loginDao.insertMemberInfo(insertMap);
			
			if(res > 0){
				res = loginDao.insertMemberActive(insertMap);
			}
		}
		
		return null;
	}

	public Map<String, Object> selectAdminNoice(int adminKey) {
		
		List<NoticeVO> noticeList = loginDao.selectAdminNoice(adminKey);
		Map<String, Object> result = new HashMap<>();
		result.put("dataList", noticeList);
		return result;
	}

	public int updateNoticeRead(int anId, int adminKey) {
		
		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("adminKey", adminKey);
		reqMap.put("anId", anId);

		return loginDao.updateNoticeRead(reqMap);
	}

}
