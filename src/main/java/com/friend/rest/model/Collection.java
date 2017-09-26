package com.friend.rest.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "collection")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Collection implements Serializable {
	private static final long serialVersionUID = -5992512537630475742L;
	@Id
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "collection_name")
	private String collection_name;

	@Column(name = "cover_image")
	@Type(type = "text")
	private String cover_image;
	
	@Column(name = "thumbnail")
	@Type(type = "text")
	private String thumbnail;

	@Column(name = "status")
	private String status;

	@Column(name = "sequnce")
	private int number;

	@Column(name = "description")
	@Type(type = "text")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "districts_id")
	private Districts districts;

	@Column(name = "type")
	private int type;

	@Column(name = "logo")
	@Type(type = "text")
	private String logo;

	@Column(name = "reference_id")
	private int reference_id;

	// 车型对应的详情链接
	@Column(name = "car_url")
	private String car_url;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "category_collection", joinColumns = { @JoinColumn(name = "category_id") }, inverseJoinColumns = {
			@JoinColumn(name = "collection_id") })
	private Set<Category> categories;

	public String getCollection_name() {
		return collection_name;
	}

	public void setCollection_name(String collection_name) {
		this.collection_name = collection_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Districts getDistricts() {
		return districts;
	}

	public void setDistricts(Districts districts) {
		this.districts = districts;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getReference_id() {
		return reference_id;
	}

	public void setReference_id(int reference_id) {
		this.reference_id = reference_id;
	}

	public String getCar_url() {
		return car_url;
	}

	public void setCar_url(String car_url) {
		this.car_url = car_url;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	

}
