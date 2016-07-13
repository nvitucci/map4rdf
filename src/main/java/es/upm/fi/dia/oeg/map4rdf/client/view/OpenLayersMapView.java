/**
 * Copyright (c) 2011 Alexander De Leon Battista
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtopenmaps.openlayers.client.LonLat;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.client.util.FeatureClickEvent;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoUtils;
import es.upm.fi.dia.oeg.map4rdf.client.view.v2.MapLayer;
import es.upm.fi.dia.oeg.map4rdf.client.widget.GeoResourceSummary;
import es.upm.fi.dia.oeg.map4rdf.client.widget.MapShapeStyleFactory;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MultiPolygon;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.WKTGeometry;

/**
 * @author Alexander De Leon
 */
public class OpenLayersMapView extends es.upm.fi.dia.oeg.map4rdf.client.view.v2.OpenLayersMapView implements
		MapPresenter.Display{

	private final Image kmlButton;
	private final Image geoJSONButton;
	private final GeoResourceSummary summary;
	private final MapLayer.PopupWindow window;
	private Map<String,List<Point>> points;
	
	public interface Stylesheet {
		String kmlButtonStyle();
		String geoJSONButtonStyle();
	}
	
	@Inject
	public OpenLayersMapView(ConfIDInterface configID,WidgetFactory widgetFactory, DispatchAsync dispatchAsync,EventBus eventBus,BrowserResources browserResources, BrowserMessages browserMessages) {
		super(configID,widgetFactory, dispatchAsync,eventBus,browserResources,browserMessages);
		kmlButton = createKMLButton(browserResources);
		geoJSONButton = createGeoJSONButton(browserResources);
		summary = widgetFactory.createGeoResourceSummary();
		window = getDefaultLayer().createPopupWindow();
		window.add(summary);
		points=new HashMap<String, List<Point>>();
		super.panel.add(kmlButton);
		super.panel.add(geoJSONButton);
	}

	@Override
	public void drawGeoResouces(List<GeoResource> resources,DrawPointStyle pointStyle) {
		for (GeoResource resource : resources) {
			drawGeoResource(resource,pointStyle);
		}
	}

	@Override
	public void clear() {
		points.clear();
		getDefaultLayer().clear();
		window.close();
	}

	@Override
	public HasClickHandlers getKmlButton() {
		return kmlButton;
	}

	

	@Override
	public HasClickHandlers getGeoJSONButton() {
		return geoJSONButton;
	}
	
	
	@Override
	public void closeWindow() {
		removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
		window.close();
		summary.closeSummary();
		getDefaultLayer().unselectFeatures();
	}

	@Override
	public void removePolylines() {
		
		getDefaultLayer().removePolylines();
	}

	@Override
	public void removePointsStyle(DrawPointStyle pointStyle) {
		
		getDefaultLayer().removePointsStyle(pointStyle);
	}

	/* --------------- helper methods -- */
	
	private void drawGeoResource(final GeoResource resource, DrawPointStyle drawStyle) {
		for (Geometry geometry : resource.getGeometries()) {
			switch (geometry.getType()) {
			case POINT:
				final Point point = (Point) geometry;
				if(points.get(resource.getUri())==null){
					points.put(resource.getUri(), new ArrayList<Point>());
				}
				points.get(resource.getUri()).add(point);
				getDefaultLayer().draw(point,drawStyle).addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						window.close();
						summary.setGeoResource(resource, point);
						removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
						drawGeoResource(resource, new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
						window.open(point);
					}
				});
				break;
			case POLYLINE:
				final PolyLine line = (PolyLine) geometry;
				getDefaultLayer().drawPolyline(MapShapeStyleFactory.createStyle(line,drawStyle),drawStyle).addClickHandler(
						new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								window.close();
								removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
								Point pToOpen = getPointOfMousePosition(resource,line,event);
								GeoResource newResource = new GeoResource(resource.getUri(), pToOpen);
								drawGeoResource(newResource, new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
								summary.setGeoResource(resource, line);
								window.open(pToOpen);
							}
						});
				break;
			case POLYGON:
				final Polygon polygon = (Polygon) geometry;
				getDefaultLayer().drawPolygon(MapShapeStyleFactory.createStyle(polygon,drawStyle),drawStyle).addClickHandler(
						new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								window.close();
								removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
								Point pToOpen = getPointOfMousePosition(resource, polygon ,event);
								GeoResource newResource = new GeoResource(resource.getUri(), pToOpen);
								drawGeoResource(newResource, new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
								summary.setGeoResource(resource, polygon);
								window.open(pToOpen);
							}
						});
				break;
			case CIRCLE:
				//TODO implement CIRCLE draw
				break;
			case MULTIPOLYGON:
				MultiPolygon multiPolygon = (MultiPolygon) geometry;
				for(final Polygon poly: multiPolygon.getPolygons()){
					getDefaultLayer().drawPolygon(MapShapeStyleFactory.createStyle(poly,drawStyle),drawStyle).addClickHandler(
							new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {
									window.close();
									removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
									Point pToOpen = getPointOfMousePosition(resource, poly,event);
									GeoResource newResource = new GeoResource(resource.getUri(), pToOpen);
									drawGeoResource(newResource, new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
									summary.setGeoResource(resource, poly);
									window.open(pToOpen);
								}
							});
				}
				break;
			case WKTGEOMETRY:
				final WKTGeometry wktGeometry = (WKTGeometry) geometry;
				getDefaultLayer().drawWKTGeometry(MapShapeStyleFactory.createStyle(wktGeometry,drawStyle),drawStyle).addClickHandler(
						new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								window.close();
								removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
								Point pToOpen = getPointOfMousePosition(resource,wktGeometry,event);
								GeoResource newResource = new GeoResource(resource.getUri(), pToOpen);
								drawGeoResource(newResource, new DrawPointStyle(DrawPointStyle.Style.SELECTED_RESOURCE));
								summary.setGeoResource(resource, wktGeometry);
								window.open(pToOpen);
							}
						});
				break;
			default:
				//Dont enter because all case are in switch
				break;
			}

		}

	}

	private Image createKMLButton(BrowserResources browserResources) {
		Image button = new Image(browserResources.kmlButton());
		button.setStyleName(browserResources.css().kmlButtonStyle());
		button.getElement().getStyle().setZIndex(2080);
		return button;
	}
	
	private Image createGeoJSONButton(BrowserResources browserResources) {
		Image button = new Image(browserResources.geoJSONButton());
		button.setStyleName(browserResources.css().geoJSONButtonStyle());
		button.getElement().getStyle().setZIndex(2080);
		return button;
	}
	
	private Point getPointOfMousePosition(GeoResource resource,Geometry geometry,ClickEvent event){
		Point pToOpen = null;
		if(event instanceof FeatureClickEvent){
			FeatureClickEvent featureEvent = (FeatureClickEvent)event;
			LonLat openLonLat = featureEvent.getClickedLonLat();
			pToOpen = new PointBean(resource.getUri(), openLonLat.lon(), openLonLat.lat(),getDefaultLayer().getOLMap().getProjection());
		}else{
			pToOpen = GeoUtils.getCentroid(geometry);
		}
		return pToOpen;
	}
}
