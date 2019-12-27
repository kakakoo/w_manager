package com.i4uworks.weys.login;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface LoginDao {

	LoginInfoVO selectAdmInfo(String adminId);

	int insertTokeninfo(LoginInfoVO info);

	void insertLog(int adminKey);

	int selectCheckToken(String token);

	int updateTokeninfo(LoginInfoVO reqVO);

	int updateLogout(int adminKey);

	String selectStoreNm(int adminKey);

	List<Map<String, Object>> selectSmsList();

	List<Integer> selectNonMember();

	int selectMemberBarcodeCnt(String barcode);

	int insertMemberInfo(Map<String, Object> insertMap);

	int insertMemberActive(Map<String, Object> insertMap);

	List<Integer> selectNonQrUsr();

	int selectBarcodeCnt(String barcode);

	void updateUsrBarcode(Map<String, Object> insertMap);

	List<NoticeVO> selectAdminNoice(int adminKey);

	int updateNoticeRead(Map<String, Object> reqMap);

}
