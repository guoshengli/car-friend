 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement
 public class ImageMedia
   implements Serializable
 {
   private static final long serialVersionUID = -7233506673855725689L;
   private String name;
   private String original_size;
   private String comment;
 
   public String getName()
   {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public String getOriginal_size() {
     return this.original_size;
   }
 
   public void setOriginal_size(String original_size) {
     this.original_size = original_size;
   }
 
 
public String getComment() {
	return comment;
}

public void setComment(String comment) {
	this.comment = comment;
}
   
   
 }

