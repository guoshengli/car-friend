 package com.friend.rest.model;
 
 import com.fasterxml.jackson.annotation.JsonBackReference;
import com.friend.rest.service.model.CoverMedia;

import java.io.Serializable;
 import javax.persistence.Basic;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.JoinColumn;
 import javax.persistence.Lob;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import javax.persistence.Transient;
 public class StoryElement extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 2524659265729857655L;
 
   @Column(name="grid_size")
   private String grid_size;
 
   @Lob
   @Basic(fetch=FetchType.LAZY)
   @Column(name="content", columnDefinition="TEXT", nullable=true)
   private String contents;
 
   @Column(name="layout_type")
   private String layout_type;
 
   
   @ManyToOne(fetch=FetchType.LAZY)
   @JoinColumn(name="story_id", updatable=false)
   private Story storyinfo;
 
   @Transient
   private CoverMedia content;
 
   public String getGrid_size()
   {
     return this.grid_size;
   }
 
   public void setGrid_size(String grid_size) {
     this.grid_size = grid_size;
   }
 
   public String getLayout_type() {
     return this.layout_type;
   }
 
   public void setLayout_type(String layout_type) {
     this.layout_type = layout_type;
   }
 
   public String getContents() {
     return this.contents;
   }
 
   public void setContents(String contents) {
     this.contents = contents;
   }
   @JsonBackReference
   
   public Story getStoryinfo() {
     return this.storyinfo;
   }
   @JsonBackReference
   
   public void setStoryinfo(Story storyinfo) {
     this.storyinfo = storyinfo;
   }
 
   @Transient
   public CoverMedia getContent() {
     return this.content;
   }
   @Transient
   public void setContent(CoverMedia content) {
     this.content = content;
   }
 }

