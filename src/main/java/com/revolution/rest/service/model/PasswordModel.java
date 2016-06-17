 package com.revolution.rest.service.model;
 
 import java.io.Serializable;
 
 public class PasswordModel
   implements Serializable
 {
   private static final long serialVersionUID = 4589506673855732679L;
   private String current_password;
   private String new_password;
 
   public String getCurrent_password()
   {
     return this.current_password;
   }
 
   public void setCurrent_password(String current_password) {
     this.current_password = current_password;
   }
 
   public String getNew_password() {
     return this.new_password;
   }
 
   public void setNew_password(String new_password) {
     this.new_password = new_password;
   }
 }

