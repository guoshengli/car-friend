package com.friend.rest.service.model;

import java.io.Serializable;
import java.util.List;


public class ProfileVo implements Serializable {
	private static final long serialVersionUID = -95140912508193593L;
	
	private UserParentModel user;
	
	private List<EventModel> event;
	

	public UserParentModel getUser() {
		return user;
	}

	public void setUser(UserParentModel user) {
		this.user = user;
	}

	public List<EventModel> getEvent() {
		return event;
	}

	public void setEvent(List<EventModel> event) {
		this.event = event;
	}


	
}
