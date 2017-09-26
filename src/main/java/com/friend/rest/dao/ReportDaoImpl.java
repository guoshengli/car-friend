package com.friend.rest.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.friend.rest.model.Report;

@Repository("reportDao")
@SuppressWarnings("unchecked")
public class ReportDaoImpl extends BaseDaoImpl<Report, Long>implements ReportDao {
	public ReportDaoImpl() {
		super(Report.class);
	}

	public List<Report> getReportsPage(int count, String filter_type) {
		List<Report> list = new ArrayList<Report>();
		if (filter_type.equals("all")) {
			String hql = "from Report group by object_id,object_type order by create_time desc";
			Session session = getSessionFactory().getCurrentSession();
			Query query = session.createQuery(hql);
			query.setMaxResults(count);
			list = query.list();
		} else {
			String hql = "from Report where status=? group by object_id,object_type order by create_time desc";
			Session session = getSessionFactory().getCurrentSession();
			Query query = session.createQuery(hql);
			query.setString(0, filter_type);
			query.setMaxResults(count);
			list = query.list();
		}

		return list;
	}

	public List<Report> getReportsPage(int count, String filter_type, Long reportId, int identifier) {
		List<Report> list = new ArrayList<Report>();
		Report report = (Report) get(reportId);
		if (report != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(report.getCreate_time().longValue() * 1000L);
			String create_time = sdf.format(date);
			Session session = getSessionFactory().getCurrentSession();
			if (filter_type.equals("all")) {
				if (identifier == 1) {
					String hql = "from Report where create_time >= ? and id != ? group by object_id,object_type order by create_time desc";
					Query query = session.createQuery(hql);
					query.setString(0, create_time);
					query.setLong(1, reportId.longValue());
					query.setMaxResults(count);
					list = query.list();
					Collections.reverse(list);
				} else if (identifier == 2) {
					String hql = "from Report where create_time <= ? and id != ? group by object_id,object_type order by create_time";
					Query query = session.createQuery(hql);
					query.setString(0, create_time);
					query.setLong(1, reportId.longValue());
					query.setMaxResults(count);
					list = query.list();
				}
			} else if (identifier == 1) {
				String hql = "from Report where create_time >= ? and id != ? and status=? group by object_id,object_type order by create_time desc";
				Query query = session.createQuery(hql);
				query.setString(0, create_time);
				query.setLong(1, reportId.longValue());
				query.setMaxResults(count);
				list = query.list();
				Collections.reverse(list);
			} else if (identifier == 2) {
				String hql = "from Report where create_time <= ? and id != ? and status=? group by object_id,object_type order by create_time";
				Query query = session.createQuery(hql);
				query.setString(0, create_time);
				query.setLong(1, reportId.longValue());
				query.setString(2, filter_type);
				query.setMaxResults(count);
				list = query.list();
			}

		}

		return list;
	}

	public Report getReportByCommentIdAndUserId(Long commentId, Long loginUserid) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Report where object_id=? and sender_id=?";
		Query query = session.createQuery(hql).setLong(0, commentId.longValue()).setLong(1, loginUserid.longValue());
		List<Report> list = query.list();
		Report report = null;
		if ((list != null) && (list.size() > 0)) {
			report = (Report) list.get(0);
		}
		return report;
	}

	public void updateReport(Long object_id, String string, Long loginUserid) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update Report set status=? where object_id=? and sender_id=?";
		Query query = session.createQuery(hql).setString(0, string).setLong(1, loginUserid.longValue()).setLong(2,
				object_id.longValue());
		query.executeUpdate();
	}

	public Report getReportByStoryId(Long storyId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Report where object_id=?";
		Query query = session.createQuery(hql).setLong(0, storyId.longValue());
		List<Report> list = query.list();
		Report report = null;
		if ((list != null) && (list.size() > 0)) {
			report = (Report) list.get(0);
		}
		return report;
	}

	public Report getReportByCommentId(Long commentId) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Report where object_id=?";
		Query query = session.createQuery(hql).setLong(0, commentId.longValue());
		List<Report> list = query.list();
		Report report = null;
		if ((list != null) && (list.size() > 0)) {
			report = (Report) list.get(0);
		}
		return report;
	}

	public Report getReportByStoryIdAndUserId(Long storyId, Long loginUserid) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "from Report where object_id=? and sender_id=?";
		Query query = session.createQuery(hql).setLong(0, storyId.longValue()).setLong(1, loginUserid.longValue());
		List<Report> list = query.list();
		Report report = null;
		if ((list != null) && (list.size() > 0)) {
			report = (Report) list.get(0);
		}
		return report;
	}

	public void handleReport(Long object_id, String type, Long loginUserid, String status) {
		Session session = getSessionFactory().getCurrentSession();
		String hql = "update Report set status=?,operator_id=? where object_id=? and type=?";
		Query query = session.createQuery(hql).setString(0, status).setLong(1, loginUserid.longValue())
				.setLong(2, object_id.longValue()).setString(3, type);
		query.executeUpdate();
	}
}
