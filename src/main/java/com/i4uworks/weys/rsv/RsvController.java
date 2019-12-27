package com.i4uworks.weys.rsv;

import java.util.HashMap;
import java.util.List;
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

import com.i4uworks.weys.ResValue;
import com.i4uworks.weys.common.ErrCode;
import com.i4uworks.weys.common.VersionCheck;
import com.i4uworks.weys.config.TokenValues;
import com.i4uworks.weys.login.LoginInfoVO;
import com.i4uworks.weys.login.LoginService;

@Controller
@RequestMapping("/api/rsv")
public class RsvController {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private LoginService loginService;
	@Autowired
	private RsvService rsvService;

	/**
	 * 예약자 리스트
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}", method = RequestMethod.GET)
	public ResValue getRsvList(HttpServletRequest req, HttpServletResponse res, RsvReqVO reqVO,
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
		 * 파라미터 필수값 체크
		 */
		if (reqVO.getListTp() == null) {
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
			List<Map<String, Object>> resMap = rsvService.selectRsvList(reqVO, value);
			result.setResData(resMap);
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
	 * 예약정보 상세 보기
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/{rsvId}", method = RequestMethod.GET)
	public ResValue rsvDetail(HttpServletRequest req, HttpServletResponse res, @PathVariable String rsvId,
			@PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
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
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			RsvInfoVO info = rsvService.selectRsvDetail(rsvId, value.getAdminKey());
			result.setResData(info);
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
	 * 예약준비 완료
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/{rsvId}", method = RequestMethod.PUT)
	public ResValue updateSt(HttpServletRequest req, HttpServletResponse res, @PathVariable String rsvId,
			@PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
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
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			int resCnt = rsvService.updateRsvSt(value.getAdminKey(), rsvId);
			if(resCnt == 0){
				logger.info("error ::: 업데이트 실패");
				result.setResCode(ErrCode.UNKNOWN_ERROR);
				result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			} else if (resCnt == -1){
				logger.info("error ::: 취소된 예약입니다.");
				result.setResCode(ErrCode.RSV_ALREADY_CANCEL);
				result.setResMsg(ErrCode.getMessage(ErrCode.RSV_ALREADY_CANCEL));
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
	
	/**
	 * 예약자 확인
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/check", method = RequestMethod.GET)
	public ResValue checkRsv(HttpServletRequest req, HttpServletResponse res, RsvInfoVO reqVO,
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
		 * 파라미터 필수값 체크
		 */
		if (reqVO.getRsvQr() == null) {
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
			reqVO.setAdminKey(value.getAdminKey());
			RsvInfoVO info = rsvService.selectRsvInfo(reqVO);
			if(info == null){
				logger.error("error ::: 존재하지 않는 예약 QR 코드");
				result.setResCode(ErrCode.RSV_NOT_EXIST);
				result.setResMsg(ErrCode.getMessage(ErrCode.RSV_NOT_EXIST));
			} else {
				result.setResData(info);
//				if(info.getAdminSt().equals("N")){
//					logger.error("error ::: 준비되지 않은 QR 코드");
//					result.setResCode(ErrCode.RSV_NOT_READY);
//					result.setResMsg(ErrCode.getMessage(ErrCode.RSV_NOT_READY));
//				} else {
//					result.setResData(info);
//				}
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

	/**
	 * 예약 완료
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/{rsvId}", method = RequestMethod.POST)
	public ResValue rsvDone(HttpServletRequest req, HttpServletResponse res, @RequestBody RsvDoneVO reqVO
			, @PathVariable String rsvId, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.getRsvSign());
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
		
		// 필수 ::: 필수 파라미터 체크 
		if(reqVO.getRsvSign() == null){
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
			reqVO.setAdminKey(value.getAdminKey());
			reqVO.setRsvId(Integer.parseInt(rsvId));
			String regDttm = rsvService.updateRsvDone(reqVO);
			if(regDttm.equals("")){
				logger.info("error ::: 업데이트 실패");
				result.setResCode(ErrCode.UNKNOWN_ERROR);
				result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			} else {
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("regDttm", regDttm);
				result.setResData(resMap);
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

	/**
	 * 인수 내역
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/accept", method = RequestMethod.GET)
	public ResValue accept(HttpServletRequest req, HttpServletResponse res, @PathVariable String version) throws Exception {

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
			Map<String, Object> info = rsvService.selectAcceptCnt(value.getAdminKey());
			result.setResData(info);
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
	 * 인수 완료
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/accept", method = RequestMethod.POST)
	public ResValue acceptDone(HttpServletRequest req, HttpServletResponse res, @RequestBody Map<String, Object> reqMap
			, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqMap.toString());
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
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			reqMap.put("adminKey", value.getAdminKey());
			String readyDttm = rsvService.updateAcceptDone(reqMap);
			if(readyDttm.equals("")){
				logger.info("error ::: 업데이트 실패");
				result.setResCode(ErrCode.UNKNOWN_ERROR);
				result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			} else {
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("readyDttm", readyDttm);
				result.setResData(resMap);
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

	/**
	 * 그룹 인수 내역
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/grp/accept", method = RequestMethod.GET)
	public ResValue grpAccept(HttpServletRequest req, HttpServletResponse res, RsvReqVO reqVO
			, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.getBarcode());
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
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			Map<String, Object> resMap = rsvService.selectGrpList(reqVO.getBarcode());
			result.setResData(resMap);
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
	 * 그룹 인수 완료
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/grp/accept", method = RequestMethod.POST)
	public ResValue grpAcceptDone(HttpServletRequest req, HttpServletResponse res, @RequestBody Map<String, Object> reqMap
			, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqMap.toString());
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
		 * 필수 ::: 토큰값 체크
		 */
		TokenValues value = loginService.checkToken(req, res, result);
		if (value == null) {
			logger.info("result ::: " + result.toString());
			logger.info("===================================== END =====================================");
			return result;
		}

		try {
			reqMap.put("adminKey", value.getAdminKey());
			String readyDttm = rsvService.updateGrpAcceptDone(reqMap);
			if(readyDttm == null){
				logger.info("error ::: 잘못된 인수 프로세스");
				result.setResCode(ErrCode.ADM_GRP_NOT_CORRECT);
				result.setResMsg(ErrCode.getMessage(ErrCode.ADM_GRP_NOT_CORRECT));
			} else {
				Map<String, Object> resMap = new HashMap<>();
				resMap.put("readyDttm", readyDttm);
				result.setResData(resMap);
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

	/**
	 * 예약일, 시간 변경
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/{rsvId}/changeDt", method = RequestMethod.PUT)
	public ResValue changeDt(HttpServletRequest req, HttpServletResponse res, @PathVariable String rsvId,
			@PathVariable String version, @RequestBody RsvReqVO reqVO) throws Exception {

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
			reqVO.setAdminKey(value.getAdminKey());
			reqVO.setRsvId(Integer.parseInt(rsvId));
			int resCnt = rsvService.updateRsvDt(reqVO);
			if(resCnt == 0){
				logger.info("error ::: 업데이트 실패");
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

	/**
	 * 상담등록
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/{rsvId}/memo", method = RequestMethod.POST)
	public ResValue memo(HttpServletRequest req, HttpServletResponse res, @RequestBody RsvReqVO reqVO
			, @PathVariable String rsvId, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.toString());
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
		if(!reqVO.checkMemo()){
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
			reqVO.setRsvId(Integer.parseInt(rsvId));
			int resCnt = rsvService.insertUsrMemo(reqVO);
			if(resCnt == 0){
				logger.info("error ::: 상담등록 실패");
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

	/**
	 * 상담등록
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/sms/{rsvId}", method = RequestMethod.GET)
	public ResValue smsText(HttpServletRequest req, HttpServletResponse res
			, @PathVariable String rsvId, @PathVariable String version) throws Exception {

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
			Map<String, Object> dataList = rsvService.selectSmsText(rsvId);
			result.setResData(dataList);
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
	 * 인수인계 내역 보기
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/transfer", method = RequestMethod.GET)
	public ResValue getTransfer(HttpServletRequest req, HttpServletResponse res, @PathVariable String version) throws Exception {

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
			Map<String, Object> resultMap = rsvService.selectTransferInfo(value.getAdminKey());
			result.setResData(resultMap);
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
	 * 인수인계
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/transfer", method = RequestMethod.POST)
	public ResValue transfer(HttpServletRequest req, HttpServletResponse res, @RequestBody LoginInfoVO reqVO
			, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.toString());
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
		 * 로그인 필수값 체크
		 */
		if (!reqVO.checkLogin()) {
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
			result = rsvService.updateTransfer(reqVO, value);
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
	 * 예약자 리스트
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/center", method = RequestMethod.GET)
	public ResValue centerList(HttpServletRequest req, HttpServletResponse res,
			@PathVariable String version) throws Exception {

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
			Map<String, Object> resultMap = rsvService.selectCenterRsv(value.getAdminKey());
			result.setResData(resultMap);
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
	 * 지점 관리 리스트
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/store", method = RequestMethod.GET)
	public ResValue storeMngList(HttpServletRequest req, HttpServletResponse res, RsvReqVO reqVO, @PathVariable String version) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.getRsvDt());
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
			reqVO.setAdminKey(value.getAdminKey());
			List<Map<String, Object>> rsvList = rsvService.selectStoreList(reqVO); 
			result.setResData(rsvList);
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
	 * 지점 관리 내역 리스트
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/store/{code}", method = RequestMethod.GET)
	public ResValue storeRSsvList(HttpServletRequest req, HttpServletResponse res, RsvReqVO reqVO, @PathVariable String version
			, @PathVariable String code) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.getCodeTp());
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
			reqVO.setSearchTxt(code);
			reqVO.setAdminKey(value.getAdminKey());
			Map<String, Object> rsvInfo = rsvService.selectStoreRsvList(reqVO); 
			result.setResData(rsvInfo);
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
	 * 예약자 리스트
	 * @param req
	 * @param res
	 * @param reqVO
	 * @param version
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/{version}/center/allday", method = RequestMethod.GET)
	public ResValue centerAllDayList(HttpServletRequest req, HttpServletResponse res,
			@PathVariable String version) throws Exception {

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
			Map<String, Object> resultMap = rsvService.selectCenterAllDayRsv(value.getAdminKey());
			result.setResData(resultMap);
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
