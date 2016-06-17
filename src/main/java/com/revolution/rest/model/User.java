 package com.revolution.rest.model;
 
 import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
 
 @Entity
 @Table(name="user")
 public class User extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 7042495344488712849L;
 
   @Column(name="username")
   private String username;
 
   @Column(name="password")
   private String password;
 
   @Column(name="email")
   private String email;
 
   @Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.ALWAYS)
   @Column(name="create_time", insertable=false, updatable=false)
   private Date created_time;
 
   @Column(name="salt")
   private String salt;
 
   @Column(name="status")
   private String status;
 
   @Column(name="introduction")
   private String introduction;
 
   @Column(name="web_url")
   private String website;
 
   @Column(name="profile_url")
   private String profile_url;
 
   @Column(name="avatar_image")
   private String avatarImage;
 
   @Column(name="cover_image")
   private String coverImage;
 
   @Column(name="zone")
   private String zone;
 
   @Column(name="phone")
   private String phone;
 
   @Column(name="email_verify", columnDefinition="BIT")
   private boolean email_verified;
 
   @Column(name="user_type")
   private String user_type;
   
   @Column(name="gender")
   private String gender;
 
   @XmlTransient
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Set<Story> stories;
   
   @XmlTransient
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Set<Collection> collection;
 
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Set<Comment> comments;
 
   @XmlTransient
   @OneToMany(mappedBy="pk.follower", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Set<Follow> followings;
 
   @XmlTransient
   @OneToMany(mappedBy="pk.user", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Set<Follow> followers;
 
 
   @XmlTransient
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Set<PublisherInfo> publisherInfos;
   
   @XmlTransient
   @ManyToMany(fetch=FetchType.LAZY)
   @JoinTable(name="user_collection", joinColumns={@JoinColumn(name="user_id")}, inverseJoinColumns={@JoinColumn(name="collection_id")})
   private Set<Collection> collections;
   
   @XmlTransient
   @ManyToMany(fetch=FetchType.LAZY)
   @JoinTable(name="user_story", joinColumns={@JoinColumn(name="user_id")}, inverseJoinColumns={@JoinColumn(name="story_id")})
   private Set<Story> repost_story;
   
   @XmlTransient
   @ManyToMany(fetch=FetchType.LAZY)
   @JoinTable(name="likes", joinColumns={@JoinColumn(name="user_id")}, inverseJoinColumns={@JoinColumn(name="story_id")})
   private Set<Story> like_story;
 
   public String getUsername()
   {
     return this.username;
   }
 
   public void setUsername(String username) {
     this.username = username;
   }
 
   public String getPassword() {
     return this.password;
   }
 
   public void setPassword(String password) {
     this.password = password;
   }
 
   public String getEmail() {
     return this.email;
   }
 
   public void setEmail(String email) {
     this.email = email;
   }
 
   public Long getCreated_time() {
     return Long.valueOf(this.created_time.getTime() / 1000L);
   }
 
   public void setCreated_time(Date created_time) {
     this.created_time = created_time;
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
 
   public String getSalt() {
     return this.salt;
   }
 
   public void setSalt(String salt) {
     this.salt = salt;
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
 
   public String getAvatarImage() {
     return this.avatarImage;
   }
 
   public void setAvatarImage(String avatarImage) {
     this.avatarImage = avatarImage;
   }
 
   public String getCoverImage() {
     return this.coverImage;
   }
 
   public void setCoverImage(String coverImage) {
     this.coverImage = coverImage;
   }
 
   public boolean getEmail_verified() {
     return this.email_verified;
   }
 
   public void setEmail_verified(boolean email_verified) {
     this.email_verified = email_verified;
   }
 
   @XmlTransient
   public Set<Story> getStories() {
     return this.stories;
   }
 
   @XmlTransient
   public void setStories(Set<Story> stories) {
     this.stories = stories;
   }
 
   @XmlTransient
   public Set<Comment> getComments() {
     return this.comments;
   }
 
   @XmlTransient
   public void setComments(Set<Comment> comments) {
     this.comments = comments;
   }
 
   @XmlTransient
   public Set<Follow> getFollowings()
   {
     return this.followings;
   }
 
   public void setFollowings(Set<Follow> followings) {
     this.followings = followings;
   }
 
   @XmlTransient
   public Set<Follow> getFollowers() {
     return this.followers;
   }
 
   public void setFollowers(Set<Follow> followers) {
     this.followers = followers;
   }
 
 

   public Set<PublisherInfo> getPublisherInfos()
   {
     return this.publisherInfos;
   }
 
   public void setPublisherInfos(Set<PublisherInfo> publisherInfos) {
     this.publisherInfos = publisherInfos;
   }
 
   public String getUser_type() {
     return this.user_type;
   }
 
   public void setUser_type(String user_type) {
     this.user_type = user_type;
   }
 
   public String getZone() {
     return this.zone;
   }
 
   public void setZone(String zone) {
     this.zone = zone;
   }
 
   public String getPhone() {
     return this.phone;
   }
 
   public void setPhone(String phone) {
     this.phone = phone;
   }

	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}

	public Set<Collection> getCollections() {
		return collections;
	}

	public void setCollections(Set<Collection> collections) {
		this.collections = collections;
	}

	public Set<Story> getRepost_story() {
		return repost_story;
	}

	public void setRepost_story(Set<Story> repost_story) {
		this.repost_story = repost_story;
	}

	public Set<Story> getLike_story() {
		return like_story;
	}

	public void setLike_story(Set<Story> like_story) {
		this.like_story = like_story;
	}

	public Set<Collection> getCollection() {
		return collection;
	}

	public void setCollection(Set<Collection> collection) {
		this.collection = collection;
	}
	 
	
	   
 }
