package com.revolution.rest.service;

import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.revolution.rest.dao.ColumnsDao;
import com.revolution.rest.model.Columns;

public class HtmlServiceImpl implements HtmlService {
	@Autowired
	private ColumnsDao columnsDao;
	@Override
	public Response getIndex() {
		StringBuffer sb = new StringBuffer();
		List<Columns> cList = columnsDao.getAll();
		sb.append("<html><head><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>"
				+ "<meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body>");
		if(cList != null && cList.size() > 0){
			for(Columns c:cList){
				sb.append("<div><h1>"+c.getColumn_name()+"</h1></div>")
					.append("<div><p>√Ë ˆ£∫</p><span>"+c.getDescription()+"</span></div>");
			}
		}
		sb.append("<div style='width:200px;height:100px;border:solid 1px red'></div></body></html>");
		return Response.status(Response.Status.OK).entity(sb.toString()).build();
	}

}
