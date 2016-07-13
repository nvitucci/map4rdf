package es.upm.fi.dia.oeg.map4rdf.share.viajero;

import es.upm.fi.dia.oeg.map4rdf.share.Resource;

public class TripProvenance extends Resource{
	
	private static final long serialVersionUID = 3225853336138295431L;
	
	public enum TripProvenanceType {
		POST, IMAGE, VIDEO, GUIDE
	};

	TripProvenanceType type;
	String title;
	String subTitle;
	String time;
	String url;
	String blog;
	
	private TripProvenance(){
		super("");
		//For serialize
	}
	public TripProvenance(String uri, TripProvenanceType type) {
		super(uri);
		this.type=type;
	}
	public boolean haveTitle(){
		return title!=null && !title.isEmpty();
	}
	public boolean haveSubTitle(){
		return subTitle!=null && !subTitle.isEmpty();
	}
	public boolean haveTime(){
		return time!=null && !time.isEmpty();
	}
	public boolean haveURL(){
		return url!=null && !url.isEmpty();
	}
	public boolean haveBlog(){
		return blog!=null && !blog.isEmpty();
	}
	public TripProvenanceType getProvenanceType() {
		return type;
	}
	public void setProvenanceType(TripProvenanceType type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBlog() {
		return blog;
	}
	public void setBlog(String blog) {
		this.blog = blog;
	}
}
