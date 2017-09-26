package com.friend.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.CarClub;
@Repository("carClubDao")
@SuppressWarnings("unchecked")
public class CarClubDaoImpl extends BaseDaoImpl<CarClub, Long> implements CarClubDao {

	public CarClubDaoImpl() {
		super(CarClub.class);
	}

	@Override
	public void deleteCarClubByCar_id(int car_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from CarClub where car_id=?";
		Query query = session.createQuery(hql).setInteger(0, car_id);
		query.executeUpdate();
	}
	
	public List<CarClub> getCarClubListByCar_id(int car_id){
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CarClub where 1=1 and car_id=?";
		Query query = session.createQuery(hql).setInteger(0, car_id);
		List<CarClub> list = query.list();
		return list;
	}

	@Override
	public List<CarClub> getCarClubListByClub_id(int club_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CarClub where 1=1 and club_id=?";
		Query query = session.createQuery(hql).setInteger(0, club_id);
		List<CarClub> list = query.list();
		return list;
	}

	@Override
	public CarClub getCarClubByClub_idAndCar_id(int club_id, int car_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CarClub where 1=1 and club_id=? and car_id=?";
		Query query = session.createQuery(hql).setInteger(0, club_id).setInteger(1, car_id);
		List<CarClub> list = query.list();
		CarClub cc = null;
		if(list != null && list.size() > 0){
			cc = list.get(0);
		}
		return cc;
	}

}
