 package com.friend.rest.service.model;
 
 import java.io.Serializable;
 import net.sf.json.JSONObject;
 
 public class ReportModel
   implements Serializable
 {
   private static final long serialVersionUID = 1236395776323896970L;
   private Long id;
   private String status;
   private Long created_at;
   private String type;
   private Long operator_id;
   private JSONObject content;
 
   public Long getId()
   {
     return this.id;
   }
 
   public void setId(Long id) {
     this.id = id;
   }
 
   public String getStatus() {
     return this.status;
   }
 
   public void setStatus(String status) {
     this.status = status;
   }
 
   public Long getCreated_at() {
     return this.created_at;
   }
 
   public void setCreated_at(Long created_at) {
     this.created_at = created_at;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public Long getOperator_id() {
     return this.operator_id;
   }
 
   public void setOperator_id(Long operator_id) {
     this.operator_id = operator_id;
   }
 
   public JSONObject getContent() {
     return this.content;
   }
 
   public void setContent(JSONObject content) {
     this.content = content;
   }
 }

