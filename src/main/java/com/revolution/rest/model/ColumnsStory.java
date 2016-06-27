package com.revolution.rest.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
@Table
@Entity(name="columns_story")
public class ColumnsStory extends BaseEntity<Long>implements Serializable {

	private static final long serialVersionUID = 4584129142985705955L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="columns_id")
	private Columns columns;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="story_id")
	private Story story;
	
	@Temporal(TemporalType.TIMESTAMP)
   @Generated(GenerationTime.ALWAYS)
   @Column(name="create_time", insertable=false)
   private Date create_time;
	
	@Column(name="operator")
	private Long operator;

	public Columns getColumns() {
		return columns;
	}

	public void setColumns(Columns columns) {
		this.columns = columns;
	}

	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Long getOperator() {
		return operator;
	}

	public void setOperator(Long operator) {
		this.operator = operator;
	}

	
}
