package com.friend.rest.dao;

import java.util.List;

import com.friend.rest.model.StoryElement;

public interface StoryElementDao extends BaseDao<StoryElement, Long>
{
  public void delete(List<StoryElement> paramList);
}

