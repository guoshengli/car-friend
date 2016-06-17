 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import java.util.Date;
 import javax.persistence.AssociationOverrides;
 import javax.persistence.Column;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Entity;
 import javax.persistence.Table;

 
 @Entity
 @Table(name="follow")
 @AssociationOverrides({@javax.persistence.AssociationOverride(name="pk.user", joinColumns={@javax.persistence.JoinColumn(name="user_id")}), @javax.persistence.AssociationOverride(name="pk.follower", joinColumns={@javax.persistence.JoinColumn(name="follower_id")})})
 public class Follow
   implements Serializable
 {
   private static final long serialVersionUID = 2524659555729848644L;
 
   @EmbeddedId
   private FollowId pk = new FollowId();
 
   @Column(name="create_time")
   public Date createTime;
 
   public FollowId getPk() { return this.pk; }
 
   public void setPk(FollowId pk)
   {
     this.pk = pk;
   }
 
   public Long getCreateTime() {
     return createTime.getTime() / 1000L;
   }
 
   public void setCreateTime(Date createTime) {
     this.createTime = createTime;
   }
 }

