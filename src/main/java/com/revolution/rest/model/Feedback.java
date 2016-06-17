package com.revolution.rest.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name="feedback")
public class Feedback extends BaseEntity<Long>implements Serializable {

	private static final long serialVersionUID = -5883664464431454771L;
	@Column(name="image")
	private String cover_image;
	
	@Column(name="content")
	private String info;
	
	@Column(name="user_id")
	private Long user_id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Generated(GenerationTime.ALWAYS)
    @Column(name="create_time", insertable=false, updatable=false)
    private Date created_time;

	public String getCover_image() {
		return cover_image;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getCreated_time() {
     return Long.valueOf(this.created_time.getTime() / 1000L);
   }

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}
	
	

}
