 package com.revolution.rest;
 
 import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.revolution.rest.service.AdminServiceImpl;
import com.revolution.rest.service.BasicParamServiceImpl;
import com.revolution.rest.service.CDNAccessServiceImpl;
import com.revolution.rest.service.CollectionServiceImpl;
import com.revolution.rest.service.CollectionStoryServiceImpl;
import com.revolution.rest.service.ColumnsServiceImpl;
import com.revolution.rest.service.CommentServiceImpl;
import com.revolution.rest.service.ConfigurationServiceImpl;
import com.revolution.rest.service.DiscoverServiceImpl;
import com.revolution.rest.service.FollowServiceImpl;
import com.revolution.rest.service.HtmlServiceImpl;
import com.revolution.rest.service.LikesServiceImpl;
import com.revolution.rest.service.NotificationServiceImpl;
import com.revolution.rest.service.PingServiceImpl;
import com.revolution.rest.service.RepublishServiceImpl;
import com.revolution.rest.service.ShareServiceImpl;
import com.revolution.rest.service.StoryServiceImpl;
import com.revolution.rest.service.TimelineServiceImpl;
import com.revolution.rest.service.UserServiceImpl;
 
 public class Application extends ResourceConfig
 {
   public Application()
   {
     register(JacksonJsonProvider.class);
     register(PingServiceImpl.class);
     register(ColumnsServiceImpl.class);
     register(CollectionServiceImpl.class);
     register(CollectionStoryServiceImpl.class);
     register(CommentServiceImpl.class);
     register(ConfigurationServiceImpl.class);
     register(FollowServiceImpl.class);
     register(LikesServiceImpl.class);
     register(NotificationServiceImpl.class);
     register(RepublishServiceImpl.class);
     register(StoryServiceImpl.class);
     register(TimelineServiceImpl.class);
     register(UserServiceImpl.class);
     register(CDNAccessServiceImpl.class);
     register(AdminServiceImpl.class);
     register(DiscoverServiceImpl.class);
     register(ShareServiceImpl.class);
     register(BasicParamServiceImpl.class);
     register(HtmlServiceImpl.class);
     
   }
 }

