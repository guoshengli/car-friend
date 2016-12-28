 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement
 public class VideoMedia
   implements Serializable
 {
   private static final long serialVersionUID = -7233506673855725689L;
   private String name;
 
   public String getName()
   {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   
   
 }

