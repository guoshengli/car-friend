package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.Category;
import com.friend.rest.model.CategoryCollection;

public interface CategoryCollectionDao extends BaseDao<CategoryCollection, Long> {
	public void updateCategoryCollectionSequence(Long category_id,int sequence,int collection_id);
	
	public List<CategoryCollection> getCategoryCollectionByCategoryId(Long categoryId);
	
	public CategoryCollection getCategoryCollectionByCategoryIdAndCollectionId(Long categoryId,int collectionId);
	
	public List<CategoryCollection> getCategoryCollectionList();
	
	public List<Category> getCategoryList();
}
