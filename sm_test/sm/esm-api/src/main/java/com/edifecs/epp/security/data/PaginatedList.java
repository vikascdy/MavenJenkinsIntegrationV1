package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.Collection;

public class PaginatedList<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Collection<T> resultList;
	private int total;

	public Collection<T> getResultList() {
		return resultList;
	}

	public void setResultList(Collection<T> resultList) {
		this.resultList = resultList;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public PaginatedList(Collection<T> resultList, int total) {
		super();
		this.resultList = resultList;
		this.total = total;
	}
}
