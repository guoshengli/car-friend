package com.friend.rest.model;

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
@Table(name="send_message")
public class SendMessage extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 5820037930918277208L;
	
	@Column(name="title")
	private String title;
	
	@Column(name="type")
	private String type;
	
	@Column(name="reference_id")
	private Long reference_id;
	
	@Column(name = "create_time")
	private Date create_time;
	
	@Column(name="url")
	private String url;
	
	@Column(name="admin_id")
	private Long admin_id;
	
	@Column(name="send_type")
	private int send_type;
	
	@Column(name="send_time")
	private String send_time;
	
	@Column(name="description")
	private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getReference_id() {
		return reference_id;
	}

	public void setReference_id(Long reference_id) {
		this.reference_id = reference_id;
	}

	public long getCreate_time() {
		return create_time.getTime()/1000;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getAdmin_id() {
		return admin_id;
	}

	public void setAdmin_id(Long admin_id) {
		this.admin_id = admin_id;
	}

	public int getSend_type() {
		return send_type;
	}

	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
}
