 package com.friend.rest.service.model;
 

import net.sf.json.JSONObject;

import java.io.Serializable;

 public class StoryElementModel 
   implements Serializable
 {
 
	private static final long serialVersionUID = 2799369629229829872L;

	private String grid_size;
 
 
   private String layout_type;
 
 
   private JSONObject content;


public String getGrid_size() {
	return grid_size;
}


public void setGrid_size(String grid_size) {
	this.grid_size = grid_size;
}


public String getLayout_type() {
	return layout_type;
}


public void setLayout_type(String layout_type) {
	this.layout_type = layout_type;
}


public JSONObject getContent() {
	return content;
}


public void setContent(JSONObject content) {
	this.content = content;
}
   
   
 
  
 }

