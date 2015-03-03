package com.technisat.radiotheque.Drawer;

public class NavDrawerItem {
     
    private String title;
    private int icon;
    private int id;
    private boolean isVisable;
    private int pressed_icon;
    
     
    public int getPressed_icon() {
		return pressed_icon;
	}

	public void setPressed_icon(int pressed_icon) {
		this.pressed_icon = pressed_icon;
	}

	public NavDrawerItem(){}
 
    public NavDrawerItem(String title, int icon, int id,boolean isVisible){
        this.title = title;
        this.icon = icon;
        this.id = id;
        this.isVisable = isVisible;
    }
    
    public NavDrawerItem(String title, int icon, int pressed_icon, int id,boolean isVisible){
        this.title = title;
        this.icon = icon;
        this.pressed_icon = pressed_icon;
        this.id = id;
        this.isVisable = isVisible;
    }
    
    public boolean isVisible(){
    	return isVisable;
    }
    
    public void setVisiblity(boolean isVisible){
    	this.isVisable = isVisible;
    }
    
    public int getId(){
    	return this.id;
    }
     
    public String getTitle(){
        return this.title;
    }
     
    public int getIcon(){
        return this.icon;
    }
     
    public void setTitle(String title){
        this.title = title;
    }
     
    public void setIcon(int icon){
        this.icon = icon;
    }
}