 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 import net.sf.json.JSONObject;
 
 @XmlRootElement
 public class UpdateUserModel
   implements Serializable
 {
   private static final long serialVersionUID = 62459356386850863L;
   private String username;
   private String introduction;
   private String website;
   private JSONObject avatar_image = new JSONObject();
 
   private JSONObject cover_image = new JSONObject();
   private String gender;
 
   public String getUsername()
   {
     return this.username;
   }
 
   public void setUsername(String username) {
     this.username = username;
   }
 
   public String getIntroduction() {
     return this.introduction;
   }
 
   public void setIntroduction(String introduction) {
     this.introduction = introduction;
   }
 
   public String getWebsite() {
     return this.website;
   }
 
   public void setWebsite(String website) {
     this.website = website;
   }
 
   public JSONObject getAvatar_image() {
     return this.avatar_image;
   }
 
   public void setAvatar_image(JSONObject avatar_image) {
     this.avatar_image = avatar_image;
   }
 
   public JSONObject getCover_image() {
     return this.cover_image;
   }
 
   public void setCover_image(JSONObject cover_image) {
     this.cover_image = cover_image;
   }
 

	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
   
   
 }

