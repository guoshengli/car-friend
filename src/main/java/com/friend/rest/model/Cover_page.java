 package com.friend.rest.model;
 
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Table;
 
 public class Cover_page extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 3675830747178172538L;
 
   @Column(name="cover_image")
   private String image;
 
   public String getImage()
   {
     return this.image;
   }
   public void setImage(String image) {
     this.image = image;
   }
 }

