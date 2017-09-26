package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.FeatureCollection;

public interface FeatureCollectionDao extends BaseDao<FeatureCollection, Long> {
	
	public FeatureCollection getFeatureCollectionByCollectionId(Long collectionId);
	
	public void delFeatureCollection(Long collectionId);
	
	public List<FeatureCollection> getFeatureCollectionByThree();
	
	public List<FeatureCollection> getFeatureCollection(int count);
	
	public List<FeatureCollection> getFeatureCollectionByPage(Long id,int count,int identify);
	
	public FeatureCollection getFeatureCollectionByOne();

}
