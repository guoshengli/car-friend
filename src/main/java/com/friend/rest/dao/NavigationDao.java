package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Navigation;

public interface NavigationDao extends BaseDao<Navigation, Long> {
	public Navigation getNavigationByCollectionId(int collection_id);
	
	public List<Navigation> getNavigationBySequence();
	
	public void deleteNavigationByCollectionIdAndType(Long collection_id,String type);
	
	public void updateNavigationSequence(Long nav_id,int sequence);
}
