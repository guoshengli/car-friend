package com.friend.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Category;
import com.friend.rest.model.CategoryCollection;
@Repository("categoryCollectionDao")
public class CategoryCollectionDaoImpl extends BaseDaoImpl<CategoryCollection, Long> implements CategoryCollectionDao {

	public CategoryCollectionDaoImpl() {
		super(CategoryCollection.class);
	}

	@Override
	public void updateCategoryCollectionSequence(Long category_id, int sequence,int collection_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update CategoryCollection set sequence=? where category_id = ? and collection_id=?";
		Query query = session.createQuery(hql).setInteger(0, sequence).setLong(1,category_id).setInteger(2, collection_id);
		query.executeUpdate();
	}

	@Override
	public List<CategoryCollection> getCategoryCollectionByCategoryId(Long categoryId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CategoryCollection cc where 1=1 and cc.collection.status='enable' and category_id = ?";
		Query query = session.createQuery(hql).setLong(0,categoryId);
		return query.list();
	}

	@Override
	public CategoryCollection getCategoryCollectionByCategoryIdAndCollectionId(Long categoryId, int collectionId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CategoryCollection where category_id = ? and collection_id = ?";
		Query query = session.createQuery(hql).setLong(0,categoryId).setInteger(1, collectionId);
		List<CategoryCollection> ccList = query.list();
		CategoryCollection cc = null;
		if(ccList != null && ccList.size() > 0){
			cc = ccList.get(0);
		}
		return cc;
	}

	@Override
	public List<CategoryCollection> getCategoryCollectionList() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from CategoryCollection cc"
				+ " where cc.category.status='enable' and cc.collection.status='enable' order by sequence";
		Query query = session.createQuery(hql);
		return query.list();
	}

	@Override
	public List<Category> getCategoryList() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "select cc.category from CategoryCollection cc where 1=1 and cc.category.status='enable' group by cc.category.id";
		Query query = session.createQuery(hql);
		return query.list();
	}

	

}
