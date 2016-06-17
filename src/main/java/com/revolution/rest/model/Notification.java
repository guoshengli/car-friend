 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Table;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 import org.hibernate.annotations.Generated;
 import org.hibernate.annotations.GenerationTime;
 
 @Entity
 @Table(name="notification")
 public class Notification extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 8153506455599601758L;
 
   @Column(name="recipient_id")
   private Long recipientId;
 
   @Column(name="sender_id")
   private Long senderId;
 
   @Column(name="notification_type")
   private int notificationType;
 
   @Column(name="object_type")
   private int objectType;
 
   @Column(name="object_id")
   private Long objectId;
 
   @Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.INSERT)
   @Column(name="create_time", insertable=false, updatable=true)
   private Date create_at;
 
   @Column(name="status")
   private String status;
 
   @Column(name="read_status", columnDefinition="BIT")
   private boolean read_already;
 
   public Long getCreate_at()
   {
     return Long.valueOf(this.create_at.getTime() / 1000L);
   }
 
   public void setCreate_at(Date create_at) {
     this.create_at = create_at;
   }
 
   public boolean getRead_already() {
     return this.read_already;
   }
 
   public void setRead_already(boolean read_already) {
     this.read_already = read_already;
   }
 
   public Long getRecipientId() {
     return this.recipientId;
   }
 
   public void setRecipientId(Long recipientId) {
     this.recipientId = recipientId;
   }
 
   public Long getSenderId() {
     return this.senderId;
   }
 
   public void setSenderId(Long senderId) {
     this.senderId = senderId;
   }
 
   public int getNotificationType() {
     return this.notificationType;
   }
 
   public void setNotificationType(int notificationType) {
     this.notificationType = notificationType;
   }
 
   public int getObjectType() {
     return this.objectType;
   }
 
   public void setObjectType(int objectType) {
     this.objectType = objectType;
   }
 
   public Long getObjectId() {
     return this.objectId;
   }
 
   public void setObjectId(Long objectId) {
     this.objectId = objectId;
   }
 
   public String getStatus() {
     return this.status;
   }
 
   public void setStatus(String status) {
     this.status = status;
   }
 }

