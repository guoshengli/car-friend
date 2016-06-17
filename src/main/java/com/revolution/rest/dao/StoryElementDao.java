package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.StoryElement;

public interface StoryElementDao extends BaseDao<StoryElement, Long>
{
  public void delete(List<StoryElement> paramList);
}

