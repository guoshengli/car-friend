package com.revolution.rest.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class FollowId implements Serializable {
	private static final long serialVersionUID = -7020717905607738823L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "follower_id")
	private User follower;

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getFollower() {
		return this.follower;
	}

	public void setFollower(User follower) {
		this.follower = follower;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof FollowId)) {
			return false;
		}
		FollowId otherFollowId = (FollowId) other;

		if ((otherFollowId.getUser().getId() == getUser().getId())
				&& (otherFollowId.getFollower().getId() == getFollower().getId())) {
			return true;
		}
		return false;
	}

	public int hashCode() {
		int result = getUser().hashCode();
		result = 31 * result + getFollower().hashCode();
		return result;
	}
}
