package com.revolution.rest.dao;

import java.util.List;

import com.revolution.rest.model.Slide;

public abstract interface SlideDao extends BaseDao<Slide, Long>
{
  public abstract List<Slide> getSlideList();
  
  public abstract List<Slide> getSlideLists();
  
  public List<Slide> getSlideByGroups(String groups);
}

