package com.friend.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Navigation;
@Repository("navigationDao")
@SuppressWarnings("unchecked")
public class NavigationDaoImpl extends BaseDaoImpl<Navigation, Long> implements NavigationDao {

	public NavigationDaoImpl() {
		super(Navigation.class);
	}

	@Override
	public Navigation getNavigationByCollectionId(int collection_id) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Navigation where 1=1 and collection_id = ?";
		Query query = session.createQuery(hql)
				.setLong(0, collection_id);
		
		List<Navigation> list = query.list();
		Navigation nav = null;
		if(list != null && list.size() > 0){
			nav = list.get(0);
		}
		
		return nav;
	}

	@Override
	public void deleteNavigationByCollectionIdAndType(Long collection_id, String type) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "delete from Navigation where 1=1 and collection_id = ? and type=?";
		Query query = session.createQuery(hql).setLong(0, collection_id).setString(1, type);
		query.executeUpdate();
	}

	@Override
	public List<Navigation> getNavigationBySequence() {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Navigation order by sequence";
		Query query = session.createQuery(hql);
		
		List<Navigation> list = query.list();
		return list;
	}

	@Override
	public void updateNavigationSequence(Long nav_id,int sequence) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update Navigation set sequence=? where id=?";
		Query query = session.createQuery(hql).setInteger(0, sequence).setLong(1,nav_id);
		query.executeUpdate();
	}
	
}
