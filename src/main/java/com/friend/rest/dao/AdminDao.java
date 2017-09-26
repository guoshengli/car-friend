package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Admin;
public interface AdminDao extends BaseDao<Admin, Long> {
	public Admin getAdminByFbid(int fb_id);
	
	public List<Admin> getAllAdmin();
}
