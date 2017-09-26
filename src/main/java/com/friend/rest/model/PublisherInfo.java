 package com.friend.rest.model;
 
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import javax.xml.bind.annotation.XmlTransient;
 
 public class PublisherInfo extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = -8711575878998779136L;
 
   @Column(name="content")
   private String content;
 
   @Column(name="type")
   private String type;
 
   @XmlTransient
   @ManyToOne
   @JoinColumn(name="user_id")
   private User user;
 
   public String getContent()
   {
     return this.content;
   }
   public void setContent(String content) {
     this.content = content;
   }
   public String getType() {
     return this.type;
   }
   public void setType(String type) {
     this.type = type;
   }
   public User getUser() {
     return this.user;
   }
   public void setUser(User user) {
     this.user = user;
   }
 }

