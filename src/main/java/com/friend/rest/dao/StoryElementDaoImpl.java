 package com.friend.rest.dao;
 
 import java.util.List;
 import org.hibernate.Query;
 import org.springframework.stereotype.Repository;

import com.friend.rest.model.StoryElement;
 
 @Repository("storyElementDao")
 public class StoryElementDaoImpl extends BaseDaoImpl<StoryElement, Long>
   implements StoryElementDao
 {
   public StoryElementDaoImpl()
   {
     super(StoryElement.class);
   }
 
   public void delete(List<StoryElement> se)
   {
     String hql = "";
     int i = 0;
     for (StoryElement s : se)
     {
       if (i == 0)
         hql = "id=" + s.getId();
       else {
         hql = hql + " or id=" + s.getId();
       }
       i++;
     }
     Query q = getSessionFactory().getCurrentSession().createQuery(
       "delete from StoryElement where " + hql);
     q.executeUpdate();
   }
 }

