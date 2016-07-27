package com.revolution.rest.service;

import com.revolution.rest.dao.CommentDao;
import com.revolution.rest.model.Comment;

import java.util.List;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDao commentDao;

	public Response createComment(Comment comment) {
		try {
			this.commentDao.saveOrUpdate(comment);
		} catch (Exception e) {
			return null;
		}
		return Response.status(Response.Status.CREATED).entity("Comment created").build();
	}

	public List<Comment> getAllComment(Long storyId) {
		List<Comment> commentList = this.commentDao.getAllByStoryId(storyId);
		return commentList;
	}

	public Comment getComment(Long commentId) {
		Comment comment = (Comment) this.commentDao.get(commentId);
		return comment;
	}

	public Response updateComment(Long commentId, Comment comment) {
		if (!((Long) comment.getId()).equals(commentId))
			throw new IllegalArgumentException();
		this.commentDao.update(comment);

		return Response.status(Response.Status.OK).entity("Comment updated").build();
	}
}
