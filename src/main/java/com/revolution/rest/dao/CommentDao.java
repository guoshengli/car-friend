package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Comment;

public interface CommentDao extends BaseDao<Comment, Long>
{
  public List<Comment> getAllByStoryId(Long paramLong);

  public void replyComment(Comment paramComment);

  public void deleteComment(Comment paramComment);

  public List<Comment> getComments(Long paramLong, int paramInt);

  public List<Comment> getComments(Long paramLong1, Long paramLong2, int paramInt);

  public List<Comment> getReplyComments(Long paramLong, int paramInt);

  public List<Comment> getReplyComments(Long paramLong1, Long paramLong2, int paramInt);

  public Comment getCommentByIdAndLoginUserid(Long paramLong1, Long paramLong2);

  public int getCommentCountById(Long paramLong);

  public List<Comment> getReplyCommentsById(Long paramLong);

  public List<Comment> getCommentByStoryId(Long paramLong);

  public List<Comment> getCommentByStoryIdNewThree(Long paramLong);

  public Comment getCommentByIdAndStatus(Long paramLong, String paramString);
  
  public List<Comment> getCommentByStoryIdAndRandom(Long storyId,int count);
}
