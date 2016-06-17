 package com.revolution.rest.model;
 
 import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;



 
 @Entity
 @Table(name="theme_color")
 public class Theme_color extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 4981322695695672454L;
 
   @Column(name="color")
   private String color;
   
  /* @OneToOne(mappedBy="theme_color",fetch=FetchType.EAGER,cascade=CascadeType.ALL) 
   private Collection collection;*/
   
  /* @OneToOne(mappedBy="theme_color",fetch=FetchType.EAGER,cascade=CascadeType.ALL) 
   private User user;*/
   
   
   public String getColor()
   {
     return this.color;
   }
   public void setColor(String color) {
     this.color = color;
   }
/*public Collection getCollection() {
	return collection;
}
@JsonBackReference
public void setCollection(Collection collection) {
	this.collection = collection;
}*/
   
   
 }

