package com.revolution.rest.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "conversion")
public class Conversion extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = -3004501193049653693L;

	@Column(name = "target_user_id")
	private Long target_user_id;

	@XmlTransient
	@OneToMany(mappedBy = "conversion", cascade = { javax.persistence.CascadeType.ALL }, fetch = FetchType.LAZY)
	private List<Chat> chats;

	public Long getTarget_user_id() {
		return target_user_id;
	}

	public void setTarget_user_id(Long target_user_id) {
		this.target_user_id = target_user_id;
	}

	public List<Chat> getChats() {
		return chats;
	}

	public void setChats(List<Chat> chats) {
		this.chats = chats;
	}

}
