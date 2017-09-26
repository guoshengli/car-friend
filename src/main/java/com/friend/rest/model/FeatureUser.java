 package com.friend.rest.model;
 
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Table;
 
 public class FeatureUser extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = -4960047882901350211L;
 
   @Column(name="userId")
   private Long userId;
 
   public Long getUserId()
   {
     return this.userId;
   }
 
   public void setUserId(Long userId) {
     this.userId = userId;
   }
 }

