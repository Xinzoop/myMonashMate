package edu.monash.mymonashmate.entities;

import java.io.Serializable;

public class User implements Serializable{

	private String email;
	private String password;
	private String publKeyString;
	private int userid;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPublKeyString() {
		return publKeyString;
	}

	public void setPublKeyString(String publKeyString) {
		this.publKeyString = publKeyString;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

}
