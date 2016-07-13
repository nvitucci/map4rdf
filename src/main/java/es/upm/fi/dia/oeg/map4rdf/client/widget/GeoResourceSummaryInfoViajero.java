package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetViajeroResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.FilterDateChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FilterDateChangeEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.resource.ViajeroMessages;
import es.upm.fi.dia.oeg.map4rdf.client.util.DateFilter;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.viajero.TripProvenance;
import es.upm.fi.dia.oeg.map4rdf.share.viajero.ViajeroGuide;
import es.upm.fi.dia.oeg.map4rdf.share.viajero.ViajeroImage;
import es.upm.fi.dia.oeg.map4rdf.share.viajero.ViajeroResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.viajero.ViajeroTrip;

public class GeoResourceSummaryInfoViajero implements GeoResourceSummaryInfo,FilterDateChangeEventHandler{
	
	public interface Stylesheet {
		String viajeroLine0Style();
		String viajeroLine1Style();
		String viajeroLine0TripProvenanceLine0();
		String viajeroLine0TripProvenanceLine1();
		String viajeroLine1TripProvenanceLine0();
		String viajeroLine1TripProvenanceLine1();
		String viajeroCellPadding();
	}
	
	private final ConfIDInterface configID;
	private DialogBox mainWidget;
	private BrowserMessages browserMessages;
	private BrowserResources browserResources;
	private Panel mainPanel;
	private TabPanel mainTabPanel=new TabPanel();
	private DispatchAsync dispatchAsync;
	private ViajeroMessages viajeroMessages=GWT.create(ViajeroMessages.class);
	private FlowPanel guidesPanel=new FlowPanel();
	private FlowPanel tripsPanel=new FlowPanel();
	private Grid additionalInfoPanel=new Grid(0, 0);
	private List<ViajeroGuide> lastGuides;
	private List<DateFilter> dateFilters;
	private List<ViajeroTrip> lastTrips;
	public GeoResourceSummaryInfoViajero(ConfIDInterface configID,DispatchAsync dispatchAsync,EventBus eventBus,BrowserResources browserResources,BrowserMessages browserMessages){
		this.configID = configID;
		this.browserMessages=browserMessages;
		this.browserResources=browserResources;
		this.dispatchAsync=dispatchAsync;
		this.dateFilters=new ArrayList<DateFilter>();
		this.lastGuides=new ArrayList<ViajeroGuide>();
		this.lastTrips=new ArrayList<ViajeroTrip>();
		eventBus.addHandler(FilterDateChangeEvent.getType(), this);
		createUI();
	}
	
	private void createUI() {
		mainPanel=new FlowPanel();
		mainWidget=new DialogBox(false, false);
		mainWidget.setAnimationEnabled(true);
		mainWidget.setGlassEnabled(false);
		mainWidget.getElement().getStyle().setZIndex(10);
		Button close = new Button(browserMessages.close());
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				mainWidget.hide();
			}
		});
		VerticalPanel mainDialogPanel=new VerticalPanel();
		mainDialogPanel.add(mainPanel);
		mainDialogPanel.add(close);
		mainWidget.setWidget(mainDialogPanel);
		mainDialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(mainPanel, HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_CENTER);
		mainWidget.setText(browserMessages.informationTittle(""));	
	}

	@Override
	public void addAdditionalInfo(Map<String, String> additionalsInfo) {
		if(additionalsInfo!=null && !additionalsInfo.isEmpty()){
			if(mainTabPanel!=null){
				additionalInfoPanel=new Grid(additionalsInfo.size(),2);
				additionalInfoPanel.setCellSpacing(10);
				int row=0;
				for(String key:additionalsInfo.keySet()){
					additionalInfoPanel.setWidget(row, 0, new Label(key));
					additionalInfoPanel.setWidget(row, 1, new Label(additionalsInfo.get(key)));
					row++;
				}
				mainTabPanel.add(additionalInfoPanel,browserMessages.additionalInfo());
			}
		}
	}

	@Override
	public void clearAdditionalInfo() {
		if(mainTabPanel!=null && additionalInfoPanel!=null && mainTabPanel.getWidgetIndex(additionalInfoPanel)>=0){
			mainTabPanel.selectTab(0);
			mainTabPanel.remove(additionalInfoPanel);
		}
		if(additionalInfoPanel!=null){
			additionalInfoPanel.clear();
		}
	}

	@Override
	public Widget getWidget() {
		return mainPanel;
	}

	@Override
	public void setGeoResource(GeoResource resource, Geometry geometry) {
		mainPanel.clear();
		mainTabPanel.clear();
		lastGuides.clear();
		lastTrips.clear();
		mainPanel.add(new Label(browserMessages.loading()));
		dispatchAsync.execute(new GetViajeroResource(configID.getConfigID(),resource.getUri()), new AsyncCallback<SingletonResult<ViajeroResourceContainer>>() {

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.clear();
				mainPanel.add(new Label(browserMessages.errorCommunication()));
			}

			@Override
			public void onSuccess(SingletonResult<ViajeroResourceContainer> result) {
				mainPanel.clear();
				mainPanel.add(mainTabPanel);
				boolean wasShowing=mainWidget.isShowing();
				if(wasShowing){
					mainWidget.hide();
				}
				guidesPanel=new FlowPanel();
				tripsPanel=new FlowPanel();
				mainWidget.setText(browserMessages.informationTittle(""));
				lastGuides=result.getValue().getGuides();
				lastTrips=result.getValue().getTrips();
				if(result.getValue().haveGuides() || result.getValue().haveTrips()){
					if(result.getValue().haveTrips()){
						addTrips(applyFiltersTrip(result.getValue().getTrips()), tripsPanel);
						mainTabPanel.add(tripsPanel,viajeroMessages.trips());
						mainTabPanel.selectTab(mainTabPanel.getWidgetIndex(tripsPanel));
					}
					if(result.getValue().haveGuides()){
						addGuides(applyFiltersGuide(result.getValue().getGuides()), guidesPanel);
						mainTabPanel.add(guidesPanel, viajeroMessages.guides());
						mainTabPanel.selectTab(mainTabPanel.getWidgetIndex(guidesPanel));
					}
					mainPanel.add(mainTabPanel);
				}else{
					mainPanel.add(new Label(viajeroMessages.noGuidesAndTrips()));
					mainPanel.add(mainTabPanel);
				}
				if(wasShowing){
					mainWidget.center();
				}
			}
		});
	}

	@Override
	public boolean isVisible() {
		return mainWidget.isShowing();
	}

	@Override
	public void show() {
		mainWidget.center();
	}

	@Override
	public void close() {
		mainWidget.hide();
	}
	@Override
	public void onYearChange(FilterDateChangeEvent event) {
		dateFilters=event.getDateFilters();
		if(dateFilters==null){
			dateFilters=new ArrayList<DateFilter>();
		}
		addGuides(applyFiltersGuide(lastGuides), guidesPanel);
		addTrips(applyFiltersTrip(lastTrips), tripsPanel);
	}
	private List<ViajeroGuide> applyFiltersGuide(List<ViajeroGuide> toApply){
		if(toApply==null){
			return new ArrayList<ViajeroGuide>();
		}
		if(dateFilters.isEmpty() || toApply.isEmpty()){
			return toApply;
		}
		List<ViajeroGuide> dataFiltered=new ArrayList<ViajeroGuide>();
		for(ViajeroGuide guide:toApply){
			boolean passAllFilters=true;
			for(DateFilter filter:dateFilters){
				Date date=getDate(guide.getDate());
				if(date==null){break;}
				if(!filter.passFilter(date)){
					passAllFilters=false;
					break;
				}
			}
			if(passAllFilters){
				dataFiltered.add(guide);
			}
		}
		return dataFiltered;
	}
	private List<ViajeroTrip> applyFiltersTrip(List<ViajeroTrip> toApply){
		if(toApply==null){
			return new ArrayList<ViajeroTrip>();
		}
		if(dateFilters.isEmpty() || toApply.isEmpty()){
			return toApply;
		}
		List<ViajeroTrip> dataFiltered=new ArrayList<ViajeroTrip>();
		for(ViajeroTrip trip:toApply){
			boolean passAllFilters=true;
			for(DateFilter filter:dateFilters){
				Date date=getDate(trip.getDate());
				if(date==null){break;}
				if(!filter.passFilter(date)){
					passAllFilters=false;
					break;
				}
			}
			if(passAllFilters){
				dataFiltered.add(trip);
			}
		}
		return dataFiltered;
	}
	// TODO search and remove all incrusted styles.
	private void addGuides(List<ViajeroGuide> guides, Panel panel){
		panel.clear();
		if(guides.isEmpty()){
			if(!lastGuides.isEmpty()){
				panel.add(new Label(viajeroMessages.notGuidesForDateFilter()));
			}else{
				return;
			}
		}
		VerticalPanel verticalPanel=new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		ScrollPanel scroll = new ScrollPanel();
		int width=(int)(Window.getClientWidth()*0.5);
		int height=(int)(Window.getClientHeight()*0.5);
		scroll.setSize(width+"px", height+"px");
		FlexTable table=new FlexTable();
		table.setSize("100%", "auto");
		table.setCellPadding(5);
		table.setCellSpacing(1);
		scroll.setWidget(table);
		HorizontalPanel anchorsPanel=new HorizontalPanel();
		anchorsPanel.setSpacing(6);
		verticalPanel.add(scroll);
		verticalPanel.add(anchorsPanel);
		anchorsPanel.getElement().getParentElement().getStyle().setProperty("borderTop", "2px solid #004C99");
		anchorsPanel.getElement().getParentElement().getStyle().setProperty("borderBottom", "2px solid #004C99");
		panel.add(verticalPanel);
		int last=guides.size();
		if(guides.size()>10){last=10;}
		addSubSetGuides(guides.subList(0, last), table, height);
		int totalI=guides.size()/10;
		for(int i=0;i<=totalI;i++){
			Anchor anchor=new Anchor(String.valueOf(i+1));
			anchorsPanel.add(anchor);
			anchor.getElement().getStyle().setColor("");
			if(i==0){
				anchor.getElement().getStyle().setColor("#CC0000");
			}
			int endGuides=(i+1)*10;
			if(endGuides>guides.size()){
				endGuides=guides.size();
			}
			addChangeGuidesPager(anchor, i*10, endGuides,guides,table,height, anchorsPanel,scroll);
		}
		
	}
	private void addTrips(List<ViajeroTrip> trips, Panel panel){
		panel.clear();
		if(trips.isEmpty()){
			if(!dateFilters.isEmpty()){
				panel.add(new Label(viajeroMessages.notTripsForDateFilter()));
			}else{
				return;
			}
		}
		ScrollPanel scroll=new ScrollPanel();
		int height=(int)(Window.getClientHeight()*0.5);
		scroll.setHeight(height+"px");
		VerticalPanel vertical=new VerticalPanel();
		vertical.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		vertical.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		scroll.setWidget(vertical);
		int tripStyle=0;
		for(ViajeroTrip trip:trips){
			Widget widget=getTripWidget(trip,tripStyle);
			vertical.add(widget);
			tripStyle=(tripStyle+1)%2;
		}
		panel.add(scroll);
	}
	private Widget getTripWidget(ViajeroTrip trip,int style) {
		HorizontalPanel toReturn=new HorizontalPanel();
		toReturn.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		toReturn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		VerticalPanel vertical = new VerticalPanel();
		vertical.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		vertical.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		toReturn.add(vertical);
		HorizontalPanel mainLine= new HorizontalPanel();
		mainLine.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainLine.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainLine.setSpacing(7);
		mainLine.add(new Label(viajeroMessages.title()));
		mainLine.add(new Anchor(trip.getTitle(), trip.getURL(), "_blank"));
		Image rdfImage= new Image(browserResources.rdfIcon());
		Anchor rdfAnchor=new Anchor("",trip.getURI(),"_blank");
		rdfAnchor.getElement().appendChild(rdfImage.getElement());
		mainLine.add(rdfAnchor);
		Label timeLabel=new Label(getVisualDate(trip.getDate()));
		timeLabel.setWidth("75px");
		mainLine.add(timeLabel);
		vertical.add(mainLine);
		Widget moreInfoTrip=getMoreInfoTripWidget(trip);
		if(moreInfoTrip!=null){
			vertical.add(moreInfoTrip);
		}
		if(style==0){
			toReturn.addStyleName(browserResources.css().viajeroLine0Style());
		}
		if(style==1){
			toReturn.setStyleName(browserResources.css().viajeroLine1Style());
		}
		VerticalPanel provenances=new VerticalPanel();
		provenances.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		provenances.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		int pronvenanceStyle=0;
		for(TripProvenance tripProvenance:trip.getProvenances()){
			Widget tripProvenanceWidget= getTripProvenanceWidget(tripProvenance,(style*10)+pronvenanceStyle);
			if(tripProvenanceWidget!=null){
				provenances.add(tripProvenanceWidget);
				pronvenanceStyle=(pronvenanceStyle+1)%2;
			}
		}
		if(provenances.getWidgetCount()>0){
			VerticalPanel temp=new VerticalPanel();
			temp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			temp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			Label historic=new Label(viajeroMessages.historic());
			historic.addStyleName(browserResources.css().viajeroCellPadding());
			temp.add(historic);
			temp.add(provenances);
			toReturn.add(temp);
		}
		return toReturn;
	}
	private Widget getTripProvenanceWidget(TripProvenance tripProvenance,int style) {
		VerticalPanel toReturn= new VerticalPanel();
		HorizontalPanel horizontal= new HorizontalPanel();
		toReturn.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		toReturn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontal.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontal.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		toReturn.add(horizontal);
		if(tripProvenance.haveTitle()){
			if(tripProvenance.haveURL()){
				Anchor title=new Anchor(tripProvenance.getTitle());
				title.addStyleName(browserResources.css().viajeroCellPadding());
				title.setHref(tripProvenance.getUrl());
				title.setTarget("_blank");
				horizontal.add(title);
			}else{
				horizontal.add(new Label(tripProvenance.getTitle()));
			}
		}
		if(tripProvenance.haveBlog()){
			Anchor linkBlog= new Anchor("",tripProvenance.getBlog(),"_blank");
			linkBlog.addStyleName(browserResources.css().viajeroCellPadding());
			Image rdfBlog=new Image(browserResources.rdfIcon());
			linkBlog.getElement().appendChild(rdfBlog.getElement());
			horizontal.add(linkBlog);
		}
		if(tripProvenance.haveTime()){
			Label timeLabel=new Label(getVisualDate(tripProvenance.getTime()));
			timeLabel.addStyleName(browserResources.css().viajeroCellPadding());
			horizontal.add(timeLabel);
		}
		if(tripProvenance.haveSubTitle()){
			Label subTitleLabel=new Label(tripProvenance.getSubTitle());
			subTitleLabel.setWidth("350px");
			subTitleLabel.addStyleName(browserResources.css().viajeroCellPadding());
			toReturn.add(subTitleLabel);
		}
		if(style==00){
			toReturn.setStyleName(browserResources.css().viajeroLine0TripProvenanceLine0());
		}else if(style==01){
			toReturn.setStyleName(browserResources.css().viajeroLine0TripProvenanceLine1());
		}else if(style==10){
			toReturn.setStyleName(browserResources.css().viajeroLine1TripProvenanceLine0());
		}else if(style==11){
			toReturn.setStyleName(browserResources.css().viajeroLine1TripProvenanceLine1());
		}
		if(horizontal.getWidgetCount()>0){
			return toReturn;
		}else{
			return null;
		}
	}

	private Widget getMoreInfoTripWidget(ViajeroTrip trip){
		VerticalPanel vertical=new VerticalPanel();
		FlexTable table= new FlexTable();
		boolean addedAMoreInfo=false;
		int actualRow=0;
		if(trip.haveDistanceLess() || trip.haveDistanceMore()){
			int actualColumn=0;
			addedAMoreInfo=true;
			table.getRowFormatter().setVerticalAlign(actualRow, HasVerticalAlignment.ALIGN_MIDDLE);
			if(trip.haveDistanceLess()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(viajeroMessages.distanceLess()+" "+trip.getDistanceLess()));
				actualColumn++;
			}
			if(trip.haveDistanceMore()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(viajeroMessages.distanceMore()+" "+trip.getDistanceMore()));
			}
			actualRow++;
		}
		if(trip.haveDurationLess() || trip.haveDurationMore()){
			int actualColumn=0;
			addedAMoreInfo=true;
			table.getRowFormatter().setVerticalAlign(actualRow, HasVerticalAlignment.ALIGN_MIDDLE);
			if(trip.haveDurationLess()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(viajeroMessages.durationLess()+" "+trip.getDurationLess()));
				actualColumn++;
			}
			if(trip.haveDurationMore()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(viajeroMessages.durationMore()+" "+trip.getDurationMore()));
			}
			actualRow++;
		}
		if(trip.havePriceLess() || trip.havePriceMore()){
			int actualColumn=0;
			addedAMoreInfo=true;
			table.getRowFormatter().setVerticalAlign(actualRow, HasVerticalAlignment.ALIGN_MIDDLE);
			if(trip.havePriceLess()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(viajeroMessages.priceLess()+" "+trip.getPriceLess()));
				actualColumn++;
			}
			if(trip.havePriceMore()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(viajeroMessages.priceMore()+" "+trip.getPriceMore()));
			}
			actualRow++;
		}
		if(addedAMoreInfo){
			vertical.add(table);
		}
		if(trip.haveDescription()){
			Label description = new Label(viajeroMessages.description()+" "+trip.getDescription());
			vertical.add(description);
		}
		if(addedAMoreInfo || trip.haveDescription()){	
			return vertical;
		}else{
			return null;
		}
	}
	private void addSubSetGuides(List<ViajeroGuide> guides, FlexTable table, int height){
		table.clear();
		int row=0;
		for(ViajeroGuide guide:guides){
			table.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
			table.setWidget(row, 0, new Anchor(guide.getTitle(),guide.getURL(),"_blank"));
			table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
			table.setWidget(row, 1, new Label(getVisualDate(guide.getDate())));
			table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
			Widget widgetImages=getWidgetForImages(guide, height);
			if(widgetImages==null){
				Image imageWidget=new Image(browserResources.missingImage().getSafeUri());
				imageWidget.setSize("auto", "180px");
				table.setWidget(row,2,imageWidget);
				table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
			}else{
				table.setWidget(row,2,widgetImages);
				table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
			}
			row++;
		}
	}
	private void addChangeGuidesPager(final Anchor anchor,final int startGuide,final int finalGuide,final List<ViajeroGuide> guides,final FlexTable contentTable,final int height,final HorizontalPanel anchorsPanel, final ScrollPanel scroll){
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addSubSetGuides(guides.subList(startGuide, finalGuide), contentTable, height);
				scroll.scrollToTop();
				for(int i=0;i<anchorsPanel.getWidgetCount();i++){
					anchorsPanel.getWidget(i).getElement().getStyle().setColor("");
				}
				anchor.getElement().getStyle().setColor("#CC0000");	
			}
		});
	}
	private void changeImageWithAnchorClick(final Anchor anchor, final int height,final ViajeroImage image,final Panel contentPanel,final HorizontalPanel anchorsPanel){
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				int imageHeight=(int)(height*0.2);
				contentPanel.clear();
				Anchor contentAnchor=new Anchor();
				contentAnchor.setHref(image.getPname());
				contentAnchor.setTarget("_blank");
				contentAnchor.setSize("auto", imageHeight+"px");
				Image imageWidget=new Image(image.getURL());
				imageWidget.setSize("auto", "180px");
				contentAnchor.getElement().appendChild(imageWidget.getElement());
				contentPanel.add(contentAnchor);
				for(int i=0;i<anchorsPanel.getWidgetCount();i++){
					anchorsPanel.getWidget(i).getElement().getStyle().setColor("");
				}
				anchor.getElement().getStyle().setColor("#CC0000");
			}
		});
	}
	private Widget getWidgetForImages(ViajeroGuide guide, int height){
		VerticalPanel toReturn=new VerticalPanel();
		toReturn.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		toReturn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Panel imagePanel=new FlowPanel();
		HorizontalPanel anchorsPanel=new HorizontalPanel();
		anchorsPanel.setSpacing(6);
		toReturn.add(imagePanel);
		toReturn.add(anchorsPanel);
		boolean isImageAdded=false;
		int imageNumber=1;
		for(ViajeroImage image:guide.getImages()){
			Anchor anchor=new Anchor(String.valueOf(imageNumber));
			imageNumber++;
			changeImageWithAnchorClick(anchor, height, image, imagePanel, anchorsPanel);
			anchorsPanel.add(anchor);
			anchor.getElement().getStyle().setColor("");
			if(!isImageAdded){
				anchor.getElement().getStyle().setColor("#CC0000");
				int imageHeight=(int)(height*0.2);
				Anchor contentImage=new Anchor();
				contentImage.setHref(image.getPname());
				contentImage.setTarget("_blank");
				contentImage.setSize("auto", imageHeight+"px");
				Image imageWidget=new Image(image.getURL());
				imageWidget.setSize("auto", "180px");
				contentImage.getElement().appendChild(imageWidget.getElement());
				imagePanel.add(contentImage);
			}
			isImageAdded=true;
		}
		if(isImageAdded){
			return toReturn;
		}else{
			return null;
		}
	}
	private Date getDate(String viajeroDate){
		try{
			DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd");
			return dtf.parse(viajeroDate);
		} catch (Exception e){
			return null;
		}
	}
	private String getVisualDate(String viajeroDate){
		try{
			Date date=getDate(viajeroDate);
			DateTimeFormat newFormat = DateTimeFormat.getFormat("dd-MM-yyyy");
			return newFormat.format(date);
		}catch (Exception e){
			return viajeroDate;
		}
	}

}
