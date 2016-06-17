package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Collection;
import com.revolution.rest.model.Story;

public interface CollectionDao extends BaseDao<Collection, Long>
{
  public void disableCollection(Long paramLong);

  public List<Story> getStoriesByCollection(Long paramLong);

  public List<Collection> getCollections();

  public int getCollectionCountByName(String paramString);

  public Collection getCollectionById(Long paramLong);
  
  public List<Collection> getCollectionBynormal();
  
  public Collection getCollectionByCollectionName(String collectionname);
  
  public List<Collection> getCollectionByRanTen();
  
  public List<Collection> getCollectionBytype(String type);
  
  public List<Collection> getCollectionBytype(String type,int count);
  
  
  public List<Collection> getCollectionBytype(String type,int count,Long collection_id,int identify);
  
  
}

