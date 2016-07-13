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
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.HasFacetValueSelectionChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigurationDrawColoursBy;

/**
 * @author Alexander De Leon
 */
public class FacetWidget extends ResizeComposite implements HasFacetValueSelectionChangedHandler {

	/**
	 * Stylesheet contract
	 */
	public static interface Stylesheet {
		String facet();

		String facetHeader();

		String facetSelectionOption();
	}

	private Label label;
	private LayoutPanel panel;
	private ScrollPanel scrollPanel;
	private FlowPanel selectionsPanel;
	private final Map<String, CheckBox> selectionOptions;
	private Stylesheet stylesheet;
	private static int[] freeHexColour;
	private Map<String,Integer> relationFacetIDHexColour;
	private ConfigurationDrawColoursBy drawColoursBy = ConfigurationDrawColoursBy.getDefault();
	
	public FacetWidget(ConfigurationDrawColoursBy drawColoursBy) {
		this.drawColoursBy = drawColoursBy;
		selectionOptions = new HashMap<String, CheckBox>();
		if(freeHexColour==null){
			freeHexColour= new int[DrawPointStyle.getHexColours().length];
			for(int i=0;i<freeHexColour.length;i++){
				freeHexColour[i]=0;
			}
		}
		relationFacetIDHexColour= new HashMap<String, Integer>();
		initWidget(createUi());
	}

	public FacetWidget(Stylesheet stylesheet,ConfigurationDrawColoursBy drawColoursBy1) {
		this(drawColoursBy1);
		setStylesheet(stylesheet);
	}
	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void addFacetSelectionOption(final String id, String label) {
		CheckBox checkBox = new CheckBox(label);
		checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				fireSelectionChanged(id, event.getValue());
			}
		});
		if (stylesheet != null) {
			checkBox.addStyleName(stylesheet.facetSelectionOption());
		}
		selectionOptions.put(id, checkBox);
		selectionsPanel.add(checkBox);
	}

	public void sort() {
		selectionsPanel.clear();
		for (CheckBox cb : getSortedSelections()) {
			selectionsPanel.add(cb);
		}
	}

	public void setStylesheet(Stylesheet stylesheet) {
		this.stylesheet = stylesheet;
		applyStylesheet(stylesheet);
	}

	public void applyStylesheet(Stylesheet stylesheet) {
		addStyleName(stylesheet.facet());
		label.addStyleName(stylesheet.facetHeader());
		for (Map.Entry<String, CheckBox> selectionOption : selectionOptions.entrySet()) {
			selectionOption.getValue().addStyleDependentName(stylesheet.facetSelectionOption());
		}
	}

	@Override
	public void addFacetValueSelectionChangedHandler(FacetValueSelectionChangedHandler handler) {
		addHandler(handler, FacetValueSelectionChangedEvent.getType());
	}

	private Widget createUi() {
		panel = new LayoutPanel();
		label = new Label();

		panel.add(label);
				
		selectionsPanel = new FlowPanel();
		scrollPanel = new ScrollPanel();
		scrollPanel.setWidget(selectionsPanel);
		panel.add(scrollPanel);

		panel.forceLayout();

		return panel;
	}
	
	@Override
	public void setHeight(String height) {
		super.setHeight(height);
		this.getElement().getStyle().setProperty("minHeight",50*selectionOptions.size() ,Unit.PX);
		scrollPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		scrollPanel.getElement().getStyle().setTop(22, Unit.PX);
	}
	
	public void setHeight(String height, int numberOfElements) {
		super.setHeight(height);
		int minHeight = 30+15*numberOfElements;
		if(minHeight>150){
			minHeight = 150;
		}
		this.getElement().getStyle().setProperty("minHeight",minHeight ,Unit.PX);
		scrollPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		scrollPanel.getElement().getStyle().setTop(22, Unit.PX);
	}
	private void fireSelectionChanged(String id, Boolean value) {
		if(value && drawColoursBy==ConfigurationDrawColoursBy.FACET){
			relationFacetIDHexColour.put(id, getFirtsFreeColour());
			selectionOptions.get(id).getElement().getStyle().setProperty("background", DrawPointStyle.getHexColours()[relationFacetIDHexColour.get(id)]);
		}else{
			if(relationFacetIDHexColour.containsKey(id)){
				removeHexColour(relationFacetIDHexColour.get(id));
			}
			selectionOptions.get(id).getElement().getStyle().setProperty("background", "");
		}
		if(relationFacetIDHexColour.get(id)!=null && drawColoursBy==ConfigurationDrawColoursBy.FACET){
			fireEvent(new FacetValueSelectionChangedEvent(DrawPointStyle.getHexColours()[relationFacetIDHexColour.get(id)],id, value));
		}else{
			fireEvent(new FacetValueSelectionChangedEvent("", id, value));
		}
		if(!value){
			relationFacetIDHexColour.remove(id);
		}
	}

	private List<CheckBox> getSortedSelections() {
		List<CheckBox> sortedList = new ArrayList<CheckBox>(selectionOptions.values());
		Collections.sort(sortedList, new Comparator<CheckBox>() {
			@Override
			public int compare(CheckBox o1, CheckBox o2) {
				return o1.getText().compareTo(o2.getText());
			}
		});
		return sortedList;
	}
	private synchronized int getFirtsFreeColour(){
		int menor=Integer.MAX_VALUE;
		int firtsFreeColour=0;
		for(int i=0;i<freeHexColour.length;i++){
			if(freeHexColour[i]<menor){
				firtsFreeColour=i;
				menor=freeHexColour[i];
			}
		}
		freeHexColour[firtsFreeColour]++;
		return firtsFreeColour;	
	}
	private synchronized void removeHexColour(int positionHexColour){
		if(positionHexColour>=0 && positionHexColour<freeHexColour.length){
			freeHexColour[positionHexColour]--;
			if(freeHexColour[positionHexColour]<0){
				freeHexColour[positionHexColour]=0;
			}
		}
	}

	public void setConfigurationDrawColours(
			ConfigurationDrawColoursBy drawColoursBy2) {
		this.drawColoursBy=drawColoursBy2;
		
	}
}
