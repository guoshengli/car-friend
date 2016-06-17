package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.LinkAccounts;

public interface LinkAccountsDao extends BaseDao<LinkAccounts, Long> {
	public Object[] getLinkAccountsByUUID(String paramString);

	public List<LinkAccounts> getLinkAccountsByUserid(Long paramLong);

	public void deleteLinkAccountsByServiceAndUserid(String paramString, Long paramLong);
}
