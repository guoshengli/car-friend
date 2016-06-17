package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Feedback;

public interface FeedbackDao extends BaseDao<Feedback, Long> {
	
	public List<Feedback> getFeedbackList(int count);
	
	public List<Feedback> getFeedbackList(int count, Long since_id,int identifier) ;
}
