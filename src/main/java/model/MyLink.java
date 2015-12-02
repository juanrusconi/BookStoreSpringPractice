package model;

import java.net.URI;

public class MyLink {
	
	public static final String REL_SELF = "self";
	public static final String REL_COLLECTION = "coll";
	
	private String rel;
	private String href;
	
	public MyLink(){
	}
	
	public MyLink(String href, String rel) {
		super();
		this.rel = rel;
		this.href = href;
	}
	
	public MyLink(URI href, String rel) {
		super();
		this.rel = rel;
		this.href = href.toString();
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public String toString() {
		return "Link [rel=" + rel + ", href=" + href + "]";
	}
	
	
	
}
