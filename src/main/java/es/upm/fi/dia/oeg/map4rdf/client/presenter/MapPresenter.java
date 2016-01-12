/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
 * 
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
package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gwtopenmaps.openlayers.client.event.VectorFeatureAddedListener;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourcesAsKmlUrl;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterClearEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterClearHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterModeChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterModeChangeHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.CloseMapMainPopupEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.CloseMapMainPopupHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetReloadEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetReloadHandler;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.client.view.v2.MapView;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

/**
 * @author Alexander De Leon
 */
@Singleton
public class MapPresenter extends ControlPresenter<MapPresenter.Display> implements FacetConstraintsChangedHandler, AreaFilterModeChangeHandler, AreaFilterClearHandler, CloseMapMainPopupHandler, FacetReloadHandler {

	private Set<FacetConstraint> facetConstraints;
	private final DispatchAsync dispatchAsync;
	private WidgetFactory widgetFactory;
	
	public interface Display extends WidgetDisplay, MapView {

		TwoDimentionalCoordinate getCurrentCenter();

		BoundingBox getVisibleBox();

		void setVisibleBox(BoundingBox boundingBox);
		
		void removePointsStyle(DrawPointStyle pointStyle);
		
		void removePolylines();
		
		void drawGeoResouces(List<GeoResource> resources,DrawPointStyle pointStyle);
		
		void clear();
		
		void setAreaFilterDrawing(Boolean value);
		
		void clearAreaFilterDrawing();
		
		Vector getFilterVector();
		
		HasClickHandlers getKmlButton();

		
	}

	@Inject
	public MapPresenter(Display display, EventBus eventBus, DispatchAsync dispatchAsync, WidgetFactory widgetFactory) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
		this.widgetFactory = widgetFactory;
		eventBus.addHandler(FacetReloadEvent.getType(), this);
		eventBus.addHandler(FacetConstraintsChangedEvent.getType(), this);
		eventBus.addHandler(AreaFilterModeChangeEvent.getType(), this);
		eventBus.addHandler(AreaFilterClearEvent.getType(), this);
		eventBus.addHandler(CloseMapMainPopupEvent.getType(), this);
	}

	/*public TwoDimentionalCoordinate getCurrentCenter() {
		return getDisplay().getCurrentCenter();
	}*/

	public BoundingBox getVisibleBox() {
		return getDisplay().getVisibleBox();
	}

	public void setVisibleBox(BoundingBox boundingBox) {
		getDisplay().setVisibleBox(boundingBox);
	}
	
	public void drawGeoResources(List<GeoResource> resources, DrawPointStyle pointStyle) {
		getDisplay().drawGeoResouces(resources, pointStyle);
	}
	
	public void drawGeoResources(List<GeoResource> resources) {
		getDisplay().drawGeoResouces(resources, new DrawPointStyle());
	}
	
	public void removePointsStyle(DrawPointStyle pointStyle){
		getDisplay().removePointsStyle(pointStyle);
	}

	public void clear() {
		//For remove only default(facets) points
		//This dont remove polylines of routes and other special points.
		getDisplay().removePointsStyle(null);
	}
	
	public void clearDrawing(){
		getDisplay().clearAreaFilterDrawing();
	}

	@Override
	public void onFacetConstraintsChanged(FacetConstraintsChangedEvent event) {
		facetConstraints = event.getConstraints();
	}

	@Override
	public void onFacetReload() {
		if(facetConstraints!=null){
			eventBus.fireEvent(new FacetConstraintsChangedEvent(facetConstraints));
		}else{
			eventBus.fireEvent(new FacetConstraintsChangedEvent(new HashSet<FacetConstraint>()));
		}
	}

	/* ----------- presenter callbacks -- */
	@Override 
	protected void onBind() {
		getDisplay().getKmlButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GetGeoResourcesAsKmlUrl action = new GetGeoResourcesAsKmlUrl(getVisibleBox());
				action.setFacetConstraints(facetConstraints);
				dispatchAsync.execute(action, new AsyncCallback<SingletonResult<String>>() {
					@Override
					public void onFailure(Throwable caught) {
						widgetFactory.getDialogBox().showError(caught.getMessage());
					}

					@Override
					public void onSuccess(SingletonResult<String> result) {
						Window.open(GWT.getModuleBaseURL() + result.getValue(), "kml", null);
					}
				});
			}
		});
		
		getDisplay().getFilterVector().addVectorFeatureAddedListener(new VectorFeatureAddedListener(){

			@Override
			public void onFeatureAdded(FeatureAddedEvent eventObject) {
				eventBus.fireEvent(new AreaFilterChangedEvent());
			}
			
		});
	}

	@Override
	protected void onUnbind() {
		

	}

	@Override
	public void onDrawingStart(AreaFilterModeChangeEvent drawingStartEvent) {
		getDisplay().setAreaFilterDrawing(drawingStartEvent.getDrawingMode());
	}

	@Override
	public void onAreaFilterClear(AreaFilterClearEvent areaFilterClearEvent) {
		if(getDisplay().getFilterVector() != null && getDisplay().getFilterVector().getFeatures() != null && getDisplay().getFilterVector().getFeatures().length > 0) {
			getDisplay().getFilterVector().destroyFeatures();
			eventBus.fireEvent(new AreaFilterChangedEvent());		
		}
	}

	@Override
	public void closeMapMainPopup() {
		
		getDisplay().getDefaultLayer().getMapView().closeWindow();
	}

	@Override
	protected void onRevealDisplay() {
		
		
	}

}
