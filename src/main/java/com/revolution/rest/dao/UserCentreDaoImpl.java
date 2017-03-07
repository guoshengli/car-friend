package com.revolution.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.UserCentre;
@Repository("userCentreDao")
public class UserCentreDaoImpl extends BaseDaoImpl<UserCentre, Long> 
	implements UserCentreDao {
	public UserCentreDaoImpl() {
		super(UserCentre.class);
	}

	@Override
	public UserCentre getUserCentreByCentreId(int centre_id) {
		String hql = " from UserCentre where centre_id=?";
	     Session session = getSessionFactory().getCurrentSession();
	     Query query = session.createQuery(hql);
	     query.setLong(0, centre_id);
	     List<UserCentre> list = query.list();
	     UserCentre uc = null;
	     if(list != null && list.size() > 0){
	    	 uc = list.get(0);
	     }
	     return uc;
	}

	@Override
	public UserCentre getUserCentreByUserId(Long user_id) {
		String hql = " from UserCentre where user_id=?";
	     Session session = getSessionFactory().getCurrentSession();
	     Query query = session.createQuery(hql);
	     query.setLong(0, user_id);
	     List<UserCentre> list = query.list();
	     UserCentre uc = null;
	     if(list != null && list.size() > 0){
	    	 uc = list.get(0);
	     }
	     return uc;
	}
}
