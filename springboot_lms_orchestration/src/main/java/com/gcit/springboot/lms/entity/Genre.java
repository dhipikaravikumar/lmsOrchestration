package com.gcit.springboot.lms.entity;

import java.io.Serializable;

public class Genre implements Serializable {
	
	private static final long serialVersionUID = -6558337775637298016L;
	
	private Integer genreId;
	private String genreName;
	/**
	 * @return the genreId
	 */
	public Integer getGenreId() {
		return genreId;
	}
	/**
	 * @param genreId the genreId to set
	 */
	public void setGenreId(Integer genreId) {
		this.genreId = genreId;
	}
	/**
	 * @return the genreName
	 */
	public String getGenreName() {
		return genreName;
	}
	/**
	 * @param genreName the genreName to set
	 */
	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((genreId == null) ? 0 : genreId.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genre other = (Genre) obj;
		if (genreId == null) {
			if (other.genreId != null)
				return false;
		} else if (!genreId.equals(other.genreId))
			return false;
		return true;
	}
	
}
