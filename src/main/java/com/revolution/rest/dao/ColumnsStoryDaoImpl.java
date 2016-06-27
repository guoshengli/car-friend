package com.revolution.rest.dao;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.ColumnsStory;
import com.revolution.rest.model.Story;
@Repository("columnsStoryDao")
@SuppressWarnings("unchecked")
public class ColumnsStoryDaoImpl extends BaseDaoImpl<ColumnsStory, Long>implements ColumnsStoryDao {

	public ColumnsStoryDaoImpl() {
		super(ColumnsStory.class);
	}
	
	@Override
	public Story getStoryByStoryid(Long storyId) {
		String hql = "select cs.story from ColumnsStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,storyId);
		List<Story> csList = query.list();
		Story story = null;
		if(csList != null && csList.size() > 0){
			story = csList.get(0);
		}
		return story;
	}
	
	
	public ColumnsStory getColumnsStoryByStoryId(Long storyId) {
		String hql = "from ColumnsStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,storyId);
		List<ColumnsStory> csList = query.list();
		ColumnsStory cs = null;
		if(csList != null && csList.size() > 0){
			cs = csList.get(0);
		}
		return cs;
	}

	
	@Override
	public List<Story> getStoriesByColumns(Long columns_id, int count,String type) {
		String hql = "select cs.story from ColumnsStory cs where cs.columns.id=? and cs.story.status = ? order by cs.create_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		List<Story> list = query.list();
		return list;
	}

	@Override
	public List<Story> getStoriesPageByColumns(Long columns_id, int count, Long storyId, int identifier,String type) {
		Story story = getStoryByStoryid(storyId);
		ColumnsStory cs = getColumnsStoryByStoryId(storyId);
		List<Story> storyList = new ArrayList<Story>();
		if (story != null) {
			Session session = getSessionFactory().getCurrentSession();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(cs.getCreate_time());
			String hql = "";
			if (identifier == 1) {
				hql = "select cs.story from CollectionStory cs where cs.columns.id=? and cs.story.status=? and cs.create_time <= ? and cs.story.id != ?  order by cs.create_time desc";
				

				Query query = session.createQuery(hql);
				query.setLong(0, columns_id);
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				//Collections.reverse(storyList);
			} else if (identifier == 2) {
				hql = "select cs.story from CollectionStory cs where cs.columns.id=? and cs.story.status=? and cs.create_time <= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.create_time desc";
				Query query = session.createQuery(hql);
				query.setLong(0,columns_id);
				query.setString(0, type);
				query.setString(1, create_time);
				query.setLong(2, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
			}
		}

		return storyList;
	}


}