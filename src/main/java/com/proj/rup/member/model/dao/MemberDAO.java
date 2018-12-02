package com.proj.rup.member.model.dao;

import java.util.List;
import java.util.Map;

import com.proj.rup.member.model.vo.Address;
import com.proj.rup.member.model.vo.Member;
import com.proj.rup.member.model.vo.Membership;
import com.proj.rup.member.model.vo.Question;
import com.proj.rup.member.model.vo.QuestionFile;

public interface MemberDAO {

	int insertMember(Member member);

	Member selectOneMember(String member_id);

	int checkIdDuplicate(String member_id);

	int updateMember(Member member);

	int connectMember(Member member);

	int selectMember(String member_id);

	int totalMember();

	int deleteConnect(String member_id);

	int insertAddress(Map<String, Object> map);

	Map<String, Object> selectConnectMember(String username);

	int updateAddress(Map<String, Object> map);

	Membership selectMembership(String memberId);

	int updateMembership(Map<String, Object> map);

	int deleteMember(String member_id);

	List<Question> selectQuestionList(int cPage, int numPerPage);

	int selectQuestionListCount(String member_id);

	int insertBoard(Question question);

	int insertFile(QuestionFile fbf);

	Question selectQuestion(int no);

	List<Address> selectAddrList(String member_id);

	int deleteMemberAddress(int address_no);

	int updateAddressLevel(Map<String, Object> map);

	int updateAddressLevelByAddrNo(int address_no);

	int selectAddrLevel(String member_id);

}
