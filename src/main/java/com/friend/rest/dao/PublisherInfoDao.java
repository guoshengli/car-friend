package com.friend.rest.dao;

import com.friend.rest.model.PublisherInfo;

public interface PublisherInfoDao extends BaseDao<PublisherInfo, Long>
{
  public void deletePublisherInfo(Long paramLong, String paramString);
}

