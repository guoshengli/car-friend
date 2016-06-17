package com.revolution.rest.service;

import com.revolution.rest.dao.TimelineDao;
import com.revolution.rest.model.Timeline;

import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TimelineServiceImpl implements TimelineService {

	@Autowired
	private TimelineDao timelineDao;

	public Response create(Timeline timeline) {
		try {
			this.timelineDao.saveOrUpdate(timeline);
		} catch (Exception e) {
			return null;
		}
		return Response.status(Response.Status.CREATED).entity("Timeline created").build();
	}

	public Timeline get(Long timelineId) {
		Timeline timeline = (Timeline) this.timelineDao.get(timelineId);
		return timeline;
	}
}
