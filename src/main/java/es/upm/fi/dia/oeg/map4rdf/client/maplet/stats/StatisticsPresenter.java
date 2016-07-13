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
package es.upm.fi.dia.oeg.map4rdf.client.maplet.stats;

import java.util.List;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourceOverlays;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticDatasets;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticYears;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.MapletEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.MyMapletEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.view.v2.MapLayer;
import es.upm.fi.dia.oeg.map4rdf.client.widget.MapShapeStyleFactory;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.CircleBean;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;

/**
 * @author Alexander De Leon
 */
public class StatisticsPresenter extends ControlPresenter<StatisticsPresenter.Display> implements AreaFilterChangedHandler {

	public interface Display extends WidgetDisplay {

		void setMapLayer(MapLayer mapLayer);

		void showSelectionDialog(List<Resource> datasets);

		void addSelectionDialogHandler(SelectionHandler<StatisticDefinition> selectionHandler);

		void closeSelectionDialogIfOpened();

		void showTimeline(List<Year> years);

		void setTimelineIndex(int index);

		void setTimelineValueChangeHandler(ValueChangeHandler<Year> changeHandler);

		void hideTimeline();

		void clear();
		
		void refreshTimeline();

		void stopProcessing();

		void startProcessing();

	}
	
	private final ConfIDInterface configID;
	private final DispatchAsync dispatchAsync;
	private StatisticDefinition currentStatistic;
	private final MapLayer mapLayer;
	private WidgetFactory widgetFactory;
	private BrowserMessages messages;
	@Inject
	public StatisticsPresenter(ConfIDInterface configID, Display view, EventBus eventBus, MapPresenter mapPresenter, DispatchAsync dispatchAsync, WidgetFactory widgetFactory, BrowserMessages messages) {
		super(view, eventBus);
		this.configID=configID;
		this.dispatchAsync = dispatchAsync;
		this.widgetFactory = widgetFactory;
		this.messages = messages;
		mapLayer = mapPresenter.getDisplay().createLayer("statistics");
		view.setMapLayer(mapLayer);
		eventBus.addHandler(AreaFilterChangedEvent.getType() ,this);
	}

	@Override
	protected void onBind() {
		HandlerRegistration registraton = eventBus.addHandler(MapletEvent.getType(), new MyMapletEventHandler(
				StatisticsMaplet.getMapletId()) {

			@Override
			public void myMapletDeactivated(MapletEvent event) {
				getDisplay().closeSelectionDialogIfOpened();
				getDisplay().clear();
				getDisplay().hideTimeline();
			}

			@Override
			public void myMapletActivated(MapletEvent event) {
				dispatchAsync.execute(new GetStatisticDatasets(configID.getConfigID()), new AsyncCallback<ListResult<Resource>>() {

					@Override
					public void onSuccess(ListResult<Resource> result) {
						getDisplay().showSelectionDialog(result.asList());
					}

					@Override
					public void onFailure(Throwable caught) {
						widgetFactory.getDialogBox().showError(messages.errorLoadingStatisticsDatasets());

					}
				});
			}
		});
		registerHandler(registraton);
		getDisplay().addSelectionDialogHandler(new SelectionHandler<StatisticDefinition>() {
			@Override
			public void onSelection(SelectionEvent<StatisticDefinition> event) {
				StatisticDefinition stat = event.getSelectedItem();
				setStatistic(stat);
			}

		});

		refreshTimeline();
	}

	@Override
	protected void onUnbind() {
		// empty

	}

	/* --------------------------------- Helper methods -- */
	private void setStatistic(final StatisticDefinition statistic) {
		currentStatistic = statistic;
		
		refreshTimeline();
		GetStatisticYears action = new GetStatisticYears(configID.getConfigID(),statistic.getDataset());
		getDisplay().startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<Year>>() {
			@Override
			public void onFailure(Throwable caught) {
				
				getDisplay().stopProcessing();
			}

			public void onSuccess(ListResult<Year> result) {
				getDisplay().stopProcessing();
				List<Year> yearsList = result.asList();
				getDisplay().showTimeline(yearsList);
				getDisplay().setTimelineIndex(yearsList.size() - 1);
			};
		});
	}

	private void drawStatistics() {
		GetGeoResourceOverlays action = new GetGeoResourceOverlays(configID.getConfigID(),mapLayer.getMapView().getVisibleBox());
		action.setStatisticDefinition(currentStatistic);
		getDisplay().startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<GeoResourceOverlay>>() {
			@Override
			public void onFailure(Throwable caught) {
				
				getDisplay().stopProcessing();
			}

			public void onSuccess(ListResult<GeoResourceOverlay> result) {
				double max = 0;
				for (GeoResourceOverlay overlay : result) {
					double value = overlay.getValue();
					if (value > max) {
						max = value;
					}
				}
				for (GeoResourceOverlay overlay : result) {
					Point point = (Point) overlay.getEntity().getFirstGeometry();
					double radius = (100000 * overlay.getValue()) / max;
					final CircleBean circle = new CircleBean(point, radius);

					final String uri = overlay.getUri();
					mapLayer.drawCircle(MapShapeStyleFactory.createStyle(circle), Double.toString(overlay.getValue()))
							.addClickHandler(new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {
									Window.open(uri, "_blank", null);
								}
							});

				}
				getDisplay().stopProcessing();
			};
		});
	}
	private void refreshTimeline() {
		getDisplay().refreshTimeline();
		getDisplay().setTimelineValueChangeHandler(new ValueChangeHandler<Year>() {
			@Override
			public void onValueChange(ValueChangeEvent<Year> event) {
				currentStatistic.getDimensions().clear();
				currentStatistic.getDimensions().add(event.getValue().getUri());
				getDisplay().clear();
				drawStatistics();

			}
		});
	}

	@Override
	public void onAreaFilterChanged(
			AreaFilterChangedEvent areaFilterChangedEvent) {
		getDisplay().clear();
		drawStatistics();
	}


	@Override
	protected void onRevealDisplay() {
		
		
	}
}
