package com.revolution.rest.dao;

import com.revolution.rest.model.PublisherInfo;

public interface PublisherInfoDao extends BaseDao<PublisherInfo, Long>
{
  public void deletePublisherInfo(Long paramLong, String paramString);
}

