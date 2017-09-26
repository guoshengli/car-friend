 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement
 public class TextMedia
   implements Serializable
 {
   private static final long serialVersionUID = -343569135638555363L;
   private String alignment;
   private String style;
   private String plain_text;
 
   public String getAlignment()
   {
     return this.alignment;
   }
 
   public void setAlignment(String alignment) {
     this.alignment = alignment;
   }
 
   public String getStyle() {
     return this.style;
   }
 
   public void setStyle(String style) {
     this.style = style;
   }
 
   public String getPlain_text() {
     return this.plain_text;
   }
 
   public void setPlain_text(String plain_text) {
     this.plain_text = plain_text;
   }
 }

