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
@Entity
@Table(name="user_collection")
public class UserCollection extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = -4428508278901353456L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User users;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="collection_id")
	private Collection collections;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.ALWAYS)
    @Column(name="create_time", insertable=false)
    private Date create_time;
	
	@Column(name="sequence")
	private int sequence;


	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	public Collection getCollections() {
		return collections;
	}

	public void setCollections(Collection collections) {
		this.collections = collections;
	}

	 public Long getCreate_time()
	   {
	     return Long.valueOf(create_time.getTime() / 1000L);
	   }

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	

}
