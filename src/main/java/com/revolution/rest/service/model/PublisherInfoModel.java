 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 
 public class PublisherInfoModel
   implements Serializable
 {
   private static final long serialVersionUID = 2516866019755316034L;
   private String type;
   private String content;
 
   public String getType()
   {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 }

