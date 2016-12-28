package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Chat;

public interface ChatDao extends BaseDao<Chat, Long> {
	public List<Chat> getAllChat(Long userId,Long loginUserid);
}
