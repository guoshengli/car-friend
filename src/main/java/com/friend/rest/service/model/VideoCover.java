 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement
 public class VideoCover extends CoverMedia
   implements Serializable
 {
   private static final long serialVersionUID = 8322494573855725689L;
   private String type;
   private VideoMedia media;
 
   public String getType()
   {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }

public VideoMedia getMedia() {
	return media;
}

public void setMedia(VideoMedia media) {
	this.media = media;
}
   
   
 
 }

