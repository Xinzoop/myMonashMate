package edu.monash.mymonashmate.entities;

import java.io.Serializable;

public class MatchResult implements Serializable{

	private Profile mate;
	private int degree;
	private double distance;
	
	public Profile getMate() {
		return mate;
	}
	public void setMate(Profile mate) {
		this.mate = mate;
	}
	public int getDegree() {
		return degree;
	}
	public void setDegree(int degree) {
		this.degree = degree;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
}
