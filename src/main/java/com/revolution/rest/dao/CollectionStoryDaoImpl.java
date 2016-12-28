package com.revolution.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.revolution.rest.model.Collection;
import com.revolution.rest.model.CollectionStory;
import com.revolution.rest.model.Story;

@Repository("collectionStoryDao")
@SuppressWarnings("unchecked")
public class CollectionStoryDaoImpl extends BaseDaoImpl<CollectionStory, Long>implements CollectionStoryDao {
	public CollectionStoryDaoImpl() {
		super(CollectionStory.class);
	}

	public Story getStoryByCollectionIdAndStoryId(Long collectionId, Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.id=?";

		List<Story> list = session.createQuery(hql).setLong(0, collectionId.longValue()).setLong(1, storyId.longValue())
				.list();
		Story story = null;
		if ((list != null) && (list.size() > 0)) {
			story = (Story) list.get(0);
		}
		return story;
	}

	public void delete(Long collectionId, Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = " from CollectionStory cs where cs.collection.id=? and cs.story.id=?";

		List<CollectionStory> list = session.createQuery(hql).setLong(0, collectionId.longValue())
				.setLong(1, storyId.longValue()).list();
		CollectionStory cs = null;
		if ((list != null) && (list.size() > 0)) {
			cs = (CollectionStory) list.get(0);
		}
		session.delete(cs);
	}

	public List<Story> getStoriesPage(Long collectionId, int count, String type) {
		String hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? order by cs.story.created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, collectionId.longValue());
		query.setString(1, type);
		query.setMaxResults(count);
		List<Story> storyList = query.list();
		return storyList;
	}

	public List<Story> getStoriesPageByCollectionId(Long collectionId, int count, Long storyId, int identifier,
			String type) {
		Story story = getStoryByCollectionIdAndStoryId(collectionId, storyId);
		List<Story> storyList = new ArrayList<Story>();
		if (story != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(story.getCreated_time().longValue() * 1000L);
			String create_time = sdf.format(date);
			Session session = getSessionFactory().getCurrentSession();

			String hql = "";
			if (identifier == 1) {
				hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.story.created_time >= ? and cs.story.id != ? order by cs.story.created_time";

				Query query = session.createQuery(hql);
				query.setLong(0, collectionId.longValue());
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				Collections.reverse(storyList);
			} else if (identifier == 2) {
				hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.story.created_time <= ? and cs.story.id != ? order by cs.story.created_time desc";

				Query query = session.createQuery(hql);
				query.setLong(0, collectionId.longValue());
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
			}
		}

		return storyList;
	}

	public CollectionStory getCollectionStoryByCollectionIdAndStoryId(Long collectionId, Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CollectionStory cs where cs.collection.id=? and cs.story.id=?";

		List<CollectionStory> list = session.createQuery(hql).setLong(0, collectionId.longValue())
				.setLong(1, storyId.longValue()).list();
		CollectionStory cs = null;
		if ((list != null) && (list.size() > 0)) {
			cs = (CollectionStory) list.get(0);
		}
		return cs;
	}

	public List<Story> getStoriesByCollectionId(Long collectionId) {
		String hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.story.recommendation=true order by cs.create_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, collectionId.longValue());
		query.setString(1, "publish");
		query.setMaxResults(5);
		List<Story> storyList = query.list();
		return storyList;
	}

	public void deleteByCollectionId(String collectionIds) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from CollectionStory where 1=1 and id in (?)";
		Query query = session.createQuery(hql).setString(0, collectionIds);
		query.executeUpdate();
	}

	public List<CollectionStory> getCollectionStoryByCollectionId(Long collectionId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CollectionStory where 1=1 and collection.id=?";
		Query query = session.createQuery(hql).setLong(0, collectionId.longValue());
		List<CollectionStory> list = query.list();
		return list;
	}

	public Collection getCollectionByStoryId(Long storyId) {
		String hql = "select cs.collection from CollectionStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId.longValue());
		List<Collection> cList = query.list();
		Collection c = null;
		if (cList != null && cList.size() > 0) {
			c = cList.get(0);
		}
		return c;
	}
	
	public List<Collection> getCollectionListByStoryId(Long storyId) {
		String hql = "select cs.collection from CollectionStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId.longValue());
		List<Collection> cList = query.list();
		return cList;
	}

	public void deleteCollectionStoryByCollectionId(Long collectionId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from CollectionStory where 1=1 and collection.id = ?";
		Query query = session.createQuery(hql).setLong(0, collectionId.longValue());
		query.executeUpdate();
	}

	public void deleteCollectionStoryByCollectionIdAndStoryId(Long collectionId, Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from CollectionStory where 1=1 and collection.id = ? and story.id=?";
		Query query = session.createQuery(hql).setLong(0, collectionId.longValue()).setLong(1, storyId.longValue());
		query.executeUpdate();
	}

	public List<Story> getFeturedStoriesPage(Long collectionId, int count, String type) {
		//String hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.story.recommendation=true order by cs.story.recommend_date desc";
		String hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? order by cs.create_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, collectionId.longValue());
		query.setString(1, type);
		query.setMaxResults(count);
		List<Story> storyList = query.list();
		return storyList;
	}

	public List<Story> getFeturedStoriesPageByCollectionId(Long collectionId, int count, Long storyId, int identifier,
			String type) {
		//Story story = getStoryByCollectionIdAndStoryId(collectionId, storyId);
		CollectionStory cs = getCollectionStoryByCollectionIdAndStoryId(collectionId, storyId);
		List<Story> storyList = new ArrayList<Story>();
		if (cs != null) {
			Session session = getSessionFactory().getCurrentSession();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//String create_time = story.getRecommend_date();
			String create_time = sdf.format(cs.getCreate_time());
			String hql = "";
			if (identifier == 1) {
				//hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.story.recommend_date >= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.story.recommend_date";
				hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.create_time >= ? and cs.story.id != ? and order by cs.create_time";

				Query query = session.createQuery(hql);
				query.setLong(0, collectionId.longValue());
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				Collections.reverse(storyList);
			} else if (identifier == 2) {
				//hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.story.recommend_date <= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.story.recommend_date desc";
				hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=? and cs.create_time <= ? and cs.story.id != ? order by cs.create_time desc";

				Query query = session.createQuery(hql);
				query.setLong(0, collectionId.longValue());
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
			}
		}

		return storyList;
	}
	
	
	public List<Story> getFeturedStoriesFollow(String ids, int count, String type) {
		String hql = "";
		if(!Strings.isNullOrEmpty(ids)){
			hql = "select distinct cs.story from CollectionStory cs where cs.collection.id in ("+ids+") and cs.story.status=? and cs.story.recommendation=true order by cs.create_time desc";
		}else{
			hql = "select distinct cs.story from CollectionStory cs where cs.story.status=? and cs.story.recommendation=true order by cs.create_time desc";
		}
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, type);
		query.setMaxResults(count);
		List<Story> storyList = query.list();
		return storyList;
	}

	public List<Story> getFeturedStoriesPageByCollections(String ids, int count, Long storyId, int identifier,
			String type) {
		Story story = getStoryByStoryid(storyId);
		CollectionStory cs = getCollectionStoryByStoryId(storyId);
		List<Story> storyList = new ArrayList<Story>();
		if (story != null) {
			Session session = getSessionFactory().getCurrentSession();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(cs.getCreate_time());
			String hql = "";
			if (identifier == 1) {
				if(!Strings.isNullOrEmpty(ids)){
					hql = "select cs.story from CollectionStory cs where cs.collection.id in ("+ids+") and cs.story.status=? and cs.create_time <= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.create_time desc";
				}else{
					hql = "select cs.story from CollectionStory cs where cs.story.status=? and cs.create_time <= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.create_time desc";
				}
				

				Query query = session.createQuery(hql);
				query.setString(0, type);
				query.setString(1, create_time);
				query.setLong(2, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				//Collections.reverse(storyList);
			} else if (identifier == 2) {
				if(!Strings.isNullOrEmpty(ids)){
					hql = "select cs.story from CollectionStory cs where cs.collection.id in ("+ids+") and cs.story.status=? and cs.create_time <= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.create_time desc";
				}else{
					hql = "select cs.story from CollectionStory cs where cs.story.status=? and cs.create_time <= ? and cs.story.id != ? and cs.story.recommendation=true order by cs.create_time desc";
				}
				Query query = session.createQuery(hql);
				query.setString(0, type);
				query.setString(1, create_time);
				query.setLong(2, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
			}
		}

		return storyList;
	}

	@Override
	public List<Story> getStoryByRand(int collection) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select cs.story from CollectionStory cs where cs.story.status=? and cs.story.recommendation=true "
				+ "ORDER BY rand()";

		Query query = session.createQuery(hql).setString(0, "publish").setMaxResults(collection);
		List<Story> list = query.list();
		return list;
	}

	@Override
	public List<Story> getStoryByCollectionIds(String ids) {
		
		String hql = "select cs.story from CollectionStory cs where cs.collection.id in (?) and cs.story.status=? and cs.story.recommendation=true order by cs.story.recommend_date desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, ids);
		query.setString(1, "publish");
		List<Story> storyList = query.list();
		return storyList;
	}

	@Override
	public CollectionStory getCollectionStoryByStoryId(Long storyId) {
		String hql = "from CollectionStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,storyId);
		List<CollectionStory> csList = query.list();
		CollectionStory cs = null;
		if(csList != null && csList.size() > 0){
			cs = csList.get(0);
		}
		return cs;
	}

	@Override
	public Story getStoryByStoryid(Long storyId) {
		String hql = "select cs.story from CollectionStory cs where cs.story.id=?";
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

	@Override
	public List<Story> getStoryByCollectionIds(String ids, int count) {
		String hql = "select distinct cs.story from CollectionStory cs where cs.collection.id in ("+ids+") and cs.story.status='publish' order by cs.create_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setMaxResults(count);
		List<Story> storyList = query.list();
		return storyList;
	}

	@Override
	public List<Story> getStoryByCollectionIds(String ids, int count, Long storyId, int identifier) {

		Story story = getStoryByStoryid(storyId);
		CollectionStory cs = getCollectionStoryByStoryId(storyId);
		List<Story> storyList = new ArrayList<Story>();
		if (story != null) {
			Session session = getSessionFactory().getCurrentSession();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(cs.getCreate_time());
			String hql = "";
			if (identifier == 1) {
				hql = "select distinct cs.story from CollectionStory cs where cs.collection.id in ("+ids+") and cs.story.status='publish' and cs.create_time <= ? and cs.story.id != ? order by cs.create_time desc";
				Query query = session.createQuery(hql);
				query.setString(0, create_time);
				query.setLong(1, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				//Collections.reverse(storyList);
			} else if (identifier == 2) {
				hql = "select distinct cs.story from CollectionStory cs where cs.collection.id in ("+ids+") and cs.story.status='publish' and cs.create_time <= ? and cs.story.id != ? order by cs.create_time desc";
				Query query = session.createQuery(hql);
				query.setString(0, create_time);
				query.setLong(1, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
			}
		}

		return storyList;
	
	}

	@Override
	public List<Story> getStoriesByCollectionIdAndRecommand(Long collectionId, int count,String type) {
		String hql = "select cs.story from CollectionStory cs where cs.collection.id = ? and cs.story.status=? "
				+ "and cs.story.recommendation = true order by cs.create_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,collectionId);
		query.setString(1,type);
		query.setMaxResults(count);
		List<Story> storyList = query.list();
		return storyList;
	}

	@Override
	public List<Story> getStoriesByCollectionIdAndRecommand(Long collectionId, Long storyId, int count, int identify,String type) {
		Story story = getStoryByStoryid(storyId);
		List<Story> storyList = null;
		String hql = "";
		if (story != null) {
			storyList = new ArrayList<Story>();
			Session session = getSessionFactory().getCurrentSession();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(story.getRecommend_date());
			if(identify == 1){
				hql = "select cs.story from CollectionStory cs where cs.collection.id = ? and cs.story.id != ? and cs.story.recommend_date <= ? and cs.story.status=? "
						+ "and cs.story.recommendation = true order by cs.create_time desc";
				Query query = session.createQuery(hql);
				query.setLong(0,collectionId);
				query.setLong(1,storyId);
				query.setString(2,create_time);
				query.setString(3,type);
				query.setMaxResults(count);
				storyList = query.list();
			}else if(identify == 2){
				hql = "select cs.story from CollectionStory cs where cs.collection.id = ? and cs.story.id != ? and cs.story.recommend_date <= ? and cs.story.status=? "
						+ "and cs.story.recommendation = true order by cs.create_time desc";
				Query query = session.createQuery(hql);
				query.setLong(0,collectionId);
				query.setLong(1,storyId);
				query.setString(2,create_time);
				query.setString(3,type);
				query.setMaxResults(count);
				storyList = query.list();
			}
		}
		
		return storyList;
	}

	@Override
	public List<Story> getStoriesByCollectionIdAndHot(Long collectionId, int count,String type) {
		String hql = "select cs.story from CollectionStory cs where cs.collection.id = ? and cs.story.status=? "
				+ "order by cs.story.last_comment_date desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,collectionId);
		query.setString(1,type);
		query.setMaxResults(count);
		List<Story> storyList = query.list();
		return storyList;
	}

	@Override
	public List<Story> getStoriesByCollectionIdAndHot(Long collectionId, Long storyId, int count, int identify,String type) {
		Story story = getStoryByStoryid(storyId);
		List<Story> storyList = null;
		String hql = "";
		if (story != null) {
			storyList = new ArrayList<Story>();
			Session session = getSessionFactory().getCurrentSession();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(story.getLast_comment_date());
			if(identify == 1){
				hql = "select cs.story from CollectionStory cs where cs.collection.id = ? and cs.story.id != ? and cs.story.last_comment_date <= ? and cs.story.status=? "
						+ " order by cs.story.last_comment_date desc";
				Query query = session.createQuery(hql);
				query.setLong(0,collectionId);
				query.setLong(1,storyId);
				query.setString(2,create_time);
				query.setString(3,type);
				query.setMaxResults(count);
				storyList = query.list();
			}else if(identify == 2){
				hql = "select cs.story from CollectionStory cs where cs.collection.id = ? and cs.story.id != ? and cs.story.last_comment_date <= ? and cs.story.status=? "
						+ " order by cs.story.last_comment_date desc";
				Query query = session.createQuery(hql);
				query.setLong(0,collectionId);
				query.setLong(1,storyId);
				query.setString(2,create_time);
				query.setString(3,type);
				query.setMaxResults(count);
				storyList = query.list();
			}
		}
		
		return storyList;
	}

	@Override
	public void deleteCollectionStoryByStoryId(Long storyId) {
		String hql = "delete from CollectionStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		session.createQuery(hql).setLong(0,storyId).executeUpdate();
	}

	@Override
	public List<CollectionStory> getCollectionStorysByStoryId(Long storyId) {
		String hql = "from CollectionStory cs where cs.story.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0,storyId);
		List<CollectionStory> csList = query.list();
		return csList;
	}

	@Override
	public int getStoriesByCount(Long collectionId) {
		String hql = "select cs.story from CollectionStory cs where cs.collection.id=? and cs.story.status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, collectionId.longValue());
		query.setString(1, "publish");
		int count = 0;
		List<Story> storyList = query.list();
		if(storyList != null && storyList.size() > 0){
			count = storyList.size();
		}
		return count;
	}
	
}
