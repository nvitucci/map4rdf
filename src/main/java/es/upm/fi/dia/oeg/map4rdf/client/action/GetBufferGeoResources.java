package es.upm.fi.dia.oeg.map4rdf.client.action;


import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

public class GetBufferGeoResources extends MultipleConfigurationAction implements Action<GetBufferGeoResourcesResult> {

	private static final long serialVersionUID = -6391683491297262424L;
	
	private String geoResourceUri;
	private double radiousKM;
	private TwoDimentionalCoordinate center;
	public TwoDimentionalCoordinate getCenter() {
		return center;
	}
	public void setCenter(TwoDimentionalCoordinate center) {
		this.center = center;
	}
	private GetBufferGeoResources(){
		super("");
		//For serialization
	}
	public GetBufferGeoResources(String configID, String geoResourceUri, TwoDimentionalCoordinate center, double radiousKM){
		super(configID);
		this.geoResourceUri=geoResourceUri;
		this.center=center;
		this.radiousKM=radiousKM;
	}
	public String getGeoResourceUri() {
		return geoResourceUri;
	}
	public void setGeoResourceUri(String geoResourceUri) {
		this.geoResourceUri = geoResourceUri;
	}
	public double getRadiousKM() {
		return radiousKM;
	}
	public void setRadiousKM(double radiousKM) {
		this.radiousKM = radiousKM;
	}


}
