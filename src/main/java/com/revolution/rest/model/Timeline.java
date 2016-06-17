 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import javax.xml.bind.annotation.XmlTransient;

 
 @Entity
 @Table(name="activity")
 public class Timeline extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = -3786437137630475742L;
 
   @Column(name="creator_id")
   private Long creatorId;
 
   @Column(name="target_user_id")
   private Long targetUserId;
 
   @XmlTransient
   @ManyToOne(fetch=FetchType.LAZY)
   @JoinColumn(name="story_id")
   private Story story;
 
  
   @Column(name="create_time")
   private Date createTime;
 
   @Column(name="reference_id")
   private Long referenceId;
 
   @Column(name="type")
   private String type;
 
   public Long getCreatorId()
   {
     return this.creatorId;
   }
 
   public void setCreatorId(Long creatorId) {
     this.creatorId = creatorId;
   }
 
   public Long getTargetUserId() {
     return this.targetUserId;
   }
 
   public void setTargetUserId(Long targetUserId) {
     this.targetUserId = targetUserId;
   }
 
   public Long getCreateTime()
   {
     return Long.valueOf(this.createTime.getTime() / 1000L);
   }
 
   public Story getStory() {
     return this.story;
   }
 
   public void setStory(Story story) {
     this.story = story;
   }
 
   public void setCreateTime(Date createTime) {
     this.createTime = createTime;
   }
 
   public Long getReferenceId() {
     return this.referenceId;
   }
 
   public void setReferenceId(Long referenceId) {
     this.referenceId = referenceId;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 }

