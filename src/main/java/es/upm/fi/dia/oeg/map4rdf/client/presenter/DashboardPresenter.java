/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Inform‡tica, Universidad 
 * PolitŽcnica de Madrid, Spain
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import name.alexdeleon.lib.gwtblocks.client.PagePresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParameters;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParametersResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticDatasets;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.DashboardDoSelectedResultWidgetEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.DashboardDoSelectedResultWidgetHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceCloseEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceCloseEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfigurationHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.StatisticsSummaryEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.StatisticsSummaryEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoUtils;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.util.WidgetsNames;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DataToolBar;
import es.upm.fi.dia.oeg.map4rdf.client.widget.EditResourceWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.PopupStatisticsView;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigurationDrawColoursBy;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.MultiPolygonBean;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.PolygonBean;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Alexander De Leon
 */
@Singleton
public class DashboardPresenter extends
		PagePresenter<DashboardPresenter.Display> implements
		FacetConstraintsChangedHandler, LoadResourceEventHandler,
		AreaFilterChangedHandler, EditResourceEventHandler,
		EditResourceCloseEventHandler, DashboardDoSelectedResultWidgetHandler,
		StatisticsSummaryEventHandler {

	public interface Display extends WidgetDisplay {
		Panel getMapPanel();

		void addWestWidget(Widget widget, String header);

		void clear();

		void setMainPopup(Integer width, Integer height, Widget widget,
				String style);

		void closeMainPopup();

		void doSelectedWestWidget(Widget widget);
	}

	private final ConfIDInterface configID;
	private final ResultsPresenter resultsPresenter;
	private final MapPresenter mapPresenter;
	private final FacetPresenter facetPresenter;
	private final FiltersPresenter filtersPresenter;
	private final GeoprocessingPresenter geoprocessingPresenter;
	private final DispatchAsync dispatchAsync;
	private final DataToolBar dataToolBar;
	private final BrowserMessages messages;
	private final BrowserResources resources;
	private final WidgetFactory widgetFactory;
	private Widget resultWidget;
	private List<GeoResource> listGeoResource;
	private ConfigurationDrawColoursBy drawColoursBy = ConfigurationDrawColoursBy
			.getDefault();
	private String statisticsURL = "";
	private String serverProjection = "";

	@Inject
	public DashboardPresenter(ConfIDInterface configID, Display display,
			EventBus eventBus, FacetPresenter facetPresenter,
			MapPresenter mapPresenter, FiltersPresenter filtersPresenter,
			ResultsPresenter resultsPresenter, DispatchAsync dispatchAsync,
			DataToolBar dataToolBar, WidgetFactory widgetFactory,
			BrowserMessages messages, BrowserResources resources,
			GeoprocessingPresenter geoprocessingPresenter) {
		super(display, eventBus);
		this.configID = configID;
		this.messages = messages;
		this.resources = resources;
		this.mapPresenter = mapPresenter;
		this.facetPresenter = facetPresenter;
		this.resultsPresenter = resultsPresenter;
		this.geoprocessingPresenter = geoprocessingPresenter;
		this.dispatchAsync = dispatchAsync;
		this.dataToolBar = dataToolBar;
		this.filtersPresenter = filtersPresenter;
		this.widgetFactory = widgetFactory;

		// add references
		this.geoprocessingPresenter.getDisplay().setDashboardPresenter(this);

		// add controls
		addControl(mapPresenter);
		addControl(facetPresenter);

		// registered for app-level events
		eventBus.addHandler(FacetConstraintsChangedEvent.getType(), this);
		eventBus.addHandler(LoadResourceEvent.getType(), this);
		eventBus.addHandler(AreaFilterChangedEvent.getType(), this);
		eventBus.addHandler(EditResourceEvent.getType(), this);
		eventBus.addHandler(EditResourceCloseEvent.getType(), this);
		eventBus.addHandler(DashboardDoSelectedResultWidgetEvent.getType(),
				this);
		eventBus.addHandler(StatisticsSummaryEvent.getType(), this);

		// initialize variables
		listGeoResource = new ArrayList<GeoResource>();

		// init google charts
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	@Override
	public void onFacetConstraintsChanged(FacetConstraintsChangedEvent event) {
		mapPresenter.clear();
		resultsPresenter.clear();
		loadResources(mapPresenter.getVisibleBox(), event.getConstraints());
	}

	@Override
	public void onLoadResource(LoadResourceEvent event) {
		mapPresenter.clear();
		resultsPresenter.clear();
		loadResource(event.getResourceUri());
	}

	/* -------------- Presenter callbacks -- */

	public List<GeoResource> getListGeoResource() {
		return listGeoResource;
	}

	@Override
	public void onAreaFilterChanged(
			AreaFilterChangedEvent areaFilterChangedEvent) {
		FacetConstraintsChangedEvent event = new FacetConstraintsChangedEvent(
				facetPresenter.getConstraints());
		eventBus.fireEvent(event);

	}

	@Override
	public void onEditResource(EditResourceEvent editResourceEvent) {
		int height = mapPresenter.getDisplay().asWidget().getOffsetHeight();
		int width = mapPresenter.getDisplay().asWidget().getOffsetWidth();
		es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter.Display d = mapPresenter
				.getDisplay();
		getDisplay().setMainPopup(
				width,
				height,
				new EditResourceWidget(configID, editResourceEvent.getUrl(),
						dispatchAsync, d, resources, messages, eventBus,
						widgetFactory), "");
	}

	@Override
	public void onEditResourceClose(
			EditResourceCloseEvent editResourceCloseEvent) {
		getDisplay().closeMainPopup();
	}

	@Override
	public void onStatisticsSummary(
			StatisticsSummaryEvent statisticsSummaryEvent) {
		if (statisticsSummaryEvent.isOpen()) {
			int height = mapPresenter.getDisplay().asWidget().getOffsetHeight();
			int width = mapPresenter.getDisplay().asWidget().getOffsetWidth();
			getDisplay().setMainPopup(
					width,
					height,
					new PopupStatisticsView(statisticsSummaryEvent
							.getGeoResource(), statisticsURL, width, height,
							eventBus, messages, resources, widgetFactory)
							.asWidget(), "Big");
		} else {
			getDisplay().closeMainPopup();
		}

	}

	@Override
	protected void onBind() {
		// attach children
		if (configID.existsConfigID()) {
			initAsync();
		} else {
			eventBus.addHandler(OnSelectedConfiguration.getType(),
					new OnSelectedConfigurationHandler() {

						@Override
						public void onSelectecConfiguration(String configID) {
							initAsync();
						}
					});
		}
	}

	private void initAsync() {
		GetStatisticDatasets action = new GetStatisticDatasets(
				configID.getConfigID());
		dispatchAsync.execute(action,
				new AsyncCallback<ListResult<Resource>>() {

					@Override
					public void onFailure(Throwable caught) {
						initWidgets(null);
					}

					@Override
					public void onSuccess(ListResult<Resource> result) {
						initWidgets(result);
					}
				});
		/*
		 * Timer timer = new Timer() {
		 * 
		 * @Override public void run() { List<GeoResource> resources= new
		 * ArrayList<GeoResource>(); es.upm.fi.dia.oeg.map4rdf.share.Point
		 * punto=new PointBean("", 678017.24, 4613322.29,"EPSG:23030");
		 * /*es.upm.fi.dia.oeg.map4rdf.share.Point punto2=new PointBean("",
		 * -3.645757, 40.30,"EPSG:4326"); es.upm.fi.dia.oeg.map4rdf.share.Point
		 * punto3=new PointBean("", -3.50, 40.465757,"EPSG:4326");
		 * es.upm.fi.dia.oeg.map4rdf.share.Point punto4=new PointBean("", -3.1,
		 * 39.465757,"EPSG:4326"); es.upm.fi.dia.oeg.map4rdf.share.Point
		 * punto5=new PointBean("", -3, 39.465757,"EPSG:4326");
		 * 
		 * es.upm.fi.dia.oeg.map4rdf.share.Polygon polyGeometry= new
		 * PolygonBean("www.poligono1.com",new Point[]{punto,punto2,punto3});
		 * es.upm.fi.dia.oeg.map4rdf.share.Polygon polyGeometry2= new
		 * PolygonBean("www.poligono1.com",new Point[]{punto3,punto4,punto5});
		 * es.upm.fi.dia.oeg.map4rdf.share.Geometry multiPolyGeometry = new
		 * MultiPolygonBean("www.multipoligono.com", new
		 * Polygon[]{polyGeometry,polyGeometry2});-/ resources.add(new
		 * GeoResource(
		 * "http://datos.localidata.com/recurso/Provincia/Madrid/Municipio/madrid/LocalComercial/11109169L80"
		 * ,punto)); //resources.add(new GeoResource("www.multipoligono.com",
		 * multiPolyGeometry)); mapPresenter.drawGeoResources(resources, new
		 * DrawPointStyle()); this.cancel(); } }; timer.schedule(8000);
		 */
	}

	private void initWidgets(final ListResult<Resource> result) {
		List<String> parameters = new ArrayList<String>();
		parameters.add(ParameterNames.STATISTICS_SERVICE_URL);
		parameters.add(ParameterNames.SUMMARY_WIDGETS);
		parameters.add(ParameterNames.DEFAULT_PROJECTION);
		parameters.add(ParameterNames.DRAW_COLOURS_BY);
		// Initialize asyn variables
		dispatchAsync.execute(
				new GetMultipleConfigurationParameters(configID.getConfigID(),
						parameters),
				new AsyncCallback<GetMultipleConfigurationParametersResult>() {

					@Override
					public void onFailure(Throwable caught) {
						widgetFactory
								.getDialogBox()
								.showError(
										messages.moduleCantContactWithServer("DASHBOARD"));
						getDisplay().addWestWidget(
								facetPresenter.getDisplay().asWidget(),
								messages.facets());
						if (result != null && result.asList().size() > 0) {
							getDisplay().addWestWidget(dataToolBar,
									messages.overlays());
						}
						getDisplay().addWestWidget(
								filtersPresenter.getDisplay().asWidget(),
								messages.filtres());
						resultWidget = resultsPresenter.getDisplay().asWidget();
						getDisplay().addWestWidget(resultWidget,
								messages.results());
						getDisplay().getMapPanel().add(
								mapPresenter.getDisplay().asWidget());
					}

					@Override
					public void onSuccess(
							GetMultipleConfigurationParametersResult values) {
						String stat = values.getResults().get(
								ParameterNames.STATISTICS_SERVICE_URL);
						if (stat == null || stat.isEmpty()) {
							widgetFactory
									.getDialogBox()
									.showError(
											messages.configParameterNullOrEmpty(ParameterNames.STATISTICS_SERVICE_URL));
						} else {
							statisticsURL = stat;
						}
						String projection = values.getResults().get(
								ParameterNames.DEFAULT_PROJECTION);
						if (projection == null || projection.isEmpty()) {
							widgetFactory
									.getDialogBox()
									.showError(
											messages.configParameterNullOrEmpty(ParameterNames.DEFAULT_PROJECTION));
						} else {
							serverProjection = projection;
						}
						getDisplay().addWestWidget(
								facetPresenter.getDisplay().asWidget(),
								messages.facets());
						if (result != null && result.asList().size() > 0) {
							getDisplay().addWestWidget(dataToolBar,
									messages.overlays());
						}
						String widgets = values.getResults().get(
								ParameterNames.SUMMARY_WIDGETS);
						if (widgets != null && !widgets.isEmpty()) {
							if (widgets.contains(WidgetsNames.ROUTES)
									|| widgets.contains(WidgetsNames.BUFFER)) {
								getDisplay().addWestWidget(
										geoprocessingPresenter.getDisplay()
												.asWidget(),
										messages.geoprocessing());
							}
						}
						String drawColoursByString = values.getResults().get(
								ParameterNames.DRAW_COLOURS_BY);
						if (drawColoursByString != null
								&& !drawColoursByString.isEmpty()) {
							if (ConfigurationDrawColoursBy
									.isValid(drawColoursByString)) {
								drawColoursBy = ConfigurationDrawColoursBy
										.valueOf(drawColoursByString);
							}
						}
						getDisplay().addWestWidget(
								filtersPresenter.getDisplay().asWidget(),
								messages.filtres());
						resultWidget = resultsPresenter.getDisplay().asWidget();
						getDisplay().addWestWidget(resultWidget,
								messages.results());
						getDisplay().getMapPanel().add(
								mapPresenter.getDisplay().asWidget());
					}
				});

	}

	@Override
	protected void onUnbind() {
		// empty
	}

	@Override
	protected void onRefreshDisplay() {
	}

	@Override
	protected void onRevealDisplay() {
	}

	/* --------------- helper methods --- */
	void loadResources(BoundingBox boundingBox,
			final Set<FacetConstraint> constraints) {
		GetGeoResources action = new GetGeoResources(configID.getConfigID(),
				boundingBox);
		if (constraints != null) {
			action.setFacetConstraints(constraints);
		}
		mapPresenter.getDisplay().startProcessing();
		dispatchAsync.execute(action,
				new AsyncCallback<ListResult<GeoResource>>() {
					@Override
					public void onFailure(Throwable caught) {
						mapPresenter.getDisplay().stopProcessing();
					}

					@Override
					public void onSuccess(ListResult<GeoResource> result) {
						mapPresenter.removePointsStyle(new DrawPointStyle());
						listGeoResource = result.asList();
						switch (drawColoursBy) {
						case FACET:
							drawColoursByFacet(constraints);
							break;
						case LABEL:
							drawColoursByLabel();
							break;
						default:
							break;
						}
						resultsPresenter.setResults(result.asList());
						mapPresenter.getDisplay().stopProcessing();
					}
				});
		if (constraints.isEmpty()) {
			listGeoResource.clear();
		}
	}
	
	private void drawColoursByFacet(Set<FacetConstraint> constraints) {
		Map<String, List<GeoResource>> toDraw = new HashMap<String, List<GeoResource>>();
		for (GeoResource i : listGeoResource) {
			for (FacetConstraint j : constraints) {
				if (j.equals(i.getFacetConstraint())) {
					if (!toDraw.containsKey(j.getFacetId()
							+ j.getFacetValueId())) {
						toDraw.put(j.getFacetId() + j.getFacetValueId(),
								new ArrayList<GeoResource>());
					}
					toDraw.get(j.getFacetId() + j.getFacetValueId()).add(i);
					break;
				}
			}
		}
		for (FacetConstraint i : constraints) {
			if (toDraw.containsKey(i.getFacetId() + i.getFacetValueId())) {
				mapPresenter.drawGeoResources(
						toDraw.get(i.getFacetId() + i.getFacetValueId()),
						new DrawPointStyle(i.getHexColour()));
			}
		}
	}

	private void drawColoursByLabel(){
    	Map<String,List<GeoResource>> toDraw = new HashMap<String, List<GeoResource>>();
    	for(GeoResource resource:listGeoResource){
    		String bestLabel=LocaleUtil.getBestLabel(resource);
    		if(bestLabel!=null && !bestLabel.isEmpty() && !bestLabel.equals(resource.getUri())){
    			if(!toDraw.containsKey(bestLabel)){
					toDraw.put(bestLabel, new ArrayList<GeoResource>());
				}
				toDraw.get(bestLabel).add(resource);
    		}else{
    			if(!toDraw.containsKey("")){
    				toDraw.put("", new ArrayList<GeoResource>());
    			}
    			toDraw.get("").add(resource);
    		}
    	}
    	List<String> sortedListKey = new ArrayList<String>(toDraw.keySet());
		Collections.sort(sortedListKey);
    	int colorIndex=0;
		for(String key: sortedListKey){
			if(colorIndex>=DrawPointStyle.getHexColours().length){
				colorIndex=0;
			}
    		String nextHexColour = DrawPointStyle.getHexColours()[colorIndex++];
    		mapPresenter.drawGeoResources(toDraw.get(key),new DrawPointStyle(nextHexColour));
    	}
    }

	void loadResource(String uri) {
		GetGeoResource action = new GetGeoResource(configID.getConfigID(), uri);
		mapPresenter.getDisplay().startProcessing();
		dispatchAsync.execute(action,
				new AsyncCallback<SingletonResult<GeoResource>>() {

					@Override
					public void onFailure(Throwable caught) {

						mapPresenter.getDisplay().stopProcessing();
					}

					@Override
					public void onSuccess(SingletonResult<GeoResource> result) {
						if (result.getValue() != null) {
							mapPresenter.drawGeoResources(Collections
									.singletonList(result.getValue()));
							mapPresenter.setVisibleBox(GeoUtils
									.computeBoundingBoxFromGeometries(result
											.getValue().getGeometries(),
											serverProjection));
							mapPresenter.getDisplay().stopProcessing();
						} else {
							widgetFactory.getDialogBox().showError(
									messages.errorToLoadResourceInUrlParam());
						}
					}
				});
	}

	@Override
	public void doSelectedResultWidget() {

		getDisplay().doSelectedWestWidget(resultWidget);
	}

}