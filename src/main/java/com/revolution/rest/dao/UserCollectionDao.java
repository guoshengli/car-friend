package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Collection;
import com.revolution.rest.model.User;
import com.revolution.rest.model.UserCollection;

public interface UserCollectionDao extends BaseDao<UserCollection, Long> {
	public int getCollectionCountByUserid(Long userid);
	
	public int getUserCountByCollectionId(Long collectionId);
	
	public List<Collection> getCollectionByUserid(Long userid);
	
	public List<Collection> getCollectionByUserid(Long userid,String type);
	
	public void deleteUserCollection(Long loginUserid,Long collection_id);
	
	public UserCollection getUserCollectionByCollectionId(Long collectionId,Long userid);
	
	public void updateUserCollectionSequenceByUserIdAndCollectionId(Long userId,Long collectionId,int sequence);
	
	public void delUserCollectionByCollectionId(Long collectionId);
	
	public List<Object[]> getCollectionByHot();
	
	public List<Object[]> getCollectionByHot(int count);
	
	public List<Object[]> getCollectionByHot(int num,Long collection_id,int count,int identify);
	
	public int getCollectionByCount(Long collection_id);
	
	public List<Collection> getCollectionByUserid(Long userid,int count);
	  
	  public List<Collection> getCollectionByUserid(Long userid,int count,Long collection_id,int identify);
	  
	  public List<User> getUserByCollectionId(Long collectionId,int count);
	  
	  public List<User> getUserByCollectionIdPage(Long collectionId,int count,Long userId,int identify);
	  
}
