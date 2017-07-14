package com.swk.bean.common;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

	private static final long serialVersionUID = -8166664955312904800L;
	
	private List<T> record;
	
	private int pageNo;
	
	private int pageSize;
	
	private boolean hasNext;
	
	private boolean hasPrev;
	
	private int nextPageNo;
	
	private int prevPageNo;
	
	private int totalPage;
	
	private int total;
	
	public Page(){}
	
	public Page(List<T> record,int total,int pageNo,int pageSize){
		this.record = record;
		this.total = total;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.hasNext = total > pageNo * pageSize;
		this.hasPrev = this.pageNo > 1;
		this.nextPageNo = this.isHasNext() ? (this.pageNo+1):0;
		this.prevPageNo = this.isHasPrev() ? (this.pageNo-1):0;
		this.totalPage = total%pageSize == 0?total/pageSize:total/pageSize+1;
	}

	public List<T> getRecord() {
		return record;
	}

	public void setRecord(List<T> record) {
		this.record = record;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public boolean isHasPrev() {
		return hasPrev;
	}

	public void setHasPrev(boolean hasPrev) {
		this.hasPrev = hasPrev;
	}

	public int getNextPageNo() {
		return nextPageNo;
	}

	public void setNextPageNo(int nextPageNo) {
		this.nextPageNo = nextPageNo;
	}

	public int getPrevPageNo() {
		return prevPageNo;
	}

	public void setPrevPageNo(int prevPageNo) {
		this.prevPageNo = prevPageNo;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
