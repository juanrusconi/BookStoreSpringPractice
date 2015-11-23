package model;

public class MyLink {
	
	private String rel;
	private String href;
	
	public MyLink(){
	}
	
	public MyLink(String rel, String href) {
		super();
		this.rel = rel;
		this.href = href;
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
