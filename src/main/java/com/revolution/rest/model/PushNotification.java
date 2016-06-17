 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Table;
 
 @Entity
 @Table(name="pushnotificationinfo")
 public class PushNotification extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = -8122071265560256963L;
 
   @Column(name="userId")
   private Long userId;
 
   @Column(name="clientId")
   private String clientId;
 
   @Column(name="deviceType")
   private String deviceType;
   
   @Column(name="deviceToken")
   private String deviceToken;
 
   public Long getUserId()
   {
     return this.userId;
   }
 
   public void setUserId(Long userId) {
     this.userId = userId;
   }
 
   public String getClientId() {
     return this.clientId;
   }
 
   public void setClientId(String clientId) {
     this.clientId = clientId;
   }
 
   public String getDeviceType() {
     return this.deviceType;
   }
 
   public void setDeviceType(String deviceType) {
     this.deviceType = deviceType;
   }

public String getDeviceToken() {
	return deviceToken;
}

public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
}
   
   
 }

