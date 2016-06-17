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
 
 @Table(name="slide")
 @Entity
 public class Slide extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 4812181417354871477L;
 
   @Column(name="type")
   private String type;
 
   @Column(name="slide_image")
   private String slide_image;
 
   @Column(name="reference_id")
   private Long reference_id;
 
   @Column(name="url")
   private String url;
 
   @Column(name="authorId")
   private Long authorId;
 
   @Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.ALWAYS)
   @Column(name="create_time", insertable=false)
   private Date create_time;
 
   @Column(name="groups")
   private String group;
   
   @Column(name="sequence")
   private int sequence;
   
   @Column(name="status")
   private String status;
 
   public String getType()
   {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public String getSlide_image() {
     return this.slide_image;
   }
 
   public void setSlide_image(String slide_image) {
     this.slide_image = slide_image;
   }
 
   public Long getReference_id() {
     return this.reference_id;
   }
 
   public void setReference_id(Long reference_id) {
     this.reference_id = reference_id;
   }
 
   public String getUrl() {
     return this.url;
   }
 
   public void setUrl(String url) {
     this.url = url;
   }
 
   public Long getAuthorId() {
     return this.authorId;
   }
 
   public void setAuthorId(Long authorId) {
     this.authorId = authorId;
   }
 
   public Date getCreate_time() {
     return this.create_time;
   }
 
   public void setCreate_time(Date create_time) {
     this.create_time = create_time;
   }

public String getGroup() {
	return group;
}

public void setGroup(String group) {
	this.group = group;
}

public int getSequence() {
	return sequence;
}

public void setSequence(int sequence) {
	this.sequence = sequence;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}
 

  
 }

