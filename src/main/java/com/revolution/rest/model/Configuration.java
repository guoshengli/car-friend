 package com.revolution.rest.model;
 
 import java.io.Serializable;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Table;
 
 @Entity
 @Table(name="configuration")
 public class Configuration extends BaseEntity<Long>
   implements Serializable
 {
   private static final long serialVersionUID = 6042495342386712849L;
 
   @Column(name="notify_new_follower", columnDefinition="BIT")
   private boolean new_follower_push;
 
   @Column(name="notify_is_liked", columnDefinition="BIT")
   private boolean new_favorite_from_following_push;
 
   @Column(name="notify_is_republished", columnDefinition="BIT")
   private boolean reposted_my_story_push;
 
   @Column(name="notify_new_story", columnDefinition="BIT")
   private boolean new_story_from_following_push;
 
   @Column(name="notify_new_comment", columnDefinition="BIT")
   private boolean new_comment_on_your_story_push;
 
   @Column(name="notify_reply_comment", columnDefinition="BIT")
   private boolean new_comment_on_your_comment_push;
 
   @Column(name="notify_recommend", columnDefinition="BIT")
   private boolean recommended_my_story_push;
   
   @Column(name="notify_recommend_slide", columnDefinition="BIT")
   private boolean recommended_my_story_slide_push;
 
   @Column(name="notify_admin_push", columnDefinition="BIT")
   private boolean new_admin_push;
   
/*   @Column(name="new_story_from_collection_push", columnDefinition="BIT")
   private boolean new_story_from_collection_push; //9
   
   @Column(name="delete_story_from_collection_push", columnDefinition="BIT")
   private boolean delete_story_from_collection_push; //10
   
   @Column(name="new_story_from_collection_review_push", columnDefinition="BIT")
   private boolean new_story_from_collection_review_push; //11
   
   @Column(name="collection_review_agree_push", columnDefinition="BIT")
   private boolean collection_review_agree_push; //12
   
   @Column(name="collection_review_reject_push", columnDefinition="BIT")
   private boolean collection_review_reject_push; //13   
   
   @Column(name="create_collection_push", columnDefinition="BIT")
   private boolean create_collection_push; //14
   
   @Column(name="story_move_to_collection", columnDefinition="BIT")
   private boolean story_move_to_collection; //14
*/   
   @Column(name="user_id")
   private Long userId;
 
   public boolean isNew_follower_push()
   {
     return this.new_follower_push;
   }
 
   public void setNew_follower_push(boolean new_follower_push) {
     this.new_follower_push = new_follower_push;
   }
 
   public boolean isNew_favorite_from_following_push() {
     return this.new_favorite_from_following_push;
   }
 
   public void setNew_favorite_from_following_push(boolean new_favorite_from_following_push)
   {
     this.new_favorite_from_following_push = new_favorite_from_following_push;
   }
 
   public boolean isReposted_my_story_push() {
     return this.reposted_my_story_push;
   }
 
   public void setReposted_my_story_push(boolean reposted_my_story_push) {
     this.reposted_my_story_push = reposted_my_story_push;
   }
 
   public boolean isNew_story_from_following_push() {
     return this.new_story_from_following_push;
   }
 
   public void setNew_story_from_following_push(boolean new_story_from_following_push)
   {
     this.new_story_from_following_push = new_story_from_following_push;
   }
 
   public boolean isNew_comment_on_your_story_push() {
     return this.new_comment_on_your_story_push;
   }
 
   public void setNew_comment_on_your_story_push(boolean new_comment_on_your_story_push)
   {
     this.new_comment_on_your_story_push = new_comment_on_your_story_push;
   }
 
   public boolean isNew_comment_on_your_comment_push() {
     return this.new_comment_on_your_comment_push;
   }
 
   public void setNew_comment_on_your_comment_push(boolean new_comment_on_your_comment_push)
   {
     this.new_comment_on_your_comment_push = new_comment_on_your_comment_push;
   }
 
   public boolean isRecommended_my_story_push() {
     return this.recommended_my_story_push;
   }
 
   public void setRecommended_my_story_push(boolean recommended_my_story_push) {
     this.recommended_my_story_push = recommended_my_story_push;
   }
 
   public boolean isNew_admin_push() {
     return this.new_admin_push;
   }
 
   public void setNew_admin_push(boolean new_admin_push) {
     this.new_admin_push = new_admin_push;
   }
   
   
   
 
   public boolean isRecommended_my_story_slide_push() {
	return recommended_my_story_slide_push;
}

public void setRecommended_my_story_slide_push(boolean recommended_my_story_slide_push) {
	this.recommended_my_story_slide_push = recommended_my_story_slide_push;
}

/*public boolean isNew_story_from_collection_push() {
	return new_story_from_collection_push;
}

public void setNew_story_from_collection_push(boolean new_story_from_collection_push) {
	this.new_story_from_collection_push = new_story_from_collection_push;
}

public boolean isDelete_story_from_collection_push() {
	return delete_story_from_collection_push;
}

public void setDelete_story_from_collection_push(boolean delete_story_from_collection_push) {
	this.delete_story_from_collection_push = delete_story_from_collection_push;
}

public boolean isNew_story_from_collection_review_push() {
	return new_story_from_collection_review_push;
}

public void setNew_story_from_collection_review_push(boolean new_story_from_collection_review_push) {
	this.new_story_from_collection_review_push = new_story_from_collection_review_push;
}

public boolean isCollection_review_agree_push() {
	return collection_review_agree_push;
}

public void setCollection_review_agree_push(boolean collection_review_agree_push) {
	this.collection_review_agree_push = collection_review_agree_push;
}

public boolean isCollection_review_reject_push() {
	return collection_review_reject_push;
}

public void setCollection_review_reject_push(boolean collection_review_reject_push) {
	this.collection_review_reject_push = collection_review_reject_push;
}


public boolean isCreate_collection_push() {
	return create_collection_push;
}

public void setCreate_collection_push(boolean create_collection_push) {
	this.create_collection_push = create_collection_push;
}



public boolean isStory_move_to_collection() {
	return story_move_to_collection;
}

public void setStory_move_to_collection(boolean story_move_to_collection) {
	this.story_move_to_collection = story_move_to_collection;
}
*/
public Long getUserId() {
     return this.userId;
   }
 
   public void setUserId(Long userId) {
     this.userId = userId;
   }
 }

