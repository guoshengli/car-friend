 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 import net.sf.json.JSONObject;
 
 @XmlRootElement
 public class EventModel
   implements Serializable
 {
   private static final long serialVersionUID = 7348494573835862519L;
   private Long id;
   private Long event_time;
   private String event_type;
   private JSONObject content;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public Long getEvent_time() {
     return this.event_time;
   }
 
   public void setEvent_time(Long event_time) {
     this.event_time = event_time;
   }
 
   public String getEvent_type() {
     return this.event_type;
   }
 
   public void setEvent_type(String event_type) {
     this.event_type = event_type;
   }
 
   public JSONObject getContent() {
     return this.content;
   }
 
   public void setContent(JSONObject content) {
     this.content = content;
   }
 }

