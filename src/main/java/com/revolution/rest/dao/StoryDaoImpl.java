package com.revolution.rest.dao;

import com.google.common.base.Strings;
import com.revolution.rest.model.Story;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.IteratorUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

@Repository("storyDao")
@SuppressWarnings("unchecked")
public class StoryDaoImpl extends BaseDaoImpl<Story, Long>implements StoryDao {
	public StoryDaoImpl() {
		super(Story.class);
	}

	public void update(Story entity) {
		Session session = getSessionFactory().getCurrentSession();
		session.update(entity);
	}

	public List<Story> getAllByUserId(Long userId) {
		String hql = " from Story where author_id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setLong(0, userId);
		Iterator<Story> ite = query.iterate();
		List<Story> storyList = IteratorUtils.toList(ite);
		return storyList;
	}

	public List<Story> getAllByCollectionId(Long collectionId) {
		String hql = "from Story where collection_id=?";
		Session session = getSessionFactory().getCurrentSession();
		List<Story> storyList = session.createQuery(hql).setLong(0, collectionId.longValue()).list();
		return storyList;
	}

	public void delete(Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		Story story = (Story) session.get(Story.class, storyId);
		story.setStatus("disabled");
		session.update(story);
	}

	public List<Story> getDraftStories(Long userId) {
		String hql = "from Story s where s.user.id=? and s.status=? order by s.created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setString(1, "unpublish");
		List<Story> storyList = query.list();
		return storyList;
	}

	public List<Story> getStoriesPageByNull(Long userId, int count, String type, Long loginUserid) {
		String hql = "";
		/*if (userId.equals(loginUserid))
			hql = "from Story s where s.user.id=? and (s.status=? or s.status = 'disabled') order by s.created_time desc";
		else {
			hql = "from Story s where s.user.id=? and s.status=? order by s.created_time desc";
		}*/
		hql = "from Story s where s.user.id=? and s.status=? order by s.created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, userId.longValue());
		query.setString(1, type);
		query.setMaxResults(count);
		List<Story> storyList = query.list();
		return storyList;
	}

	public List<Story> getStoriesPageByStoryId(Long userId, int count, Long since_id, int identifier, String type,
			Long loginUserid) {
		Story story = get(since_id);
		List<Story> storyList = new ArrayList<Story>();
		if (story != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(story.getCreated_time().longValue() * 1000L);
			String create_time = sdf.format(date);
			Session session = getSessionFactory().getCurrentSession();

			String hql = "";
			if (identifier == 1) {
				/*if (userId.equals(loginUserid)) {
					hql = "from Story s where s.user.id=? and (s.status=? or s.status='disabled')and s.created_time >= ? and id != ? order by s.created_time desc";
				} else {
					hql = "from Story s where s.user.id=? and s.status=? and s.created_time >= ? and id != ? order by s.created_time desc";
				}*/
				
				hql = "from Story s where s.user.id=? and s.status=? and s.created_time >= ? and id != ? order by s.created_time desc";

				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, since_id.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				Collections.reverse(storyList);
			} else if (identifier == 2) {
				/*if (userId.equals(loginUserid)) {
					hql = "from Story s where s.user.id=? and (s.status=? or s.status='disabled') and s.created_time <= ? and id != ? order by s.created_time";
				} else {
					hql = "from Story s where s.user.id=? and s.status=? and s.created_time <= ? and id != ? order by s.created_time";
				}*/
				hql = "from Story s where s.user.id=? and s.status=? and s.created_time <= ? and id != ? order by s.created_time desc";
				Query query = session.createQuery(hql);
				query.setLong(0, userId.longValue());
				query.setString(1, type);
				query.setString(2, create_time);
				query.setLong(3, since_id.longValue());
				query.setMaxResults(count);
				storyList = query.list();
			}
		}

		return storyList;
	}

	public int getStoryCount(Long userId) {
		String hql = "select count(id) from Story where user.id=? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		List<Long> list = session.createQuery(hql).setLong(0, userId.longValue()).setString(1, "publish").list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long l = list.get(0);
			count = l.intValue();
		}
		return count;
	}

	public Story getStoryByIdAndLoginUserid(Long id, Long loginUserid) {
		String hql = "from Story s where s.id = ? and s.user.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, id.longValue());
		query.setLong(1, loginUserid.longValue());

		List<Story> list = query.list();
		Story story = null;
		if ((list != null) && (list.size() > 0)) {
			story = (Story) list.get(0);
		}
		return story;
	}

	public Story getStoryByIdAndStatus(Long id, String status1, String status2) {
		String hql = "from Story where id = ? and (status=? or status = ?)";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, id.longValue());
		query.setString(1, status1);
		query.setString(2, status2);
		List<Story> list = query.list();
		Story story = null;
		if ((list != null) && (list.size() > 0)) {
			story = (Story) list.get(0);
		}
		return story;
	}

	public List<Story> getStoriesByNow(Long authorId, String status) {
		String hql = "from Story where user.id=? and status=? and title is not null order by created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, authorId.longValue());
		query.setString(1, "publish");
		query.setMaxResults(3);
		List<Story> list = query.list();
		return list;
	}

	public List<Story> getStoriesByRandThree(Long storyId) {
		String hql = "from Story where 1=1 and status=? and id != ? and recommendation=1 and title is not null";

		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, "publish").setLong(1, storyId);
		int size = query.list().size();
		Random r = new Random();
		query.setMaxResults(3);
		if (size == 3)
			query.setFirstResult(0);
		else {
			query.setFirstResult(r.nextInt(Math.abs(size - 3)) + 1);
		}

		List<Story> list = query.list();
		return list;
	}

	public List<Story> getStoriesByTimeAndNull(int count, String type, String since_date, String max_date) {
		String hql = "";
		List<Story> storyList = new ArrayList<Story>();
		Session session = getSessionFactory().getCurrentSession();
		if ((!Strings.isNullOrEmpty(max_date)) && (!Strings.isNullOrEmpty(since_date))) {
			hql = "from Story s where s.status=? and s.created_time between ? and ? order by s.created_time desc";
			Query query = session.createQuery(hql);
			query.setString(0, type);
			query.setString(1, since_date);
			query.setString(2, max_date);
			query.setMaxResults(count);
			storyList = query.list();
		} else {
			hql = "from Story s where s.status=? order by s.created_time desc";
			Query query = session.createQuery(hql);
			query.setString(0, type);
			query.setMaxResults(count);
			storyList = query.list();
		}
		return storyList;
	}

	public List<Story> getStoriesByTime(Long storyId, int count, int identifier, String type, String since_date,
			String max_date) {
		Story story = (Story) get(storyId);
		List<Story> storyList = new ArrayList<Story>();
		if (story != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(story.getCreated_time().longValue() * 1000L);
			String create_time = sdf.format(date);
			Session session = getSessionFactory().getCurrentSession();
			String hql = "";
			if ((!Strings.isNullOrEmpty(max_date)) && (!Strings.isNullOrEmpty(since_date))) {
				if (identifier == 1) {
					hql = "from Story s where s.status=? and s.created_time >= ? and id != ? and s.created_time between ? and ?  order by s.created_time";

					Query query = session.createQuery(hql);
					query.setString(0, type);
					query.setString(1, create_time);
					query.setLong(2, storyId.longValue());
					query.setString(3, since_date + " 00:00:00");
					query.setString(4, max_date + " 23:59:59");
					query.setMaxResults(count);
					storyList = query.list();
					Collections.reverse(storyList);
				} else if (identifier == 2) {
					hql = "from Story s where 1=1 and s.status=? and s.created_time <= ? and id != ? and s.created_time between ? and ? order by s.created_time desc";

					Query query = session.createQuery(hql);
					query.setString(0, type);
					query.setString(1, create_time);
					query.setLong(2, storyId.longValue());
					query.setString(3, since_date + " 00:00:00");
					query.setString(4, max_date + " 23:59:59");
					query.setMaxResults(count);
					storyList = query.list();
				}
			} else if (identifier == 1) {
				hql = "from Story s where s.status=? and s.created_time >= ? and id != ? order by s.created_time";

				Query query = session.createQuery(hql);
				query.setString(0, type);
				query.setString(1, create_time);
				query.setLong(2, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				Collections.reverse(storyList);
			} else if (identifier == 2) {
				hql = "from Story s where 1=1 and s.status=? and s.created_time <= ? and id != ? order by s.created_time desc";

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

	public List<Story> getStoriesByViewAndNull(int count, String type, String since_date, String max_date) {
		String hql = "";
		Session session = getSessionFactory().getCurrentSession();
		List<Story> storyList = new ArrayList<Story>();
		if ((!Strings.isNullOrEmpty(max_date)) && (!Strings.isNullOrEmpty(since_date))) {
			hql = "from Story s where s.status=? and s.created_time between ? and ? order by s.viewTimes desc";
			Query query = session.createQuery(hql);
			query.setString(0, type);
			query.setString(1, since_date);
			query.setString(2, max_date);
			query.setMaxResults(count);
			storyList = query.list();
		} else {
			hql = "from Story s where s.status=? order by s.viewTimes desc";

			Query query = session.createQuery(hql);
			query.setString(0, type);
			query.setMaxResults(count);
			storyList = query.list();
		}
		return storyList;
	}

	public List<Map<String, Object>> getStoriesByView(Long storyId, int count, int identifier, String type,
			String since_date, String max_date) {
		Story story = get(storyId);

		List<Map<String, Object>> storyList = new ArrayList<Map<String, Object>>();
		Session session = getSessionFactory().getCurrentSession();
		if (story != null) {
			int viewTimes = story.getViewTimes();
			String hql = "";
			if ((!Strings.isNullOrEmpty(max_date)) && (!Strings.isNullOrEmpty(since_date))) {
				if (identifier == 1) {
					hql = "select * from (select @rownum\\:=@rownum+1 as rownum, s.* from Story s,(select @rownum\\:=0) t where s.status=? and s.view_count >= ? and s.created_time between ? and ? ORDER BY view_count desc) r where rownum > (select rownum from (select @rownum\\:=@rownum+1 as rownum, s.* from Story s,(select @rownum\\:=0) t where s.status=? and s.view_count >= ? and s.created_time between ? and ? ORDER BY s.viewTimes desc) r where id =?)";

					Query query = session.createQuery(hql);
					query.setString(0, type);
					query.setInteger(1, viewTimes);
					query.setLong(2, storyId.longValue());
					query.setString(3, since_date + " 00:00:00");
					query.setString(4, max_date + " 23:59:59");
					query.setString(5, type);
					query.setInteger(6, viewTimes);
					query.setLong(7, storyId.longValue());
					query.setString(8, since_date + " 00:00:00");
					query.setString(9, max_date + " 23:59:59");
					query.setMaxResults(count);
					storyList = query.list();
					Collections.reverse(storyList);
				} else if (identifier == 2) {
					hql = "select * from (select @rownum\\:=@rownum+1 as rownum, s.* from story s,(select @rownum\\:=0) t where s.status=? and s.view_count <= ? and s.created_time between ? and ? ORDER BY view_count desc) r where rownum > (select rownum from (select @rownum\\:=@rownum+1 as rownum, s.* from story s,(select @rownum\\:=0) t where s.status=? and s.view_count <= ? and s.created_time between ? and ? ORDER BY s.view_count desc) r where id =?)";

					Query query = session.createQuery(hql);
					query.setString(0, type);
					query.setInteger(1, viewTimes);
					query.setLong(2, storyId.longValue());
					query.setString(3, since_date + " 00:00:00");
					query.setString(4, max_date + " 23:59:59");
					query.setString(5, type);
					query.setInteger(6, viewTimes);
					query.setLong(7, storyId.longValue());
					query.setString(8, since_date + " 00:00:00");
					query.setString(9, max_date + " 23:59:59");
					query.setMaxResults(count);
					storyList = query.list();
				}
			} else if (identifier == 1) {
				hql = "select * from (select @rownum\\:=@rownum+1 as rownum, s.* from story s,(select @rownum\\:=0) t where s.status=? and s.view_count >= ? ORDER BY view_count desc) r where rownum > (select rownum from (select @rownum\\:=@rownum+1 as rownum, s.* from story s,(select @rownum\\:=0) t where s.status=? and s.view_count >= ? ORDER BY s.view_count desc) r where id =?)";

				Query query = session.createQuery(hql);
				query.setString(0, type);
				query.setInteger(1, viewTimes);
				query.setString(2, type);
				query.setInteger(3, viewTimes);
				query.setLong(4, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
				Collections.reverse(storyList);
			} else if (identifier == 2) {
				hql = "select * from (select @rownum\\:=@rownum+1 as rownum, s.* from story s,(select @rownum\\:=0) t where s.status=? and s.view_count <= ? ORDER BY view_count desc) r where rownum > (select rownum from (select @rownum\\:=@rownum+1 as rownum, s.* from story s,(select @rownum\\:=0) t where s.status=? and s.view_count <= ? ORDER BY s.view_count desc) r where id =?)";

				Query query = session.createSQLQuery(hql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				query.setString(0, type);
				query.setInteger(1, viewTimes);
				query.setString(2, type);
				query.setInteger(3, viewTimes);
				query.setLong(4, storyId.longValue());
				query.setMaxResults(count);
				storyList = query.list();
			}

		}

		return storyList;
	}

	public Story getStoryByIdAndStatus(Long id, String status) {
		String hql = "from Story where id = ? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, id.longValue());
		query.setString(1, status);
		List<Story> list = query.list();
		Story story = null;
		if ((list != null) && (list.size() > 0)) {
			story = list.get(0);
		}
		return story;
	}

	@Override
	public Story getStoryByURL(String url) {
		String hql = "from Story where 1=1 and tinyURL = ? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, url);
		query.setString(1, "publish");
		List<Story> list = query.list();
		Story story = null;
		if ((list != null) && (list.size() > 0)) {
			story = list.get(0);
		}
		return story;
	}

	@Override
	public List<Story> getStoryByTime(String start, String end) {
		// TODO Auto-generated method stub
		String hql = "from Story where 1=1 and status=? and recommendation =true and recommend_date between ? and ? order by viewTimes desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, "publish");
		query.setString(1, start + " 00:00");
		query.setString(2, start + " 23:59");
		query.setMaxResults(3);
		List<Story> list = query.list();
		return list;
	}

	@Override
	public List<Story> getStoryByFour(Long userId) {
		String hql = "from Story where 1=1 and status=? and user.id=? order by created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, "publish").setLong(1,userId);
		query.setMaxResults(4);
		List<Story> list = query.list();
		return list;
	}

	@Override
	public void updateStoryResource(Long storyId, String resource) {
		String hql = "update Story set resource = ? where 1=1 and id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql).setString(0, resource).setLong(1, storyId);
		query.executeUpdate();
	}

	@Override
	public List<Story> getStoryListByIds(String ids) {
		String hql = "from Story where 1=1 and id in (?)";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setString(0, ids);
		List<Story> list = query.list();
		return list;
	}
}
