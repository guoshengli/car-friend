 package com.revolution.rest.model;
 
 import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.revolution.rest.service.model.CoverMedia;
 
 @Entity
 @Table(name="story")
 public class Story extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 8042495353855714237L;
 
   @Column(name="title")
   private String title;
 
 
   @Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.ALWAYS)
   @Column(name="create_time", insertable=false, updatable=false)
   private Date created_time;
 
   @Column(name="update_time")
   private Date update_time;
 
   @Column(name="recommend_date")
   private String recommend_date;
 
   @Column(name="tiny_url")
   private String tinyURL;
 
   @Column(name="status")
   private String status;
 
   @Column(name="location")
   private String location;
 
   @Column(name="view_count")
   private int viewTimes;
 
   @Column(name="image_count")
   private int image_count;
 
   @Column(name="cover_page", columnDefinition="TEXT")
   private String cover_page;
 
   @Column(name="comments_enabled", columnDefinition="BIT")
   private boolean comments_enabled;
 
   @Column(name="summary", columnDefinition="TEXT")
   private String summary;
 
   @Column(name="recommendation", columnDefinition="BIT")
   private boolean recommendation;
   
   @Column(name="resource")
   private String resource;
   
   @Column(name="last_comment_date")
   private Date last_comment_date;
   
   @Column(name="tid")
   private Long tid;
   
   @Column(name="pid")
   private Long pid;
   
   @Column(name="fid")
   private Long fid;
   
   @OneToMany(mappedBy="storyinfo", cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private List<StoryElement> elements = new ArrayList<StoryElement>();
 
   @OneToMany(mappedBy="story", cascade=CascadeType.ALL)
   private Set<Comment> comments;
 
   
   @OneToMany(mappedBy="story", cascade={javax.persistence.CascadeType.ALL},fetch=FetchType.LAZY)
   private Set<Timeline> timelines;
 
   
   @ManyToOne(fetch=FetchType.LAZY)
   @JoinColumn(name="author_id")
   private User user;
 
   
   @ManyToMany(fetch=FetchType.LAZY)
   @JoinTable(name="collection_story", joinColumns={@JoinColumn(name="story_id")}, inverseJoinColumns={@JoinColumn(name="collection_id")})
   private Set<Collection> collections;
   
   
   @ManyToMany(fetch=FetchType.LAZY)
   @JoinTable(name="columns_story", joinColumns={@JoinColumn(name="story_id")}, inverseJoinColumns={@JoinColumn(name="columns_id")})
   private Set<Columns> columns;
   
   
   @ManyToMany(mappedBy="repost_story", fetch=FetchType.LAZY)
   private Set<User> repost_users;
   
   
   @ManyToMany(mappedBy="like_story", fetch=FetchType.LAZY)
   private Set<User> like_users;
 
   @Transient
   private CoverMedia cover_media;
 
   @Transient
   public CoverMedia getCover_media() { return this.cover_media; }
 
   @Transient
   public void setCover_media(CoverMedia cover_media)
   {
     this.cover_media = cover_media;
   }
 
   public String getTitle() {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public Long getCreated_time() {
     return Long.valueOf(this.created_time.getTime() / 1000L);
   }
 
   public void setCreated_time(Date created_time) {
     this.created_time = created_time;
   }
 
   public Long getUpdate_time() {
     return Long.valueOf(this.update_time.getTime() / 1000L);
   }
 
   public void setUpdate_time(Date update_time) {
     this.update_time = update_time;
   }
 
   public String getTinyURL() {
     return this.tinyURL;
   }
 
   public void setTinyURL(String tinyURL) {
     this.tinyURL = tinyURL;
   }
 
   public String getStatus() {
     return this.status;
   }
 
   public void setStatus(String status) {
     this.status = status;
   }
 
   public String getLocation() {
     return this.location;
   }
 
   public void setLocation(String location) {
     this.location = location;
   }
 
   public int getViewTimes() {
     return this.viewTimes;
   }
 
   public void setViewTimes(int viewTimes) {
     this.viewTimes = viewTimes;
   }
 
   
   public List<StoryElement> getElements() {
     return this.elements;
   }
 
   
   public void setElements(List<StoryElement> elements) {
     this.elements = elements;
   }
 
   public Set<Comment> getComments() {
     return this.comments;
   }
 
   
   public void setComments(Set<Comment> comments) {
     this.comments = comments;
   }
 
   public String getCover_page()
   {
     return this.cover_page;
   }
 
   public void setCover_page(String cover_page) {
     this.cover_page = cover_page;
   }
 
   public boolean getComments_enabled() {
     return this.comments_enabled;
   }
 
   public void setComments_enabled(boolean comments_enabled) {
     this.comments_enabled = comments_enabled;
   }
 
   
   public User getUser() {
     return this.user;
   }
 
   public void setUser(User user) {
     this.user = user;
   }
 
   
   public Set<Collection> getCollections() {
     return this.collections;
   }
 
   
   public void setCollections(Set<Collection> collections) {
     this.collections = collections;
   }
 
   public Set<Timeline> getTimelines() {
     return this.timelines;
   }
 
   public void setTimelines(Set<Timeline> timelines) {
     this.timelines = timelines;
   }
 
   public String getSummary() {
     return this.summary;
   }
 
   public void setSummary(String summary) {
     this.summary = summary;
   }
 
   public String getRecommend_date()
   {
     return this.recommend_date;
   }
 
   public void setRecommend_date(String recommend_date) {
     this.recommend_date = recommend_date;
   }
 
   public boolean isRecommendation() {
     return this.recommendation;
   }
 
   public void setRecommendation(boolean recommendation) {
     this.recommendation = recommendation;
   }
 
   public int getImage_count() {
     return this.image_count;
   }
 
   public void setImage_count(int image_count) {
     this.image_count = image_count;
   }

	public String getResource() {
		return resource;
	}
	
	public void setResource(String resource) {
		this.resource = resource;
	}

	public Set<User> getRepost_users() {
		return repost_users;
	}

	public void setRepost_users(Set<User> repost_users) {
		this.repost_users = repost_users;
	}

	public Set<User> getLike_users() {
		return like_users;
	}

	public void setLike_users(Set<User> like_users) {
		this.like_users = like_users;
	}

	public Date getLast_comment_date() {
		return last_comment_date;
	}

	public void setLast_comment_date(Date last_comment_date) {
		this.last_comment_date = last_comment_date;
	}

	public Set<Columns> getColumns() {
		return columns;
	}

	public void setColumns(Set<Columns> columns) {
		this.columns = columns;
	}

	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Long getFid() {
		return fid;
	}

	public void setFid(Long fid) {
		this.fid = fid;
	}

	
   
 }

