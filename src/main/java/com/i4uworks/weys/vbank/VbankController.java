package com.i4uworks.weys.vbank;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.i4uworks.weys.ResValue;
import com.i4uworks.weys.common.ErrCode;

@Controller
@RequestMapping("/api/vbank")
public class VbankController {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private VbankService vbankService;
	
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
	@RequestMapping(value = "/82v/chk", method = RequestMethod.POST)
	public ResValue rsvDone(HttpServletRequest req, HttpServletResponse res, @RequestBody VbankVO reqVO) throws Exception {

		logger.info("===================================== START ===================================");
		logger.info("url ::: " + req.getRequestURL());
		logger.info("req ::: " + reqVO.toString());
		ResValue result = new ResValue();

		if(!reqVO.getMsg().contains("338******67304")){
			logger.info("error ::: 인증 실패");
			result.setResCode(ErrCode.UNKNOWN_ERROR);
			result.setResMsg(ErrCode.getMessage(ErrCode.UNKNOWN_ERROR));
			return result;
		}

		try {
			int resCnt = vbankService.insertVBankMsg(reqVO);
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

}
