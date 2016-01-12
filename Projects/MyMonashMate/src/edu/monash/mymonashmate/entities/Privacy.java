package edu.monash.mymonashmate.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Privacy implements Serializable{
	
	private int userid;
	private List<Integer> publAttrs = new ArrayList<Integer>();

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public List<Integer> getPublAttrs() {
		return publAttrs;
	}

	public void setPublAttrs(List<Integer> publAttrs) {
		this.publAttrs = publAttrs;
	}
	
}
