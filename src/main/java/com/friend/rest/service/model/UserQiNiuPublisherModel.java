package com.friend.rest.service.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

@XmlRootElement
public class UserQiNiuPublisherModel extends UserParentModel implements Serializable {
	private static final long serialVersionUID = 5854604288425666043L;
	private Long id;
	private String username;
	private String email;
	private String zone;
	private String phone;
	private Long created_time;
	private String status;
	private String introduction;
	private JSONObject avatar_image;
	private JSONObject cover_image;
	private int reposts_count;
	private int stories_count;
	private int followers_count;
	private int following_count;
	private String website;
	private String profile_url;
	private JSONObject notification_prefs;
	private JSONObject direct_upload_QN_params;
	private boolean email_verified;
	private List<LinkAccountModel> linked_account;
	private List<CollectionIntroLast> collection_info;
	private List<CoverPageModel> default_cover_images;
	private String user_type;
	private List<PublisherInfoModel> publisher_info;
	private String share_url_prefix;
	private String appstore_link;
	private String gender;

	public UserQiNiuPublisherModel() {
	}

	public UserQiNiuPublisherModel(Long id, String username, String email, String zone, String phone, Long created_time,
			String status, String introduction, JSONObject avatar_image, JSONObject cover_image,
			int reposts_count, int stories_count, int followers_count, int following_count, String website,
			String profile_url, JSONObject notification_prefs, JSONObject direct_upload_QN_params,
			boolean email_verified, List<LinkAccountModel> linked_account, List<CollectionIntroLast> collection_info,
			 List<CoverPageModel> default_cover_images,
			String user_type, List<PublisherInfoModel> publisher_info,String share_url_prefix,String appstore_link,String gender) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.zone = zone;
		this.phone = phone;
		this.created_time = created_time;
		this.status = status;
		this.introduction = introduction;
		this.avatar_image = avatar_image;
		this.cover_image = cover_image;
		this.reposts_count = reposts_count;
		this.stories_count = stories_count;
		this.followers_count = followers_count;
		this.following_count = following_count;
		this.website = website;
		this.profile_url = profile_url;
		this.notification_prefs = notification_prefs;
		this.direct_upload_QN_params = direct_upload_QN_params;
		this.email_verified = email_verified;
		this.linked_account = linked_account;
		this.collection_info = collection_info;
		this.default_cover_images = default_cover_images;
		this.user_type = user_type;
		this.publisher_info = publisher_info;
		this.share_url_prefix = share_url_prefix;
		this.appstore_link = appstore_link;
		this.gender = gender;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getCreated_time() {
		return this.created_time;
	}

	public void setCreated_time(Long created_time) {
		this.created_time = created_time;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIntroduction() {
		return this.introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public JSONObject getAvatar_image() {
		return this.avatar_image;
	}

	public void setAvatar_image(JSONObject avatar_image) {
		this.avatar_image = avatar_image;
	}

	public JSONObject getCover_image() {
		return this.cover_image;
	}

	public void setCover_image(JSONObject cover_image) {
		this.cover_image = cover_image;
	}


	public int getReposts_count() {
		return this.reposts_count;
	}

	public void setReposts_count(int reposts_count) {
		this.reposts_count = reposts_count;
	}

	public int getStories_count() {
		return this.stories_count;
	}

	public void setStories_count(int stories_count) {
		this.stories_count = stories_count;
	}

	public int getFollowers_count() {
		return this.followers_count;
	}

	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	public int getFollowing_count() {
		return this.following_count;
	}

	public void setFollowing_count(int following_count) {
		this.following_count = following_count;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getProfile_url() {
		return this.profile_url;
	}

	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}

	public JSONObject getNotification_prefs() {
		return this.notification_prefs;
	}

	public void setNotification_prefs(JSONObject notification_prefs) {
		this.notification_prefs = notification_prefs;
	}

	public JSONObject getDirect_upload_QN_params() {
		return this.direct_upload_QN_params;
	}

	public void setDirect_upload_QN_params(JSONObject direct_upload_QN_params) {
		this.direct_upload_QN_params = direct_upload_QN_params;
	}

	public boolean isEmail_verified() {
		return this.email_verified;
	}

	public void setEmail_verified(boolean email_verified) {
		this.email_verified = email_verified;
	}

	public List<LinkAccountModel> getLinked_account() {
		return this.linked_account;
	}

	public void setLinked_account(List<LinkAccountModel> linked_account) {
		this.linked_account = linked_account;
	}

	public List<CollectionIntroLast> getCollection_info() {
		return collection_info;
	}

	public void setCollection_info(List<CollectionIntroLast> collection_info) {
		this.collection_info = collection_info;
	}

	public List<CoverPageModel> getDefault_cover_images() {
		return this.default_cover_images;
	}

	public void setDefault_cover_images(List<CoverPageModel> default_cover_images) {
		this.default_cover_images = default_cover_images;
	}

	public String getUser_type() {
		return this.user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public List<PublisherInfoModel> getPublisher_info() {
		return this.publisher_info;
	}

	public void setPublisher_info(List<PublisherInfoModel> publisher_info) {
		this.publisher_info = publisher_info;
	}

	public String getZone() {
		return this.zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShare_url_prefix() {
		return share_url_prefix;
	}

	public void setShare_url_prefix(String share_url_prefix) {
		this.share_url_prefix = share_url_prefix;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAppstore_link() {
		return appstore_link;
	}

	public void setAppstore_link(String appstore_link) {
		this.appstore_link = appstore_link;
	}
	
	
	
}
