 package com.friend.rest.service.model;
 
 import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
 import net.sf.json.JSONObject;
 
 @XmlRootElement
 public class UserCollectionModel extends UserParentModel
   implements Serializable
 {
   private static final long serialVersionUID = 827540663675736790L;
   private Long id;
   private String username;
   private String email;
   private Long created_time;
   private String status;
   private String introduction;
   private JSONObject avatar_image;
   private JSONObject cover_image;
   private int reposts_count;
   private int stories_count;
   private int followers_count;
   private int following_count;
   private boolean followed_by_current_user;
   private boolean is_following_current_user;
   private String website;
   private String profile_url;
   private boolean email_verified;
   private String user_type;
   private String gender;
   List<CollectionIntroLast> followed_collection_info;
 
   public UserCollectionModel()
   {
   }
 
   public UserCollectionModel(Long id, String username, String email, Long created_time, 
		   String status, String introduction, JSONObject avatar_image, 
		   JSONObject cover_image,  int reposts_count, 
		   int stories_count, int followers_count, int following_count, 
		   String website, boolean followed_by_current_user, boolean is_following_current_user,
		   String user_type,String gender,List<CollectionIntroLast> followed_collection_info)
   {
     this.id = id;
     this.username = username;
     this.email = email;
     this.created_time = created_time;
     this.status = status;
     this.introduction = introduction;
     this.avatar_image = avatar_image;
     this.cover_image = cover_image;
     this.reposts_count = reposts_count;
     this.stories_count = stories_count;
     this.followers_count = followers_count;
     this.following_count = following_count;
     this.website = website;
     this.followed_by_current_user = followed_by_current_user;
     this.is_following_current_user = is_following_current_user;
     this.user_type = user_type;
     this.gender = gender;
     this.followed_collection_info = followed_collection_info;
   }
 
   public Long getId() {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getUsername() {
     return this.username;
   }
 
   public void setUsername(String username) {
     this.username = username;
   }
 
   public String getEmail() {
     return this.email;
   }
 
   public void setEmail(String email) {
     this.email = email;
   }
 
   public Long getCreated_time() {
     return this.created_time;
   }
 
   public void setCreated_time(Long created_time) {
     this.created_time = created_time;
   }
 
   public String getStatus() {
     return this.status;
   }
 
   public void setStatus(String status) {
     this.status = status;
   }
 
   public String getIntroduction() {
     return this.introduction;
   }
 
   public void setIntroduction(String introduction) {
     this.introduction = introduction;
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
 
 
   public int getReposts_count() {
     return this.reposts_count;
   }
 
   public void setReposts_count(int reposts_count) {
     this.reposts_count = reposts_count;
   }
 
   public int getStories_count() {
     return this.stories_count;
   }
 
   public void setStories_count(int stories_count) {
     this.stories_count = stories_count;
   }
 
   public int getFollowers_count() {
     return this.followers_count;
   }
 
   public void setFollowers_count(int followers_count) {
     this.followers_count = followers_count;
   }
 
   public int getFollowing_count() {
     return this.following_count;
   }
 
   public void setFollowing_count(int following_count) {
     this.following_count = following_count;
   }
 
   public boolean getFollowed_by_current_user() {
     return this.followed_by_current_user;
   }
 
   public void setFollowed_by_current_user(boolean followed_by_current_user) {
     this.followed_by_current_user = followed_by_current_user;
   }
 
   public boolean getIs_following_current_user() {
     return this.is_following_current_user;
   }
 
   public void setIs_following_current_user(boolean is_following_current_user) {
     this.is_following_current_user = is_following_current_user;
   }
 
   public String getWebsite() {
     return this.website;
   }
 
   public void setWebsite(String website) {
     this.website = website;
   }
 
   public String getProfile_url() {
     return this.profile_url;
   }
 
   public void setProfile_url(String profile_url) {
     this.profile_url = profile_url;
   }
 
   public boolean getEmail_verified() {
     return this.email_verified;
   }
 
   public void setEmail_verified(boolean email_verified) {
     this.email_verified = email_verified;
   }
 
   public String getUser_type() {
     return this.user_type;
   }
 
   public void setUser_type(String user_type) {
     this.user_type = user_type;
   }

	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<CollectionIntroLast> getFollowed_collection_info() {
		return followed_collection_info;
	}

	public void setFollowed_collection_info(List<CollectionIntroLast> followed_collection_info) {
		this.followed_collection_info = followed_collection_info;
	}

	
   
   
 }

