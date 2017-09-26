package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.User;

public  interface UserDao extends BaseDao<User, Long>
{
  public  User loginUser(String paramString1, String paramString2);

  public  User loginByPhone(String paramString1, String paramString2, String paramString3);

  public  void disableUser(Long paramLong);

  public  User getUserByUsernameAndEmail(String paramString1, String paramString2);

  public  User getUserByEamil(String paramString);

  public  User getUserByZoneAndPhone(String paramString1, String paramString2);

  public  User getUserByUserName(String paramString);
  
  public  User getUserByFbname(String fbname);

  public  List<User> getRandomUser();

  public  List<User> getUsersByStoryIdAndNull(Long paramLong, int paramInt);

  public  List<User> getUsersByStoryIdAndUserId(Long paramLong1, Long paramLong2, String paramString, int paramInt1, int paramInt2);

  public  List<User> getRepostUsersByStoryId(Long paramLong, int paramInt);

  public  List<User> getRepostUsersByStoryIdAndRepost(Long paramLong1, Long paramLong2, String paramString, int paramInt1, int paramInt2);

  public  void updateUserByUserType(Long paramLong, String paramString);

  public  List<User> getUserByName(String paramString);

  public  User getUserByPhoneAndZone(String paramString1, String paramString2);

  public  List<User> getUserByUserType();
  
  public List<User> getUserByUserType(String user_type);
  
  public List<User> getUserByPhone(String phone);
  
  public List<User> getUserRandom();
  
  public List<User> getUserByFbnameAndPhone(String fbname,String phone);
}

