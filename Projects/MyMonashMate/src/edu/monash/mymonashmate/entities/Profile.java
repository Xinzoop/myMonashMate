package edu.monash.mymonashmate.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable{
	
	public final static int ATTR_ID = 0;
	public final static int ATTR_SURNAME = 1;
	public final static int ATTR_FIRSTNAME= 2;
	public final static int ATTR_NICKNAME = 3;
	public final static int ATTR_COURSE = 4;
	public final static int ATTR_LATITUDE = 5;
	public final static int ATTR_LONGITUDE = 6;
	public final static int ATTR_NATIONALITY = 7;
	public final static int ATTR_NATIVLANG= 8;
	public final static int ATTR_SECONDLANG = 9;
	public final static int ATTR_SUBURB = 10;
	public final static int ATTR_FAVFOOD = 11;
	public final static int ATTR_FAVMOVIE = 12;
	public final static int ATTR_FAVPROGLANG = 13;
	public final static int ATTR_FAVUNIT = 14;
	public final static int ATTR_CURJOB = 15;
	public final static int ATTR_PREVJOB = 16;
	public final static int ATTR_UNIT = 17;
	
	private int id;
	private String surname;
	private String firstname;
	private String nickname;
	private Course course;
	private List<Unit> units = new ArrayList<Unit>();
	private double latitude = -37.876470;
	private double longitude = 145.044078;
	
	private String nationality;
	private String nativLang;
	private String secondLang;
	private String suburb;
	private String favFood;
	private String favMovie;
	private String favProgLang;
	private Unit favUnit;
	private String curJob;
	private String prevJob;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}
	public List<Unit> getUnits() {
		return units;
	}
	public void setUnits(List<Unit> units) {
		this.units = units;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getFavFood() {
		return favFood;
	}

	public void setFavFood(String favFood) {
		this.favFood = favFood;
	}

	public String getNativLang() {
		return nativLang;
	}

	public void setNativLang(String nativLang) {
		this.nativLang = nativLang;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getSecondLang() {
		return secondLang;
	}

	public void setSecondLang(String secondLang) {
		this.secondLang = secondLang;
	}

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getFavMovie() {
		return favMovie;
	}

	public void setFavMovie(String favMovie) {
		this.favMovie = favMovie;
	}

	public String getFavProgLang() {
		return favProgLang;
	}

	public void setFavProgLang(String favProgLang) {
		this.favProgLang = favProgLang;
	}

	public Unit getFavUnit() {
		return favUnit;
	}

	public void setFavUnit(Unit favUnit) {
		this.favUnit = favUnit;
	}

	public String getCurJob() {
		return curJob;
	}

	public void setCurJob(String curJob) {
		this.curJob = curJob;
	}

	public String getPrevJob() {
		return prevJob;
	}

	public void setPrevJob(String prevJob) {
		this.prevJob = prevJob;
	}	
}
