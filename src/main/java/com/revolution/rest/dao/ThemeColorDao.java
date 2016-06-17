package com.revolution.rest.dao;

import com.revolution.rest.model.Theme_color;

public abstract interface ThemeColorDao extends BaseDao<Theme_color, Long>
{
  public abstract Theme_color getThemeColorByRand();
}

