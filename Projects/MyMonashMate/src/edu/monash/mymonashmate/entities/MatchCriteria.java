package edu.monash.mymonashmate.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchCriteria implements Serializable{
	
	public final static int COND_DISTANCE = 99;
	
	private List<Integer> sourceIDs = new ArrayList<Integer>();
		
	public List<Integer> getSourceIDs() {
		return sourceIDs;
	}

	public void setSourceIDs(List<Integer> sourceIDs) {
		this.sourceIDs = sourceIDs;
	}

	public List<MatchCriteriaItem> getCriteriaItems() {
		return criteriaItems;
	}

	public void setCriteriaItems(List<MatchCriteriaItem> criteriaItems) {
		this.criteriaItems = criteriaItems;
	}

	private List<MatchCriteriaItem> criteriaItems = new ArrayList<MatchCriteriaItem>();
}
