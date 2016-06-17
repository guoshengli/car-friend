package com.revolution.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Comment;

@Repository("commentDao")
@SuppressWarnings("unchecked")

public class CommentDaoImpl extends BaseDaoImpl<Comment, Long>implements CommentDao {
	public CommentDaoImpl() {
		super(Comment.class);
	}

	public void saveOrUpdate(Comment comment) {
		Session session = getSessionFactory().getCurrentSession();
		session.save(comment);
	}

	public List<Comment> getAllByStoryId(Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = " from Comment where story.id=? and status=? order by created_time desc";
		List<Comment> commentList = session.createQuery(hql).setLong(0, storyId.longValue()).setString(1, "enabled")
				.list();
		return commentList;
	}

	public void update(Comment comment) {
		Session session = getSessionFactory().getCurrentSession();
		session.update(comment);
	}

	public void replyComment(Comment comment) {
		Session session = getSessionFactory().getCurrentSession();
		session.save(comment);
	}

	public void deleteComment(Comment comment) {
		Session session = getSessionFactory().getCurrentSession();
		session.update(comment);
	}

	public List<Comment> getComments(Long storyId, Long commentId, int count) {
		Comment comment = (Comment) get(commentId);
		List<Comment> commentList = new ArrayList<Comment>();
		if (comment != null) {
			Long createTime = Long.valueOf(comment.getCreated_time().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			Session session = getSessionFactory().getCurrentSession();

			String hql = "from Comment c where c.story.id=? and c.target_user_id is null and c.target_comment_id is null and c.status='enabled' and c.created_time <= ? and c.id != ? order by c.created_time desc";
			Query query = session.createQuery(hql);
			query.setLong(0, storyId.longValue());
			query.setString(1, create_time);
			query.setLong(2, commentId);
			query.setMaxResults(count);
			commentList = query.list();
		}

		return commentList;
	}

	public List<Comment> getComments(Long storyId, int count) {
		String hql = "from Comment c where c.story.id=? and c.status='enabled' and  c.target_user_id is null and c.target_comment_id is null order by c.created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId.longValue());
		query.setMaxResults(count);
		List<Comment> list = query.list();
		return list;
	}

	public List<Comment> getReplyComments(Long commentId, int count) {
		String hql = "from Comment c where c.target_comment_id=? and c.status='enabled' order by c.created_time";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, commentId.longValue());
		query.setMaxResults(count);
		List<Comment> list = query.list();
		return list;
	}

	public List<Comment> getReplyComments(Long targetCommentId, Long commentId, int count) {
		Comment comment = (Comment) get(commentId);
		List<Comment> commentList = new ArrayList<Comment>();
		if (comment != null) {
			Long createTime = Long.valueOf(comment.getCreated_time().longValue() * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date(createTime.longValue()));
			Session session = getSessionFactory().getCurrentSession();

			String hql = "from Comment c where c.target_comment_id=? and c.status='enabled' and c.created_time >= ? and c.id != ? order by c.created_time";
			Query query = session.createQuery(hql);
			query.setLong(0, targetCommentId.longValue());
			query.setString(1, create_time);
			query.setLong(2, commentId.longValue());
			query.setMaxResults(count);
			commentList = query.list();
		}

		return commentList;
	}

	public Comment getCommentByIdAndLoginUserid(Long commentId, Long loginUserid) {
		String hql = "from Comment c where c.id=? and c.user.id=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, commentId.longValue());
		query.setLong(1, loginUserid.longValue());
		List<Comment> list = query.list();
		Comment comment = null;
		if ((list != null) && (list.size() > 0)) {
			comment = (Comment) list.get(0);
		}
		return comment;
	}

	public int getCommentCountById(Long storyId) {
		String hql = "select count(id) from Comment where story.id=? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId);
		query.setString(1, "enabled");

		List<Long> list = query.list();
		int count = 0;
		if ((list != null) && (list.size() > 0)) {
			Long c = (Long) list.get(0);
			count = c.intValue();
		}

		return count;
	}

	public List<Comment> getReplyCommentsById(Long commentId) {
		String hql = "from Comment where target_comment_id=? and status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, commentId.longValue()).setString(1, "enabled");
		List<Comment> list = query.list();
		return list;
	}

	public List<Comment> getCommentByStoryId(Long storyId) {
		String hql = "from Comment c where c.story.id=? and c.status=? order by created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId.longValue()).setString(1, "enabled");
		List<Comment> list = query.list();
		return list;
	}

	public List<Comment> getCommentByStoryIdNewThree(Long storyId) {
		String hql = "from Comment c where c.story.id=? and c.status=? and c.target_comment_id is null and c.target_user_id is null order by c.created_time desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, storyId.longValue()).setString(1, "enabled");
		query.setMaxResults(20);
		List<Comment> list = query.list();
		return list;
	}

	public Comment getCommentByIdAndStatus(Long commentId, String status) {
		String hql = "from Comment c where c.id=? and c.status=?";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setLong(0, commentId.longValue());
		query.setString(1, status);
		List<Comment> list = query.list();
		Comment comment = null;
		if ((list != null) && (list.size() > 0)) {
			comment = (Comment) list.get(0);
		}
		return comment;
	}
}
