 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement
 public class ImageCover extends CoverMedia
   implements Serializable
 {
   private static final long serialVersionUID = 8322494573855725689L;
   private String type;
   private ImageMedia media;
 
   public String getType()
   {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public ImageMedia getMedia() {
     return this.media;
   }
 
   public void setMedia(ImageMedia media) {
     this.media = media;
   }
 }

