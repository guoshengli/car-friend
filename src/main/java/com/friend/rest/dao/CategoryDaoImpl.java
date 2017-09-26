package com.friend.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Category;
@Repository("categoryDao")
@SuppressWarnings("unchecked")
public class CategoryDaoImpl extends BaseDaoImpl<Category, Long> implements CategoryDao {

	public CategoryDaoImpl() {
		super(Category.class);
	}

	@Override
	public void updateCategorySequence(Long id, int sequence) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update Category set sequence=? where id=?";
		Query query = session.createQuery(hql).setInteger(0, sequence)
				.setLong(1,id);
		query.executeUpdate();
	}

	
	@Override
	public List<Category> getCategoryListBySequence() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Category where 1=1 and status='enable' order by sequence";
		Query query = session.createQuery(hql);
		return query.list();
	}

	

}
