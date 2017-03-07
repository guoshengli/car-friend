package com.revolution.rest.service.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VideoMedia implements Serializable {
	private static final long serialVersionUID = -7233506673855725689L;
	private String name;
	private String title;
	private int length;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLength() {
		int s = length % 60;
		int m = length/60;
		long h = 0;
		StringBuffer sb = new StringBuffer();
		if(m > 60){
			h = m/60;
			m = m % 60;
		}
		if(h == 0){
			sb.append("00:");
		}else if(h > 0 && h < 10){
			sb.append("0"+h+":");
		}else{
			sb.append(h+":");
		}
		
		if(m == 0){
			sb.append("00:");
		}else if(m > 0 && m < 10){
			sb.append("0"+m+":");
		}else{
			sb.append(m+":");
		}
		
		if(s == 0){
			sb.append("00");
		}else if(s > 0 && s < 10){
			sb.append("0"+s);
		}else{
			sb.append(s);
		}
		return sb.toString();
	}

	public void setLength(int length) {
		this.length = length;
	}

}
