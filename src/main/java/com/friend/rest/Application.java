 package com.friend.rest;
 
 import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.friend.rest.service.AdminServiceImpl;
import com.friend.rest.service.BasicParamServiceImpl;
import com.friend.rest.service.CDNAccessServiceImpl;
import com.friend.rest.service.CollectionServiceImpl;
import com.friend.rest.service.ColumnsServiceImpl;
import com.friend.rest.service.CommentServiceImpl;
import com.friend.rest.service.ConfigurationServiceImpl;
import com.friend.rest.service.FMapServiceImpl;
import com.friend.rest.service.HtmlServiceImpl;
import com.friend.rest.service.NotificationServiceImpl;
import com.friend.rest.service.PingServiceImpl;
import com.friend.rest.service.ShareServiceImpl;
import com.friend.rest.service.StoryServiceImpl;
import com.friend.rest.service.UserServiceImpl;
 
 public class Application extends ResourceConfig
 {
   public Application()
   {
     register(JacksonJsonProvider.class);
     register(PingServiceImpl.class);
     register(ColumnsServiceImpl.class);
     register(CollectionServiceImpl.class);
     register(CommentServiceImpl.class);
     register(ConfigurationServiceImpl.class);
     register(NotificationServiceImpl.class);
     register(StoryServiceImpl.class);
     register(UserServiceImpl.class);
     register(CDNAccessServiceImpl.class);
     register(AdminServiceImpl.class);
//     register(DiscoverServiceImpl.class);
     register(ShareServiceImpl.class);
     register(BasicParamServiceImpl.class);
     register(HtmlServiceImpl.class);
     register(FMapServiceImpl.class);
     
   }
 }

