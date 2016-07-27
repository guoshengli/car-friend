 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
 import org.hibernate.annotations.GenerationTime;
 
 @Entity
 @Table(name="collection_story")
 public class CollectionStory extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 8468321267967923649L;
 
   @ManyToOne(fetch=FetchType.LAZY)
   @JoinColumn(name="collection_id")
   private Collection collection;
 
   @ManyToOne(fetch=FetchType.LAZY)
   @JoinColumn(name="story_id")
   private Story story;
 
   @Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.ALWAYS)
   @Column(name="create_time", insertable=false)
   private Date create_time;
   
   public Collection getCollection()
   {
     return this.collection;
   }
 
   public void setCollection(Collection collection) {
     this.collection = collection;
   }
 
   public Story getStory() {
     return this.story;
   }
 
   public void setStory(Story story) {
     this.story = story;
   }
 
   public Date getCreate_time() {
     return this.create_time;
   }
 
   public void setCreate_time(Date create_time) {
     this.create_time = create_time;
   }

   
   
   
 }

