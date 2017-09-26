package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Category;

public interface CategoryDao extends BaseDao<Category, Long> {
	public void updateCategorySequence(Long id, int sequence);
	
	public List<Category> getCategoryListBySequence();
}
