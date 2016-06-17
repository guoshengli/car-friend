 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class SlideModel
   implements Serializable
 {
   private static final long serialVersionUID = 7055835805321015136L;
   private Long id;
   private String type;
   private JSONObject slide_image;
   private JSONObject slide;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public JSONObject getSlide_image() {
     return this.slide_image;
   }
 
   public void setSlide_image(JSONObject slide_image) {
     this.slide_image = slide_image;
   }
 
   public JSONObject getSlide() {
     return this.slide;
   }
 
   public void setSlide(JSONObject slide) {
     this.slide = slide;
   }
 }

