package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Report;

public interface ReportDao extends BaseDao<Report, Long> {
	public List<Report> getReportsPage(int paramInt, String paramString);

	public List<Report> getReportsPage(int paramInt1, String paramString, Long paramLong, int paramInt2);

	public Report getReportByCommentIdAndUserId(Long paramLong1, Long paramLong2);

	public void updateReport(Long paramLong1, String paramString, Long paramLong2);

	public Report getReportByStoryId(Long paramLong);

	public Report getReportByCommentId(Long paramLong);

	public Report getReportByStoryIdAndUserId(Long paramLong1, Long paramLong2);

	public void handleReport(Long paramLong1, String paramString1, Long paramLong2, String paramString2);
}
