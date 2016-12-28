package com.revolution.rest.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "chat")
public class Chat extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 6552344980147194565L;
	@Column(name = "current_user_id")
	private Long current_user_id;

	@Column(name = "target_user_id")
	private Long target_user_id;

	@Column(name = "content", columnDefinition="LONGTEXT")
	private String content;

	@Column(name = "picture")
	private String picture;

	@Temporal(TemporalType.TIMESTAMP)
	@Generated(GenerationTime.ALWAYS)
	@Column(name = "create_time", insertable = false, updatable = false)
	private Date create_time;
	
	@XmlTransient
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="conversion_id", updatable=false)
	private Conversion conversion;
	
	 

	public Long getCurrent_user_id() {
		return current_user_id;
	}

	public void setCurrent_user_id(Long current_user_id) {
		this.current_user_id = current_user_id;
	}

	public Long getTarget_user_id() {
		return target_user_id;
	}

	public void setTarget_user_id(Long target_user_id) {
		this.target_user_id = target_user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Long getCreate_time() {
		return Long.valueOf(create_time.getTime() / 1000L);
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Conversion getConversion() {
		return conversion;
	}

	public void setConversion(Conversion conversion) {
		this.conversion = conversion;
	}
	
	

}
