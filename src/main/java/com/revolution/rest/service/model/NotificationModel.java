 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class NotificationModel
   implements Serializable
 {
   private static final long serialVersionUID = 3524626363855723556L;
   private Long id;
   private Long recipient_id;
   private Long created_at;
   private boolean read_already;
   private JSONObject content;
   private String notification_type;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public Long getRecipient_id() {
     return this.recipient_id;
   }
 
   public void setRecipient_id(Long recipient_id) {
     this.recipient_id = recipient_id;
   }
 
   public Long getCreated_at() {
     return this.created_at;
   }
 
   public void setCreated_at(Long created_at) {
     this.created_at = created_at;
   }
 
   public boolean isRead_already() {
     return this.read_already;
   }
 
   public void setRead_already(boolean read_already) {
     this.read_already = read_already;
   }
 
   public JSONObject getContent() {
     return this.content;
   }
 
   public void setContent(JSONObject content) {
     this.content = content;
   }
 
   public String getNotification_type() {
     return this.notification_type;
   }
 
   public void setNotification_type(String notification_type) {
     this.notification_type = notification_type;
   }
 }

