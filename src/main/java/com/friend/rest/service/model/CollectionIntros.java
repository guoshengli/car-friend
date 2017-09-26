 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 
 public class CollectionIntros
   implements Serializable
 {
   private static final long serialVersionUID = -8605259752032107633L;
   private Long id;
   private String collection_name;
   private String info;
 
   public Long getId()
   {
     return this.id;
   }
   public void setId(Long id) {
     this.id = id;
   }
   public String getCollection_name() {
     return this.collection_name;
   }
   public void setCollection_name(String collection_name) {
     this.collection_name = collection_name;
   }
   public String getInfo() {
     return this.info;
   }
   public void setInfo(String info) {
     this.info = info;
   }
 }

