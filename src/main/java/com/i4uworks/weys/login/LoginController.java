package com.i4uworks.weys.login;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.i4uworks.weys.config.TokenValues;
import com.i4uworks.weys.common.VersionCheck;
import com.i4uworks.weys.ResValue;
import com.i4uworks.weys.common.ErrCode;

@Controller
@RequestMapping("/api")
public class LoginController {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private LoginService loginService;

	@ResponseBody
	@RequestMapping(value = "/{version}/login", method = RequestMethod.POST)
	public ResValue login(HttpServletRequest req, HttpServletResponse res, @RequestBody LoginInfoVO reqVO,
			@PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.toString());
		ResValue result = new ResValue();

		/**
		 * 필수 ::: API 버전 체크
		 */
		int ver = VersionCheck.checkVersion(version, 4);
		if(ver == 0){
			result.setResCode(ErrCode.VERSION_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("result ::: " + ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("===================================== END =====================================");
			return result;
		}
		
		/**
		 * 로그인 필수값 체크
		 */
		if (!reqVO.checkLogin()) {
			result.setResCode(ErrCode.INVALID_PARAMETER);
			result.setResMsg(ErrCode.getMessage(ErrCode.INVALID_PARAMETER));
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			result = loginService.updateLoginResult(reqVO);
		} catch (Exception e) {
			logger.error("error ::: " + e.getMessage());
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}

		logger.info("result ::: " + result.toString());
		logger.info("===================================== END =====================================");
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/{version}/auto", method = RequestMethod.POST)
	public ResValue autoLogin(HttpServletRequest req, HttpServletResponse res, @RequestBody LoginInfoVO reqVO,
			@PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.toString());
		ResValue result = new ResValue();

		// 필수 ::: API 버전 체크
		int ver = VersionCheck.checkVersion(version, 3);
		if(ver == 0){
			result.setResCode(ErrCode.VERSION_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("result ::: " + ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("===================================== END =====================================");
			return result;
		}

		/**
		 * 로그인 필수값 체크
		 */
		if (reqVO.getOs() == null) {
			result.setResCode(ErrCode.INVALID_PARAMETER);
			result.setResMsg(ErrCode.getMessage(ErrCode.INVALID_PARAMETER));
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		/**
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			result = loginService.updateAutoLoginResult(value, reqVO);
		} catch (Exception e) {
			logger.error("error ::: " + e.getMessage());
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}

		logger.info("result ::: " + result.toString());
		logger.info("===================================== END =====================================");
		return result;
	}
	

	@ResponseBody
	@RequestMapping(value = "/{version}/logout", method = RequestMethod.DELETE)
	public ResValue logout(HttpServletRequest req, HttpServletResponse res,
			@PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		ResValue result = new ResValue();

		/**
		 * 필수 ::: API 버전 체크
		 */
		int ver = VersionCheck.checkVersion(version, 3);
		if(ver == 0){
			result.setResCode(ErrCode.VERSION_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("result ::: " + ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("===================================== END =====================================");
			return result;
		}
		
		/**
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			int resCnt = loginService.updateLogout(value.getAdminKey());
			if(resCnt == 0){
				result.setResCode(ErrCode.UNKNOWN_ERROR);
				result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			}
		} catch (Exception e) {
			logger.error("error ::: " + e.getMessage());
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}

		logger.info("result ::: " + result.toString());
		logger.info("===================================== END =====================================");
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/{version}/sms", method = RequestMethod.GET)
	public ResValue getSms(HttpServletRequest req, HttpServletResponse res, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		ResValue result = new ResValue();

		// 필수 ::: API 버전 체크
		int ver = VersionCheck.checkVersion(version, 4);
		if(ver == 0){
			result.setResCode(ErrCode.VERSION_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("result ::: " + ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("===================================== END =====================================");
			return result;
		}

		/**
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			result = loginService.selectSmsList();
		} catch (Exception e) {
			logger.error("error ::: " + e.getMessage());
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}

		logger.info("result ::: " + result.toString());
		logger.info("===================================== END =====================================");
		return result;
	}

	/**
	 * 휴대폰번호 인증 
	 */
	@ResponseBody
	@RequestMapping(value ="/{version}/member", method=RequestMethod.GET)
	public ResValue member(HttpServletRequest req, HttpServletResponse res, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		ResValue result = new ResValue();

		// 필수 ::: API 버전 체크
		int ver = VersionCheck.checkVersion(version, 4);
		if(ver == 0){
			result.setResCode(ErrCode.VERSION_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("result ::: " + ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("===================================== END =====================================");
			return result;
		}
		
		try{
			Map<String, Object> resultMap = loginService.insertUsrMember();
			
		} catch(Exception e) {
			logger.info("error ::: " + e.getMessage());
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}

		logger.info("result ::: " + result.toString());
		logger.info("===================================== END =====================================");
		return result;
	}
	
	/**
	 * 공지사항 
	 */
	@ResponseBody
	@RequestMapping(value ="/{version}/notice", method=RequestMethod.GET)
	public ResValue notice(HttpServletRequest req, HttpServletResponse res, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		ResValue result = new ResValue();

		// 필수 ::: API 버전 체크
		int ver = VersionCheck.checkVersion(version, 4);
		if(ver == 0){
			result.setResCode(ErrCode.VERSION_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("result ::: " + ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("===================================== END =====================================");
			return result;
		}
		
		/**
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}
		
		try{
			Map<String, Object> resultMap = loginService.selectAdminNoice(value.getAdminKey());
			result.setResData(resultMap);
		} catch(Exception e) {
			logger.info("error ::: " + e.getMessage());
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}

		logger.info("result ::: " + result.toString());
		logger.info("===================================== END =====================================");
		return result;
	}

	/**
	 * 공지사항 읽기
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/notice", method = RequestMethod.POST)
	public ResValue rsvDone(HttpServletRequest req, HttpServletResponse res, @RequestBody NoticeVO reqVO
			, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.getAnId());
		ResValue result = new ResValue();

		// 필수 ::: API 버전 체크
		int ver = VersionCheck.checkVersion(version, 4);
		if(ver == 0){
			result.setResCode(ErrCode.VERSION_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("result ::: " + ErrCode.getMessage(ErrCode.VERSION_ERROR));
			logger.info("===================================== END =====================================");
			return result;
		}
		
		// 필수 ::: 필수 파라미터 체크 
		if(reqVO.getAnId() == 0){
			result.setResCode(ErrCode.INVALID_PARAMETER);
			result.setResMsg(ErrCode.getMessage(ErrCode.INVALID_PARAMETER));
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}
				
		/**
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			int resCnt = loginService.updateNoticeRead(reqVO.getAnId(), value.getAdminKey());
			if(resCnt == 0){
				result.setResCode(ErrCode.UNKNOWN_ERROR);
				result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			}
		} catch (Exception e) {
			logger.error("error ::: " + e.getMessage());
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
		}

		logger.info("result ::: " + result.toString());
		logger.info("===================================== END =====================================");
		return result;
	}

}
