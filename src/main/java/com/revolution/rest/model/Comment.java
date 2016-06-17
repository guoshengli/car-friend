 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 import javax.persistence.Transient;
 import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Generated;
 import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
 
 @Entity
 @Table(name="comment")
 public class Comment extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = -3892512537630475742L;
 
   @Transient
   private Long userId;
 
   @XmlTransient
   @ManyToOne
   @JoinColumn(name="user_id")
   private User user;
 
   @Transient
   private Long story_id;
 
   @ManyToOne(fetch=FetchType.LAZY)
   @JoinColumn(name="story_id")
   private Story story;
 
   @Column(name="content", columnDefinition="LONGTEXT")
   private String content;
   
   @Column(name="comment_image")
   @Type(type="text")
   private String comment_image;
 
   @Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.ALWAYS)
   @Column(name="create_time", insertable=false, updatable=false)
   private Date created_time;
 
   @Column(name="target_user_id")
   private Long target_user_id;
 
   @Column(name="target_comment_id")
   private Long target_comment_id;
 
   @Column(name="status")
   private String status;
 
   public Long getUserId()
   {
     if (this.user != null) {
       setUserId((Long)this.user.getId());
     }
     return this.userId;
   }
 
   public void setUserId(Long userId) {
     this.userId = userId;
   }
 
   @XmlTransient
   public User getUser() {
     return this.user;
   }
 
   public void setUser(User user) {
     this.user = user;
   }
 
   public Long getStory_id() {
     if (this.story != null) {
       setStory_id((Long)this.story.getId());
     }
     return this.story_id;
   }
 
   public void setStory_id(Long story_id) {
     this.story_id = story_id;
   }
 
   @XmlTransient
   public Story getStory() {
     return this.story;
   }
 
   public void setStory(Story story) {
     this.story = story;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public Long getCreated_time() {
     return Long.valueOf(this.created_time.getTime() / 1000L);
   }
 
   public void setCreated_time(Date created_time) {
     this.created_time = created_time;
   }
 
   public Long getTarget_user_id() {
     return this.target_user_id;
   }
 
   public void setTarget_user_id(Long target_user_id) {
     this.target_user_id = target_user_id;
   }
 
   public String getStatus() {
     return this.status;
   }
 
   public void setStatus(String status) {
     this.status = status;
   }
 
   public Long getTarget_comment_id() {
     return this.target_comment_id;
   }
 
   public void setTarget_comment_id(Long target_comment_id) {
     this.target_comment_id = target_comment_id;
   }

	public String getComment_image() {
		return comment_image;
	}
	
	public void setComment_image(String comment_image) {
		this.comment_image = comment_image;
	}
   
   
   
 }

