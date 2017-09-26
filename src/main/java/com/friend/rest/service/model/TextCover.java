 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement
 public class TextCover extends CoverMedia
   implements Serializable
 {
   private static final long serialVersionUID = 567859135638725363L;
   private String type;
   private TextMedia media;
 
   public String getType()
   {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public TextMedia getMedia() {
     return this.media;
   }
 
   public void setMedia(TextMedia media) {
     this.media = media;
   }
 }

