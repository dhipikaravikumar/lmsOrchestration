package com.gcit.springboot.lms.entity;

import java.io.Serializable;

public class Publisher implements Serializable{
	
	private static final long serialVersionUID = -1621034780628554130L;
	
	private Integer pubId;
	private String pubName;
	private String pubPhone;
	private String pubAddress;
	/**
	 * @return the pubId
	 */
	public Integer getPubId() {
		return pubId;
	}
	/**
	 * @param pubId the pubId to set
	 */
	public void setPubId(Integer pubId) {
		this.pubId = pubId;
	}
	/**
	 * @return the name
	 */
	public String getPubName() {
		return pubName;
	}
	/**
	 * @param name the name to set
	 */
	public void setPubName(String name) {
		this.pubName = name;
	}
	/**
	 * @return the phone
	 */
	public String getPubPhone() {
		return pubPhone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPubPhone(String phone) {
		this.pubPhone = phone;
	}
	/**
	 * @return the address
	 */
	public String getPubAddress() {
		return pubAddress;
	}
	/**
	 * @param address the address to set
	 */
	public void setPubAddress(String address) {
		this.pubAddress = address;
	}
	/**
	 *
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pubId == null) ? 0 : pubId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Publisher other = (Publisher) obj;
		if (pubId == null) {
			if (other.pubId != null)
				return false;
		} else if (!pubId.equals(other.pubId))
			return false;
		return true;
	}
	
}
