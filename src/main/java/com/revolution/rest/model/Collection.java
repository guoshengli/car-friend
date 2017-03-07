package com.revolution.rest.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "collection")
public class Collection extends BaseEntity<Long>implements Serializable {
	private static final long serialVersionUID = -5992512537630475742L;

	@Column(name = "collection_name")
	private String collectionName;

	@Column(name = "cover_image")
	@Type(type = "text")
	private String cover_image;

	@Column(name = "status")
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "authorId")
	private User user;

	@Column(name = "sequnce")
	private Long number;

	@Column(name = "info")
	private String info;

	@Column(name = "view_count")
	private int view_count;

	@ManyToMany(mappedBy = "collections", fetch = FetchType.LAZY)
	private Set<Story> stories;

	@ManyToMany(mappedBy = "collections", fetch = FetchType.LAZY)
	private Set<User> users;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interest_id", updatable = false)
	private Interest interest;

	public String getCollectionName() {
		return this.collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getCover_image() {
		return this.cover_image;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<Story> getStories() {
		return this.stories;
	}

	public void setStories(Set<Story> stories) {
		this.stories = stories;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getNumber() {
		return this.number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}
	/*
	 * public Theme_color getTheme_color() { return theme_color; }
	 * 
	 * @JsonBackReference public void setTheme_color(Theme_color theme_color) {
	 * this.theme_color = theme_color; }
	 */

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}



	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public Interest getInterest() {
		return interest;
	}

	public void setInterest(Interest interest) {
		this.interest = interest;
	}

}
