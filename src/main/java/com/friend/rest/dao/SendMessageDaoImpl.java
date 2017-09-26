package com.friend.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.SendMessage;
@Repository("sendMessageDao")
@SuppressWarnings("unchecked")
public class SendMessageDaoImpl extends BaseDaoImpl<SendMessage, Long> implements SendMessageDao {

	public SendMessageDaoImpl() {
		super(SendMessage.class);
	}

	@Override
	public List<SendMessage> getSendMessageList(int count, int page) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from SendMessage order by create_time desc";
		int start = (page-1) * count;
		Query query = session.createQuery(hql).setFirstResult(start).setMaxResults(count);
		List<SendMessage> seList = query.list();
		return seList;
	}

	@Override
	public int getSendMessageCount() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select count(1) from SendMessage";
		Query query = session.createQuery(hql);
		List seList = query.list();
		int count = 0;
		if(seList != null && seList.size() > 0){
			count = Integer.parseInt(seList.get(0).toString());
		}
		return count;
	}

	@Override
	public List<SendMessage> getSendMessageListByTitle(int count, int page, String title) {

		Session session = getSessionFactory().getCurrentSession();
		String hql = "from SendMessage where 1=1 and title like :title order by create_time desc";
		int start = (page-1) * count;
		Query query = session.createQuery(hql).setString("title", "%"+title+"%")
				.setFirstResult(start).setMaxResults(count);
		List<SendMessage> seList = query.list();
		return seList;
	
	}

	@Override
	public int getSendMessageCountByTitle(String title) {

		Session session = getSessionFactory().getCurrentSession();
		String hql = "select count(1) from SendMessage where 1=1 and title like :title";
		Query query = session.createQuery(hql).setString("title", "%"+title+"%");
		List seList = query.list();
		int count = 0;
		if(seList != null && seList.size() > 0){
			count = Integer.parseInt(seList.get(0).toString());
		}
		return count;
	
	}

	
}
