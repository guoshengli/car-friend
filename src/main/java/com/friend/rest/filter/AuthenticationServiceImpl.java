 package com.friend.rest.filter;
 
 import com.friend.rest.common.EncryptionUtil;
import com.friend.rest.dao.UserDao;
import com.friend.rest.model.User;
import com.google.common.base.Strings;

import java.util.Map;
 import org.apache.commons.logging.Log;
 import org.apache.commons.logging.LogFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 
 public class AuthenticationServiceImpl
   implements AuthenticationService
 {
   private static final Log log = LogFactory.getLog(AuthenticationServiceImpl.class);
 
   @Autowired
   UserDao userDao;
 
   public boolean authenticateUser(Map<String, String> params)
   {
     String userId = params.get("userId") != null ? (String)params.get("userId") : "";
     String timestamp = params.get("timestamp") != null ? (String)params.get("timestamp") : "";
     String token = params.get("token") != null ? (String)params.get("token") : "";
 
     if ((Strings.isNullOrEmpty(userId)) || (Strings.isNullOrEmpty(timestamp)) || (Strings.isNullOrEmpty(token))) {
       log.error(String.format("[**Auth Failure**] Missing params: userId:%s timestamp:%s token:%s", new Object[] { 
         userId, timestamp, token }));
       return false;
     }
 
     User user = (User)this.userDao.get(Long.valueOf(Long.parseLong(userId)));
     if (user == null) {
       log.error(String.format("[**Auth Failure**] invalid userId:%s timestamp:%s token:%s", new Object[] { 
         userId, timestamp, token }));
       return false;
     }
 
     String raw = user.getEmail() + user.getPassword() + timestamp;
     String generatedToken = EncryptionUtil.hashMessage(raw);
     log.debug("[**Debug Info**] raw: " + raw);
     log.debug("[**Debug Info**] server calculated hash is: " + generatedToken);
     log.debug("[**Debug Info**] provided hash is: " + token);
 
     if (!token.equals(generatedToken)) {
       log.error(String.format("[**Auth Failure**] invalid token for userId:%s timestamp:%s token:%s", new Object[] { 
         userId, timestamp, token }));
       return false;
     }
 
     return true;
   }
 }

