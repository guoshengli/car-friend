package com.friend.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Admin;
@Repository("adminDao")
@SuppressWarnings("unchecked")
public class AdminDaoImpl extends BaseDaoImpl<Admin, Long> implements AdminDao {

	public AdminDaoImpl() {
		super(Admin.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Admin getAdminByFbid(int fb_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Admin where 1=1 and fb_id=?";
		Query query = session.createQuery(hql).setInteger(0, fb_id);
		List<Admin> list = query.list();
		Admin admin = null;
		if(list != null && list.size() > 0){
			admin = list.get(0);
		}
		return admin;
	}

	@Override
	public List<Admin> getAllAdmin() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Admin";
		Query query = session.createQuery(hql);
		List<Admin> list = query.list();
		return list;
	}

	
	
	

}
