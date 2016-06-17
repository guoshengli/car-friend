package com.revolution.rest.dao;

import java.util.List;
import java.util.Random;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Theme_color;

@Repository("themeColorDao")
@SuppressWarnings("unchecked")
public class ThemeColorDaoImpl extends BaseDaoImpl<Theme_color, Long>implements ThemeColorDao {
	public ThemeColorDaoImpl() {
		super(Theme_color.class);
	}

	public Theme_color getThemeColorByRand() {
		Session session = getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from Theme_color");
		int size = q.list().size();
		Random r = new Random();
		q.setMaxResults(10);
		q.setFirstResult(r.nextInt(size - 1) + 1);
		List<Theme_color> list = q.list();
		Theme_color tc = null;
		if ((list != null) && (list.size() > 0)) {
			tc = (Theme_color) list.get(0);
		}
		return tc;
	}
}
