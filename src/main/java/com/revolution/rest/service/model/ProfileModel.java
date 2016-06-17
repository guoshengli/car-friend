package com.revolution.rest.service.model;

import java.io.Serializable;
import java.util.List;


import net.sf.json.JSONObject;

public class ProfileModel implements Serializable {
	private static final long serialVersionUID = -95140912508193593L;
	
	private UserParentModel user;
	
	private List<JSONObject> story;
	
	private List<EventModel> event;

	public UserParentModel getUser() {
		return user;
	}

	public void setUser(UserParentModel user) {
		this.user = user;
	}

	public List<JSONObject> getStory() {
		return story;
	}

	public void setStory(List<JSONObject> story) {
		this.story = story;
	}

	public List<EventModel> getEvent() {
		return event;
	}

	public void setEvent(List<EventModel> event) {
		this.event = event;
	}


	
}
