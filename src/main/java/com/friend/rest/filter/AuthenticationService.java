package com.friend.rest.filter;

import java.util.Map;

public abstract interface AuthenticationService
{
  public abstract boolean authenticateUser(Map<String, String> paramMap);
}

