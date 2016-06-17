 package com.revolution.rest.dao;
 
import com.google.common.base.Strings;
import com.revolution.rest.model.Timeline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Query;
 import org.hibernate.Session;
 import org.springframework.stereotype.Repository;
 
 @Repository("timelineDao")
 @SuppressWarnings({"unchecked"})
 public class TimelineDaoImpl extends BaseDaoImpl<Timeline, Long>
   implements TimelineDao
 {
   public TimelineDaoImpl()
   {
     super(Timeline.class);
   }
 
   public List<Timeline> getTimelinesByUserId(Long userId)
   {
     String hql = " from Timeline where userId=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, userId.longValue());
     List<Timeline> list = query.list();
     return list;
   }
 
   public List<Timeline> getTimelinesPageByUserId(Long userId, Long timelineId, int count, int identifier, String followIds)
   {
     Timeline timeline = (Timeline)get(timelineId);
     List<Timeline> timelineList = new ArrayList<Timeline>();
     if (timeline != null) {
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       long timestamp = timeline.getCreateTime().longValue() * 1000L;
       String create_time = sdf.format(new Date(timestamp));
 
       Session session = getSessionFactory().getCurrentSession();
       String hql = "";
       if (identifier == 1) {
         hql = "from Timeline where creatorId in (" + followIds + ") and createTime >= ? and id != ? and story.status = ? group by story.id order by createTime";
         Query query = session.createQuery(hql);
         query.setString(0, create_time);
         query.setLong(1, timelineId.longValue());
         query.setString(2, "publish");
         query.setMaxResults(count);
         timelineList = query.list();
         Collections.reverse(timelineList);
       }
       else if (identifier == 2) {
         hql = "from Timeline where creatorId in (" + followIds + ") and createTime <= ? and id != ? and story.status = ? group by story.id order by createTime desc";
         Query query = session.createQuery(hql);
         query.setString(0, create_time);
         query.setLong(1, timelineId);
         query.setString(2, "publish");
         query.setMaxResults(count);
         timelineList = query.list();
       }
 
     }
 
     return timelineList;
   }
 
   public void saveTimelines(List<Timeline> timelineList) {
     Session session = getSessionFactory().getCurrentSession();
     if ((timelineList != null) && (timelineList.size() > 0))
       for (int i = 0; i < timelineList.size(); i++) {
         session.save(timelineList.get(i));
         if (i % 50 == 0) {
           session.flush();
           session.clear();
         }
       }
   }
 
   public List<Timeline> getTimelinesPageByUserId(Long userId, int count, String followIds)
   {
     List<Timeline> timelineList = new ArrayList<Timeline>();
     String hql = "from Timeline where creatorId in (" + followIds + ") and story.status =? group by story.id order by createTime desc";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setString(0, "publish");
     query.setMaxResults(count);
     timelineList = query.list();
 
     return timelineList;
   }
 
   public void deleteTimelines(Long creator_id, Long target_user_id)
   {
     String hql = "delete from Timeline where creatorId= ? and targetUserId=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, creator_id.longValue());
     query.setLong(1, target_user_id.longValue());
     query.executeUpdate();
   }
 
   public void deleteTimelineByType(Long creator_id, Long storyId, String type)
   {
     String hql = "delete from Timeline where creatorId= ? and story.id=? and type=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, creator_id.longValue());
     query.setLong(1, storyId.longValue());
     query.setString(2, type);
     query.executeUpdate();
   }
 
   public Timeline getTimelineByUseridAndStoryIdAndType(Long userid, Long storyId, String type)
   {
     String hql = "from Timeline where creatorId=? and story.id=? and type=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, userid.longValue());
     query.setLong(1, storyId.longValue());
     query.setString(2, type);
     List<Timeline> list = query.list();
     Timeline timeline = null;
     if ((list != null) && (list.size() > 0)) {
       timeline = (Timeline)list.get(0);
     }
     return timeline;
   }
 
   public void deleteTimelineByStoryId(Long storyId)
   {
     String hql = "delete from Timeline where story.id=?";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, storyId.longValue());
     query.executeUpdate();
   }
 
   
public List<Timeline> getTimelinesPageByRecommand(Long userId, int count, String followIds,String dates,String date_before)
   {
	 Session session = getSessionFactory().getCurrentSession();
	 List<Timeline> timelineList = new ArrayList<Timeline>();
	 if(Strings.isNullOrEmpty(dates) && Strings.isNullOrEmpty(date_before)){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	     String date = sdf.format(new Date());
	     date = date + " 00:00";
	     
	     String hql = "from Timeline where 1=1 and story.status =?  and referenceId=story.id and story.recommendation=true and type='post' and story.recommend_date is not null and story.recommend_date < ? order by story.recommend_date desc";
	     
	     Query query = session.createQuery(hql);
	     query.setString(0, "publish");
	     query.setString(1, date);
	     query.setMaxResults(count);
	     timelineList = query.list();
	 }else if(!Strings.isNullOrEmpty(dates) && Strings.isNullOrEmpty(date_before)){
		 Date nowdate = new Date();
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
		 Date d = null;
		 try {
			d = sdf.parse(dates);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 boolean flag = d.before(nowdate);
		 if(flag){
			 String start = dates+" 00:00";
			 String end = dates + " 23:59";
			 String hql = "from Timeline where 1=1 and story.status =?  and referenceId=story.id and story.recommendation=true and type='post' and story.recommend_date is not null and story.recommend_date >=? and story.recommend_date <= ? order by story.recommend_date desc";
			 Query query = session.createQuery(hql);
		     query.setString(0, "publish");
		     query.setString(1, start);
		     query.setString(2, end);
		     query.setMaxResults(count);
		     timelineList = query.list();
		 }
		 
	 }else if(Strings.isNullOrEmpty(dates) && !Strings.isNullOrEmpty(date_before)){
		 String start = date_before+" 00:00";
		 String hql = "from Timeline where 1=1 and story.status =?  and referenceId=story.id and story.recommendation=true and type='post' and story.recommend_date is not null and story.recommend_date < ? order by story.recommend_date desc";
		 Query query = session.createQuery(hql);
	     query.setString(0, "publish");
	     query.setString(1, start);
	     query.setMaxResults(count);
	     timelineList = query.list();
	 }
     
 
     return timelineList;
   }
 
   public List<Timeline> getTimelinesPageByRecommand(Long userId, Long timelineId, int count, int identifier, String followIds,String dates,String date_before)
   {
     Timeline timeline = (Timeline)get(timelineId);
     List<Timeline> timelineList = new ArrayList<Timeline>();
     if (timeline != null) {
       Timeline t = (Timeline)get(timelineId);
       String date = t.getStory().getRecommend_date();
 
       Session session = getSessionFactory().getCurrentSession();
       String hql = "";
       if (identifier == 1) {
    	 if(Strings.isNullOrEmpty(dates) && Strings.isNullOrEmpty(date_before)){
    		 hql = "from Timeline where 1=1 and id != ?  and referenceId=story.id and story.status != ? and story.recommendation=true and type='post' and story.recommend_date is not null and story.recommend_date >= ? order by story.recommend_date";
             Query query = session.createQuery(hql);
     
             query.setLong(0, timelineId.longValue());
             query.setString(1, "unpublish");
             query.setString(2, date);
             query.setMaxResults(count);
             timelineList = query.list();
    	 }else if(!Strings.isNullOrEmpty(dates) && Strings.isNullOrEmpty(date_before)){
    		 String end = dates + " 23:59";
    		 hql = "from Timeline where 1=1 and id != ?  and referenceId=story.id and story.status != ? and story.recommendation=true and type='post' and story.recommend_date is not null and story.recommend_date >= ? and story.recommend_date < ? order by story.recommend_date";
             Query query = session.createQuery(hql);
     
             query.setLong(0, timelineId.longValue());
             query.setString(1, "unpublish");
             query.setString(2, date);
             query.setString(3, end);
             query.setMaxResults(count);
             timelineList = query.list();
    	 }else if(!Strings.isNullOrEmpty(dates) && Strings.isNullOrEmpty(date_before)){
    		 String end = dates + " 00:00";
    		 hql = "from Timeline where 1=1 and id != ?  and referenceId=story.id and story.status != ? and story.recommendation=true and type='post' and story.recommend_date is not null and story.recommend_date >= ? and story.recommend_date < ? order by story.recommend_date";
             Query query = session.createQuery(hql);
     
             query.setLong(0, timelineId.longValue());
             query.setString(1, "unpublish");
             query.setString(2, date);
             query.setString(3, end);
             query.setMaxResults(count);
             timelineList = query.list();
    	 }
        
         Collections.reverse(timelineList);
       }
       else if (identifier == 2) {
    	   if(Strings.isNullOrEmpty(dates) && Strings.isNullOrEmpty(date_before)){
    		   hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status != ? and type='post' and story.recommend_date is not null and story.recommendation=true and story.recommend_date <= ? order by story.recommend_date desc";
    	         Query query = session.createQuery(hql);
    	 
    	         query.setLong(0, timelineId.longValue());
    	         query.setString(1, "unpublish");
    	         query.setString(2, date);
    	         query.setMaxResults(count);
    	         timelineList = query.list();
    	   }else if(!Strings.isNullOrEmpty(dates) && Strings.isNullOrEmpty(date_before)){
    		   String start = dates+" 00:00";
    		   hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status != ? and type='post' and story.recommend_date is not null and story.recommendation=true and story.recommend_date <= ? and story.recommend_date > ? order by story.recommend_date desc";
  	         Query query = session.createQuery(hql);
  	 
  	         query.setLong(0, timelineId.longValue());
  	         query.setString(1, "unpublish");
  	         query.setString(2, date);
  	         query.setString(3,start);
  	         query.setMaxResults(count);
  	         timelineList = query.list();
    	   }else if(Strings.isNullOrEmpty(dates) && !Strings.isNullOrEmpty(date_before)){
    		   String start = date_before+" 00:00";
    		   hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status != ? and type='post' and story.recommend_date is not null and story.recommendation=true and story.recommend_date <= ? and story.recommend_date < ? and story.recommend_date > ? order by story.recommend_date desc";
  	         Query query = session.createQuery(hql);
  	 
  	         query.setLong(0, timelineId.longValue());
  	         query.setString(1, "unpublish");
  	         query.setString(2, date);
  	         query.setString(3,start);
  	         query.setMaxResults(count);
  	         timelineList = query.list();
    	   }
         
       }
 
     }
 
     return timelineList;
   }

@Override
public List<Timeline> getTimelineByRecommandAndRand(int recommand) {
	Session session = getSessionFactory().getCurrentSession();
	String hql = "from Timeline where 1=1 and story.status = ? and type='post' and story.recommend_date is not null and story.recommendation=true"
			+ " ORDER BY rand()" ;
    Query query = session.createQuery(hql);

    query.setString(0, "publish").setMaxResults(recommand);
	List<Timeline> timelineList = query.list();
	return timelineList;
}

public List<Timeline> getTimelineByStoryIdAndType(String storyIds, String type)
{
  String hql = "from Timeline where story.id in(?) and type=?";
  Session session = getSessionFactory().getCurrentSession();
  Query query = session.createQuery(hql);
  query.setString(0, storyIds);
  query.setString(1, type);
  List<Timeline> list = query.list();
  return list;
}

@Override
public List<Timeline> getTimelineByUserIdAndType(Long userId, String type) {
	
	String hql = "from Timeline where 1=1 and creatorId=? and type=? and story.status='publish' order by createTime desc";
	  Session session = getSessionFactory().getCurrentSession();
	  Query query = session.createQuery(hql);
	  query.setLong(0, userId);
	  query.setString(1, type);
	  query.setMaxResults(20);
	  List<Timeline> list = query.list();
	  return list;
}

@Override
public Timeline getTimelineByStoryIdAndType(Long storyId, String type) {
	String hql = "from Timeline where 1=1 and story.id=? and type=?";
	Session session = getSessionFactory().getCurrentSession();
	Query query = session.createQuery(hql);
	query.setLong(0, storyId);
	query.setString(1, type);
	List<Timeline> list = query.list();
	Timeline timeline = null;
	if(list != null && list.size() > 0){
		timeline = list.get(0);
	}
	return timeline;
}

@Override
public List<Timeline> getTimelineByRecommandation(int count) {
	Date now = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String now_date = sdf.format(now);
	
	String hql = "from Timeline where 1=1 and type='recommandation' and story.status='publish' and createTime < ? order by createTime desc";
	Session session = getSessionFactory().getCurrentSession();
	Query query = session.createQuery(hql).setString(0,now_date);
	query.setMaxResults(count);
	List<Timeline> list = query.list();
	return list;
}

@Override
public List<Timeline> getTimelineByRecommandation(Long timelineId, int count, int identify) {
	
    Timeline timeline = (Timeline)get(timelineId);
    List<Timeline> timelineList = new ArrayList<Timeline>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (timeline != null) {
      Timeline t = (Timeline)get(timelineId);
      String date = sdf.format(new Date(t.getCreateTime()*1000));
      Session session = getSessionFactory().getCurrentSession();
      String hql = "";
      if (identify == 1) {
    	  hql = "from Timeline where 1=1 and id != ?  and referenceId=story.id and story.status = ? and type='recommandation' and createTime <= ? order by createTime desc";
          Query query = session.createQuery(hql);
          query.setLong(0, timelineId.longValue());
          query.setString(1, "publish");
          query.setString(2, date);
          query.setMaxResults(count);
          timelineList = query.list();
      }else if (identify == 2) {
    	hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status = ? and type='recommandation' and createTime >= ? order by createTime";
        Query query = session.createQuery(hql);
        query.setLong(0, timelineId.longValue());
        query.setString(1, "publish");
        query.setString(2, date);
        query.setMaxResults(count);
        timelineList = query.list();
        
      }

    }

    return timelineList;
  
}

@Override
public List<Timeline> getTimelineByHome(int count,String followIds) {
	Date now = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String now_date = sdf.format(now);
	String hql = "from Timeline where 1=1 and (creatorId in (?) or type='recommandation') and story.status ='publish' and createTime < ? order by createTime desc";
	Session session = getSessionFactory().getCurrentSession();
	Query query = session.createQuery(hql);
	query.setString(0,followIds).setString(1,now_date);
	query.setMaxResults(count);
	List<Timeline> list = query.list();
	return list;
}

@Override
public List<Timeline> getTimelineByHome(Long timelineId, int count, int identify,String followIds) {
	Timeline timeline = (Timeline)get(timelineId);
    List<Timeline> timelineList = new ArrayList<Timeline>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (timeline != null) {
      Timeline t = (Timeline)get(timelineId);
      String date = sdf.format(new Date(t.getCreateTime()*1000));
      Session session = getSessionFactory().getCurrentSession();
      String hql = "";
      if (identify == 1) {
    	  hql = "from Timeline where 1=1 and id != ?  and referenceId=story.id and story.status = ? and createTime <= ? and (creatorId in (?) or type='recommandation') order by createTime desc";
          Query query = session.createQuery(hql);
          query.setLong(0, timelineId);
          query.setString(1, "publish");
          query.setString(2, date);
          query.setString(3, followIds);
          query.setMaxResults(count);
          timelineList = query.list();
          //Collections.reverse(timelineList);
      }else if (identify == 2) {
    	hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status = ? and createTime >= ? and (creatorId in (?) or type='recommandation') order by createTime desc";
        Query query = session.createQuery(hql);
        query.setLong(0, timelineId);
        query.setString(1, "publish");
        query.setString(2, date);
        query.setString(3, followIds);
        query.setMaxResults(count);
        timelineList = query.list();
       
      }

    }

    return timelineList;
}

@Override
public List<Timeline> getTimelineBySquare(int count) {
	String hql = "from Timeline where 1=1 and type='post' and story.status='publish' order by createTime desc";
	Session session = getSessionFactory().getCurrentSession();
	Query query = session.createQuery(hql);
	query.setMaxResults(count);
	List<Timeline> list = query.list();
	return list;
}

@Override
public List<Timeline> getTimelineBySquare(Long timelineId, int count, int identify) {

	
    Timeline timeline = (Timeline)get(timelineId);
    List<Timeline> timelineList = new ArrayList<Timeline>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (timeline != null) {
      Timeline t = (Timeline)get(timelineId);
      String date = sdf.format(new Date(t.getCreateTime()*1000));
      Session session = getSessionFactory().getCurrentSession();
      String hql = "";
      if (identify == 1) {
    	  hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status = ? and type='post' and createTime <= ? order by createTime desc";
          Query query = session.createQuery(hql);
          query.setLong(0, timelineId.longValue());
          query.setString(1, "publish");
          query.setString(2, date);
          query.setMaxResults(count);
          timelineList = query.list();
      }else if (identify == 2) {
    	hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status = ? and type='post' and createTime <= ? order by createTime";
        Query query = session.createQuery(hql);
        query.setLong(0, timelineId.longValue());
        query.setString(1, "publish");
        query.setString(2, date);
        query.setMaxResults(count);
        timelineList = query.list();
        Collections.reverse(timelineList);
      }

    }

    return timelineList;
  

}

@Override
public List<Timeline> getTimelineByUserIdAndType(Long userId, String type, int count) {
	  String hql = "from Timeline where 1=1 and creatorId=? and type=? and story.status !='unpublish' and story.status !='disabled' order by createTime desc";
	  Session session = getSessionFactory().getCurrentSession();
	  Query query = session.createQuery(hql);
	  query.setLong(0, userId);
	  query.setString(1, type);
	  query.setMaxResults(count);
	  List<Timeline> list = query.list();
	  return list;
}

@Override
public List<Timeline> getTimelineByUserIdAndType(Long userId, String type, int count, Long id, int identify) {
	Timeline timeline = get(id);
    List<Timeline> timelineList = new ArrayList<Timeline>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (timeline != null) {
      String date = sdf.format(new Date(timeline.getCreateTime()*1000));
      Session session = getSessionFactory().getCurrentSession();
      String hql = "";
      if (identify == 1) {
    	  hql = "from Timeline where 1=1 and id != ?  and referenceId=story.id and story.status != ? and story.status !='disabled' and type=? and createTime <= ? and creatorId=? order by createTime desc";
          Query query = session.createQuery(hql);
          query.setLong(0, id);
          query.setString(1, "unpublish");
          query.setString(2, type);
          query.setString(3, date);
          query.setLong(4, userId);
          query.setMaxResults(count);
          timelineList = query.list();
      }else if (identify == 2) {
    	hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status != ? and story.status !='disabled' and type=? and createTime <= ? and creatorId=? order by createTime desc";
        Query query = session.createQuery(hql);
        query.setLong(0, id);
        query.setString(1, "unpublish");
        query.setString(2, type);
        query.setString(3, date);
        query.setLong(4, userId);
        query.setMaxResults(count);
        timelineList = query.list();
        //Collections.reverse(timelineList);
      }

    }

    return timelineList;
}

@Override
public void deleteTimelineByStoryIdAndType(Long paramLong) {
	 String hql = "delete from Timeline where story.id=? and type='repost'";
     Session session = getSessionFactory().getCurrentSession();
     Query query = session.createQuery(hql);
     query.setLong(0, paramLong);
     query.executeUpdate();
}

@Override
public List<Timeline> getTimelineByUserIdAndTypeAndStatus(Long userId, String type, int count, String status) {

	  String hql = "from Timeline where 1=1 and creatorId=? and type=? and story.status =? order by createTime desc";
	  Session session = getSessionFactory().getCurrentSession();
	  Query query = session.createQuery(hql);
	  query.setLong(0, userId);
	  query.setString(1, type);
	  query.setString(2,status);
	  query.setMaxResults(count);
	  List<Timeline> list = query.list();
	  return list;

}

@Override
public List<Timeline> getTimelineByUserIdAndTypeAndStatus(Long userId, String type, int count, Long id, int identify,
		String status) {
	Timeline timeline = get(id);
    List<Timeline> timelineList = new ArrayList<Timeline>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (timeline != null) {
      String date = sdf.format(new Date(timeline.getCreateTime()*1000));
      Session session = getSessionFactory().getCurrentSession();
      String hql = "";
      if (identify == 1) {
    	  hql = "from Timeline where 1=1 and id != ?  and referenceId=story.id and story.status = ? and type=? and createTime <= ? and creatorId=? order by createTime desc";
          Query query = session.createQuery(hql);
          query.setLong(0, id);
          query.setString(1, status);
          query.setString(2, type);
          query.setString(3, date);
          query.setLong(4, userId);
          query.setMaxResults(count);
          timelineList = query.list();
      }else if (identify == 2) {
    	hql = "from Timeline where 1=1 and id != ? and referenceId=story.id and story.status = ? and type=? and createTime <= ? and creatorId=? order by createTime desc";
        Query query = session.createQuery(hql);
        query.setLong(0, id);
        query.setString(1, status);
        query.setString(2, type);
        query.setString(3, date);
        query.setLong(4, userId);
        query.setMaxResults(count);
        timelineList = query.list();
        //Collections.reverse(timelineList);
      }

    }

    return timelineList;
}


 }

