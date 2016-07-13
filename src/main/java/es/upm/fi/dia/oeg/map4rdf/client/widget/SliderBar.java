package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent; 
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SliderBar extends SimplePanel implements HasValueChangeHandlers<Double>,RequiresResize,ProvidesResize{
	private FocusPanel mainPanel;
	private FlowPanel flowPanel;
	private Image knob;
	private FlowPanel line;
	private boolean mouseDown;
	private int min;
	private int max;
	private double stepSize;
	private int numTicks;
	private int numLabels;
	private int horizontalSpacing = 12;
	private LabelFormatter labelFormatter;
	private Map<Integer,Double> positionsMap;
	private boolean isLoad;
	private int actualPosition;
	private int lastPosition;
	private int lastWidth;

	public interface LabelFormatter {

		String formatLabel(SliderBar slider, double value);

	}

	public SliderBar(int min, int max, LabelFormatter labelFormatter) {
		this.min = min;
		this.max = max;
		stepSize = 1.0;
		numTicks = (int) ((max - min) / stepSize);
		numLabels = (int) ((max - min) / stepSize);
		this.labelFormatter = labelFormatter;
		positionsMap=new HashMap<Integer, Double>();
		actualPosition=horizontalSpacing;
		lastPosition=horizontalSpacing;
		isLoad=false;
		mouseDown=false;
		lastWidth=0;
		this.setWidget(createUI());
		//initWidget(createUI());
	}

	private Widget createUI() {
		mainPanel = new FocusPanel();
		addMainStyle(mainPanel);
		mainPanel.setHeight("34pt");
		flowPanel = new FlowPanel();
		line = new FlowPanel();
		addLineStyle(line);
		flowPanel.setHeight("34pt");
		knob = new Image(GWT.getModuleBaseURL() + "slider.png");
		addKnobStyle(knob);
		mainPanel.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					mouseDown=true;
					int	x =(int) (event.getRelativeX(mainPanel.getElement())-knob.getOffsetWidth()/2);
					int nearX=searchNearPosition(x);
					if(actualPosition!=nearX){
						actualPosition=nearX;
						refreshPosition(nearX);
					}
				}
			}

		});
		mainPanel.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					mouseDown = false;
					if(lastPosition!=actualPosition){
						fireEvents();
					}
				}
			}
		});
		mainPanel.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (mouseDown && event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					int	x =(int) (event.getRelativeX(mainPanel.getElement())-knob.getOffsetWidth()/2);
					int nearX=searchNearPosition(x);
					if(actualPosition!=nearX){
						actualPosition=nearX;
						refreshPosition(nearX);
					}
				}
			}
		});
		mainPanel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				mouseDown=false;
				refreshPosition(lastPosition);
				actualPosition=lastPosition;
			}
		});
		mainPanel.add(flowPanel);
		return mainPanel;
	}

	public void setCurrentValue(double value, boolean fireEvent) {
		double distance=max-min;
		for(int i:positionsMap.keySet()){
			double temp=Math.abs(value-positionsMap.get(i));
			if(temp<=distance){
				actualPosition=i;
				distance=temp;
			}
		}
		move(knob,actualPosition);
		if(fireEvent){
			fireEvents();
		}
	}
	public double getCurrentValue(){
		return positionsMap.get(actualPosition);
	}
	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
		if(isLoad){
			initComponents();
		}
	}

	public double getStepSize() {
		return stepSize;
	}

	public void setNumTicks(int numTicks) {
		this.numTicks = numTicks;
		if(isLoad){
			initComponents();
		}
	}

	public int getNumTicks() {
		return numTicks;
	}

	public void setNumLabels(int numLabels) {
		this.numLabels = numLabels;
		if(isLoad){
			initComponents();
		}
	}

	public int getNumLabels() {
		return numLabels;
	}

	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Double> valueChangeHandler) {
		return this.addHandler(valueChangeHandler, ValueChangeEvent.getType());
	}

	@Override
	public void onLoad() {
		super.onLoad();
		isLoad=true;
		initComponents();
	}
	@Override
	public void onResize(){
		if(lastWidth!=mainPanel.getOffsetWidth()){
			initComponents();
			Widget child = getWidget();
			if ((child != null) && (child instanceof RequiresResize)) {
				((RequiresResize) child).onResize();
			}
		}
	}
	@Override
	public void setWidth(String width){
		super.setWidth(width);
		onResize();
	}
	@Override
	public void setSize(String width,String height){
		super.setSize(width, height);
		onResize();
	}
	@Override
	public void setPixelSize(int width,int height){
		super.setPixelSize(width, height);
		onResize();
	}
	private void fireEvents() {
		lastPosition=actualPosition;
		ValueChangeEvent.fire(this,positionsMap.get(actualPosition));
	}
	private int searchNearPosition(int x){
		int minDistance=mainPanel.getOffsetWidth()-horizontalSpacing/2;
		int toReturn=min;
		for(int px:positionsMap.keySet()){
			if(Math.abs(x-px)<minDistance){
				minDistance=Math.abs(x-px);
				toReturn=px;
			}
		}
		return toReturn;
	}
	private void addMainStyle(Widget widget) {
		widget.getElement().getStyle().setBackgroundColor("white");
		widget.getElement().getStyle().setCursor(Cursor.AUTO);
	}

	private void addLabelStyle(Widget widget) {
		widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
		widget.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		widget.getElement().getStyle().setTop(2, Unit.PT);
		widget.getElement().getStyle().setFontSize(8, Unit.PT);
		widget.getElement().getStyle().setCursor(Cursor.DEFAULT);
		widget.getElement().setAttribute("draggable", "false");
	}

	private void addLineStyle(Widget widget) {
		widget.getElement().getStyle().setProperty("border", "1px solid black");
		widget.getElement().getStyle().setBackgroundColor("white");
		widget.getElement().getStyle().setHeight(4, Unit.PX);
		widget.getElement().getStyle().setTop(22, Unit.PT);
		widget.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
		widget.getElement().getStyle().setLeft(horizontalSpacing, Unit.PX);
		widget.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		widget.getElement().getStyle().setOpacity(1.0);
		widget.getElement().setAttribute("draggable", "false");
	}

	private void addKnobStyle(Widget widget) {
		widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
		widget.getElement().getStyle().setTop(14, Unit.PT);
		widget.getElement().setAttribute("border", "0");
		widget.getElement().getStyle().setLeft(horizontalSpacing-5, Unit.PX);
		widget.getElement().getStyle().setCursor(Cursor.POINTER);
		widget.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		widget.getElement().getStyle().setOpacity(1.0);
		widget.getElement().setAttribute("draggable", "false");
	}

	private void addTickStyle(Widget widget) {
		widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
		widget.getElement().getStyle().setTop(16, Unit.PT);
		widget.getElement().getStyle().setWidth(1, Unit.PX);
		widget.getElement().getStyle().setHeight(6, Unit.PT);
		widget.getElement().getStyle().setProperty("background", "none repeat scroll 0% 0% black");
		widget.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		widget.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		widget.getElement().getStyle().setOpacity(1.0);
		widget.getElement().setAttribute("draggable", "false");
	}

	private void move(Widget widget, int pixelMove) {
		widget.getElement().getStyle().setLeft(pixelMove, Unit.PX);
	}

	private void refreshPosition(int x) {
		if (x > (mainPanel.getOffsetWidth() - horizontalSpacing - knob
				.getOffsetWidth() / 2)) {
			x = mainPanel.getOffsetWidth() - horizontalSpacing
					- knob.getOffsetWidth() / 2;
		}
		if (x < horizontalSpacing - knob.getOffsetWidth() / 2) {
			x = horizontalSpacing - knob.getOffsetWidth() / 2;
		}
		move(knob, x);
	}

	private void initComponents() {
		lastWidth=mainPanel.getOffsetWidth();
		flowPanel.clear();
		// Init line
		line.setWidth((mainPanel.getOffsetWidth() - (horizontalSpacing * 2) - 2)
				+ "px");
		flowPanel.add(line);
		// Init ticks
		double separationTicks = mainPanel.getOffsetWidth() - horizontalSpacing
				* 2;
		separationTicks = separationTicks / (numTicks - 1);
		double x = horizontalSpacing;
		for (int i = 0; i < numTicks; i++) {
			FlowPanel tick = new FlowPanel();
			addTickStyle(tick);
			flowPanel.add(tick);
			int move;
			if (i == numTicks - 1) {
				 move=mainPanel.getOffsetWidth()
						- horizontalSpacing - 1;
			} else {
				move=(int) Math.round(x);
			}
			move(tick,move);
			x += separationTicks;
		}
		// Init labels
		double separationLabels = mainPanel.getOffsetWidth()
				- horizontalSpacing * 2;
		separationLabels = separationLabels / (numLabels - 1);
		double xLabel = horizontalSpacing;
		for (int i = 0; i < numLabels; i++) {
			Label label = new Label(labelFormatter.formatLabel(this, (((double)(max-min)/(double)(numLabels-1))*i)+min));
			addLabelStyle(label);
			flowPanel.add(label);
			int move;
			if (i == numTicks - 1) {
				move= mainPanel.getOffsetWidth()
						- horizontalSpacing - label.getOffsetWidth()+3;
			} else if (i == 0) {
				move= (int) Math.round(xLabel)-4;
			} else {
				move=(int) Math.round(xLabel - label.getOffsetWidth() / 2);
			}
			move(label,move);
			xLabel += separationLabels;
		}
		// Init knob
		flowPanel.add(knob);
		move(knob, actualPosition);
		// Init steps
		positionsMap.clear();
		double numStepsDouble=(max-min)/stepSize;
		int numStepsInt=(int)Math.round(numStepsDouble);
		double separationSteps=mainPanel.getOffsetWidth() - horizontalSpacing* 2;
		separationSteps = separationSteps/ numStepsInt;
		if(numStepsInt!=numStepsDouble){
			positionsMap.put(mainPanel.getOffsetWidth()
					- horizontalSpacing - 1 - knob.getOffsetWidth()/2,(double)max);
		}
		int stepPX=horizontalSpacing-knob.getOffsetWidth()/2;
		for(double i=min;i<=max;i=i+stepSize){
			positionsMap.put(stepPX,i);
			stepPX=(int)Math.round((double)(stepPX)+separationSteps);
		}
	}
}
