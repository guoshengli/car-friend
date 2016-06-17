package com.revolution.rest.service;

import com.revolution.rest.dao.LikesDao;
import com.revolution.rest.model.Likes;

import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LikesServiceImpl implements LikesService {

	@Autowired
	private LikesDao likesDao;

	public Response createLike(Likes likes) {
		try {
			this.likesDao.saveOrUpdate(likes);
		} catch (Exception e) {
			return null;
		}
		return Response.status(Response.Status.CREATED).entity("Likes created").build();
	}

	public Response deleteLike(Long id) {
		this.likesDao.delete(id);
		return Response.status(Response.Status.OK).entity("Likes deleted").build();
	}
}
