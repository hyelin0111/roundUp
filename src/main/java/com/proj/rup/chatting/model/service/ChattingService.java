package com.proj.rup.chatting.model.service;

import java.util.List;
import java.util.Map;

import com.proj.rup.chatting.model.vo.Chatting;

public interface ChattingService {

	List<Chatting> selectChattingList();

	int connectCount();

	int insertChat(Chatting chatting);

	int chatUpload(Map<String, String> map);


}
