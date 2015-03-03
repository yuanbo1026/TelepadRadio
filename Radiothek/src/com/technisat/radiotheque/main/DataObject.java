package com.technisat.radiotheque.main;

public class DataObject {

	private int color;
	private String name;
	private int bg_color;

	public DataObject(String name, int color, int bg_color) {
		this.color = color;
		this.name = name;
		this.bg_color = bg_color;
	}
	
	public DataObject(String name, int color) {
		this.color = color;
		this.name = name;
	}

	public int getBg_color() {
		return bg_color;
	}

	public void setBg_color(int bg_color) {
		this.bg_color = bg_color;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
