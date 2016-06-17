package com.revolution.rest.service;

import com.revolution.rest.dao.FollowDao;
import com.revolution.rest.model.Follow;

import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FollowServiceImpl implements FollowService {

	@Autowired
	private FollowDao followDao;

	public Response createFollow(Follow follow) {
		try {
			this.followDao.saveOrUpdate(follow);
		} catch (Exception e) {
			return null;
		}
		return Response.status(Response.Status.CREATED).entity("Follow created").build();
	}

	public Response deleteFollow(Long id) {
		this.followDao.delete(id);

		return Response.status(Response.Status.OK).entity("Follow deleted").build();
	}
}
