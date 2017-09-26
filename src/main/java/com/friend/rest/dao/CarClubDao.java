package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.CarClub;

public interface CarClubDao extends BaseDao<CarClub, Long> {
	public void deleteCarClubByCar_id(int car_id);
	
	public List<CarClub> getCarClubListByCar_id(int car_id);
	
	
	public List<CarClub> getCarClubListByClub_id(int club_id);
	
	public CarClub getCarClubByClub_idAndCar_id(int club_id,int car_id);
}
