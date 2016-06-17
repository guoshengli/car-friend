 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Table;
 
 @Entity
 @Table(name="linked_accounts")
 public class LinkAccounts extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 4551831834861252688L;
 
   @Column(name="auth_token")
   private String auth_token;
 
   @Column(name="description")
   private String description;
 
   @Column(name="refreshed_at")
   private String refreshed_at;
 
   @Column(name="service")
   private String service;
 
   @Column(name="uuid")
   private String uuid;
 
   @Column(name="user_id")
   private Long user_id;
 
   @Column(name="avatar_url")
   private String avatar_url;
 
   public String getAuth_token()
   {
     return this.auth_token;
   }
 
   public void setAuth_token(String auth_token) {
     this.auth_token = auth_token;
   }
 
   public String getDescription() {
     return this.description;
   }
 
   public void setDescription(String description) {
     this.description = description;
   }
 
   public String getRefreshed_at() {
     return this.refreshed_at;
   }
 
   public void setRefreshed_at(String refreshed_at) {
     this.refreshed_at = refreshed_at;
   }
 
   public String getService() {
     return this.service;
   }
 
   public void setService(String service) {
     this.service = service;
   }
 
   public String getUuid() {
     return this.uuid;
   }
 
   public void setUuid(String uuid) {
     this.uuid = uuid;
   }
 
   public Long getUser_id() {
     return this.user_id;
   }
 
   public void setUser_id(Long user_id) {
     this.user_id = user_id;
   }
 
   public String getAvatar_url() {
     return this.avatar_url;
   }
 
   public void setAvatar_url(String avatar_url) {
     this.avatar_url = avatar_url;
   }
 }

