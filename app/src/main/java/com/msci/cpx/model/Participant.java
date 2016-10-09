package com.msci.cpx.model;

import java.io.Serializable;

public class Participant implements Serializable {
	private static final long serialVersionUID = 5327309673835955314L;
	private int id;
	private String nameId;
	private int score;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNameId() {
		return nameId;
	}
	public void setNameId(String nameId) {
		this.nameId = nameId;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
