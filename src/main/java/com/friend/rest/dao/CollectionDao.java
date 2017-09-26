package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Collection;
import com.friend.rest.model.Story;

public interface CollectionDao extends BaseDao<Collection, Integer>
{
  public void disableCollection(Long paramLong);


  public List<Collection> getCollections();

  public int getCollectionCountByName(String paramString);

  public Collection getCollectionById(Long paramLong);
  
  public List<Collection> getCollectionBynormal();
  
  public Collection getCollectionByCollectionName(String collectionname);
  
  public List<Collection> getCollectionByRanTen();
  
  public List<Collection> getCollectionBytype(int type);
  
  public List<Collection> getCollectionByCarType(String type);
  
  public List<Collection> getCollectionBytype(String type,int count);
  
  public List<Collection> getCollectionListByParams(int count,Long districts_id);
  
  public List<Collection> getCollectionListByParams(int count,Long districts_id,String collection_name);
  
  public List<Collection> getCollectionListByParams(int count,Long districts_id,int collection_id);
  
  public List<Collection> getCollectionListByParams(int count,Long districts_id,int collection_id,String collection_name);
  
  public List<Collection> getCollectionListByCollectionName(String collection_name);
  
  public Collection getCollectionById(int id);
}

