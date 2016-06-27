package com.revolution.rest.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.revolution.rest.model.Columns;
@Repository("columnsDao")
@SuppressWarnings("unchecked")
public class ColumnsDaoImpl extends BaseDaoImpl<Columns, Long>implements ColumnsDao {

	public ColumnsDaoImpl() {
		super(Columns.class);
	}

	@Override
	public List<Columns> getAllColumns() {
		String hql = "from Columns order by sequence desc";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		List<Columns> cList = query.list();
		return cList;
	}
}
