package edu.monash.mymonashmate.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchCriteriaItem implements Serializable{
	private int attrID;
	
	private List<String> attrValueList = new ArrayList<String>();

	public int getAttrID() {
		return attrID;
	}

	public void setAttrID(int attrID) {
		this.attrID = attrID;
	}

	public List<String> getAttrValueList() {
		return attrValueList;
	}

	public void setAttrValueList(List<String> attrValueList) {
		this.attrValueList = attrValueList;
	}
	
}
