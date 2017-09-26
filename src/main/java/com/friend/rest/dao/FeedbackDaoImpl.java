package com.friend.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Feedback;
@Repository("feedbackDao")
@SuppressWarnings("unchecked")
public class FeedbackDaoImpl extends BaseDaoImpl<Feedback, Long> implements FeedbackDao {

	public FeedbackDaoImpl() {
		super(Feedback.class);
	}

	@Override
	public List<Feedback> getFeedbackList(int count) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Feedback order by create_time desc";
		Query query = session.createQuery(hql);
		query.setMaxResults(count);
		List<Feedback> list = (List<Feedback>)query.list();
		return list;
	}

	@Override
	public List<Feedback> getFeedbackList(int count, Long since_id,
			 int identifier) {

		Feedback foodback = get(since_id);

		List<Feedback> feedbackList = new ArrayList<Feedback>();
		Session session = getSessionFactory().getCurrentSession();
		if (foodback != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(foodback.getCreated_time().longValue() * 1000L);
			String create_time = sdf.format(date);
			String hql = "";
			if (identifier == 1) {
				hql = "from Feedback where 1=1 and created_time >= ? and id !=?  order by create_time desc";

				Query query = session.createQuery(hql);
				query.setString(0,create_time).setLong(1,since_id);
				query.setMaxResults(count);
				feedbackList = query.list();
				Collections.reverse(feedbackList);
			} else if (identifier == 2) {
				hql = "from Feedback where 1=1 and created_time <= ? and id !=?  order by create_time desc";

				Query query = session.createQuery(hql);
				query.setString(0,create_time).setLong(1,since_id);
				query.setMaxResults(count);
				feedbackList = query.list();
			}
		}

	
		return feedbackList;
	}

}
