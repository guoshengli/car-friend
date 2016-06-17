 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
 
 @Entity
 @Table(name="likes")
 public class Likes extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 6232345736768601758L;
 
   @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User like_users;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="story_id")
	private Story like_story;
 
   @Column(name="create_time")
   private Date createTime;
 
   
 
   public User getLike_users() {
		return like_users;
	}
	
	public void setLike_users(User like_users) {
		this.like_users = like_users;
	}
	
	public Story getLike_story() {
		return like_story;
	}
	
	public void setLike_story(Story like_story) {
		this.like_story = like_story;
	}

	public Long getCreateTime() {
     return Long.valueOf(this.createTime.getTime() / 1000L);
	}
 
   public void setCreateTime(Date createTime) {
     this.createTime = createTime;
   }
 }

