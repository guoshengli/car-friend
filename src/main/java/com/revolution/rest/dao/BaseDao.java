package com.revolution.rest.dao;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<Entity, PrimaryKey extends Serializable>
{
  public List<Entity> getAll();

  public Entity get(PrimaryKey paramPrimaryKey);

  public Entity merge(Entity paramEntity);

  public void save(Entity paramEntity);

  public void saveOrUpdate(Entity paramEntity);

  public void update(Entity paramEntity);

  public void delete(PrimaryKey paramPrimaryKey);

  public List<Entity> findByHQL(String paramString, Object[] paramArrayOfObject);
}

