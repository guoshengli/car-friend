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
 @Table(name="report")
 public class Report extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = -386557138305532813L;
 
   @Column(name="recipient_id")
   private Long recipient_id;
 
   @Column(name="sender_id")
   private Long sender_id;
 
   @Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.ALWAYS)
   @Column(name="create_time", insertable=false)
   private Date create_time;
 
   @Column(name="type")
   private String type;
 
   @Column(name="object_type")
   private int object_type;
 
   @Column(name="object_id")
   private Long object_id;
 
   @Column(name="status")
   private String status;
 
   @Column(name="operator_id")
   private Long operator_id;
 
   public Long getRecipient_id()
   {
     return this.recipient_id;
   }
 
   public void setRecipient_id(Long recipient_id) {
     this.recipient_id = recipient_id;
   }
 
   public Long getSender_id() {
     return this.sender_id;
   }
 
   public void setSender_id(Long sender_id) {
     this.sender_id = sender_id;
   }
 
   public Long getCreate_time() {
     return Long.valueOf(this.create_time.getTime() / 1000L);
   }
 
   public void setCreate_time(Date create_time) {
     this.create_time = create_time;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public int getObject_type() {
     return this.object_type;
   }
 
   public void setObject_type(int object_type) {
     this.object_type = object_type;
   }
 
   public Long getObject_id() {
     return this.object_id;
   }
 
   public void setObject_id(Long object_id) {
     this.object_id = object_id;
   }
 
   public String getStatus() {
     return this.status;
   }
 
   public void setStatus(String status) {
     this.status = status;
   }
 
   public Long getOperator_id() {
     return this.operator_id;
   }
 
   public void setOperator_id(Long operator_id) {
     this.operator_id = operator_id;
   }
 }

