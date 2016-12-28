package com.revolution.rest.dao;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Chat;
@Repository("chatDao")
@SuppressWarnings("unchecked")
public class ChatDaoImpl extends BaseDaoImpl<Chat, Long> implements ChatDao {

	public ChatDaoImpl() {
		super(Chat.class);
	}

	
	@Override
	public List<Chat> getAllChat(Long userId, Long loginUserid) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Chat where (current_user_id=? and target_user_id=?) or (current_user_id=? and target_user_id=?)";
		System.out.println(hql);
		List<Chat> cList = session.createQuery(hql).setLong(0,userId).setLong(1,loginUserid)
				.setLong(2,loginUserid).setLong(3,userId).list();
		return cList;
	}
	
}
