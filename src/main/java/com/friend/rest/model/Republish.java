 package com.friend.rest.model;
 
 import java.io.Serializable;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

 
 public class Republish extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = -3135674352386712849L;
 
   /*@Column(name="user_id")
   private Long userId;
 
   @Column(name="story_id")
   private Long storyId;*/
   
   @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User repost_users;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="story_id")
	private Story repost_story;
 
   @Column(name="create_time")
   private Date createTime;
 
   @Column(name="type")
   private String type;
 
  /* public Long getUserId()
   {
     return this.userId;
   }
 
   public void setUserId(Long userId) {
     this.userId = userId;
   }
 
   public Long getStoryId() {
     return this.storyId;
   }
 
   public void setStoryId(Long storyId) {
     this.storyId = storyId;
   }*/
 
   public Long getCreateTime() {
     return Long.valueOf(this.createTime.getTime() / 1000L);
   }
 
   public void setCreateTime(Date createTime) {
     this.createTime = createTime;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }

public User getRepost_users() {
	return repost_users;
}

public void setRepost_users(User repost_users) {
	this.repost_users = repost_users;
}

public Story getRepost_story() {
	return repost_story;
}

public void setRepost_story(Story repost_story) {
	this.repost_story = repost_story;
}
   
   
 }

