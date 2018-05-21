package com.sample.SE;

public class PageInfo {
	private String title;
	private String link;
	private String content;
	public PageInfo(String title, String link, String content){
		this.title = title;
		this.link = link;
		this.content = content;
	}
	public void setTitle(String title){
		this.title = title;
	}
	public void setLink(String link){
		this.link = link;
	}
	public void setContent(String content){
		this.content = content;
	}
	public String getTitle(){
		return this.title;
	}
	public String getLink(){
		return this.link;
	}
	public String getContent(){
		return this.content;
	}
}
