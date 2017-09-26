package com.friend.rest.dao;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.ColumnsStory;
import com.friend.rest.model.Story;
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
		String hql = "select cs.story from ColumnsStory cs where cs.columns.id=? and cs.story.status = ? and cs.story.recommendation=true order by cs.create_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,columns_id).setString(1, type);
		query.setMaxResults(count);
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
				hql = "select cs.story from ColumnsStory cs where cs.columns.id=? and cs.story.status=? and cs.create_time <= ? and cs.story.id != ?  order by cs.create_time desc";
				

				Query query = session.createQuery(hql);
				query.setLong(0, columns_id);
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, storyId);
				query.setMaxResults(count);
				storyList = query.list();
				//Collections.reverse(storyList);
			} else if (identifier == 2) {
				hql = "select cs.story from ColumnsStory cs where cs.columns.id=? and cs.story.status=? and cs.create_time <= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.create_time desc";
				Query query = session.createQuery(hql);
				query.setLong(0,columns_id);
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, storyId);
				query.setMaxResults(count);
				storyList = query.list();
			}
		}

		return storyList;
	}

	@Override
	public void deleteColumnsStoryByColumnsIdAndStoryId(Long columns_id, Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from ColumnsStory where 1=1 and columns.id=? and story.id=?";
		Query query = session.createQuery(hql).setLong(0,columns_id).setLong(1,storyId);
		query.executeUpdate();
	}

	@Override
	public ColumnsStory getColumnsStoryByColumnsIdAndStoryId(Long columnsId, Long storyId) {
		String hql = "from ColumnsStory cs where 1=1 and cs.columns.id=? and cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,columnsId).setLong(1,storyId);
		List<ColumnsStory> csList = query.list();
		ColumnsStory cs = null;
		if(csList != null && csList.size() > 0){
			cs = csList.get(0);
		}
		return cs;
	}

	@Override
	public List<ColumnsStory> getColumnsStoryListByStoryId(Long storyId) {
		String hql = "from ColumnsStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,storyId);
		List<ColumnsStory> csList = query.list();
		return csList;
	}

	@Override
	public void deleteColumnsSotryList(String ids) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from ColumnsStory where 1=1 and id in (?)";
		Query query = session.createQuery(hql).setString(0,ids);
		query.executeUpdate();
		
	}


}
