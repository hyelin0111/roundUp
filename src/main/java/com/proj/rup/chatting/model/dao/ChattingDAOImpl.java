package com.proj.rup.chatting.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proj.rup.chatting.model.vo.Chatting;

@Repository
public class ChattingDAOImpl implements ChattingDAO {

	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Override
	public List<Chatting> selectChattingList() {
		// TODO Auto-generated method stub
		return sqlSession.selectList("chatting.selectChattingList");
	}

	@Override
	public int connectCount() {
		return sqlSession.selectOne("chatting.connectCount");
	}

	@Override
	public int insertChat(Chatting chatting) {
		return sqlSession.insert("chatting.insertChat",chatting);
	}

	@Override
	public int chatUpload(Map<String, String> map) {
		return sqlSession.insert("chatting.chatUpload",map);
	}



	
	
}
