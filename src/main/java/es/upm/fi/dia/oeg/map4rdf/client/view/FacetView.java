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
package es.upm.fi.dia.oeg.map4rdf.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.FacetPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.widget.FacetWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigurationDrawColoursBy;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetGroup;

/**
 * @author Alexander De Leon
 */
public class FacetView extends Composite implements FacetPresenter.Display {

	private FlowPanel panel;
	private final BrowserResources resources;
	private FacetSelectionHandler handler;
	private ConfigurationDrawColoursBy drawColoursBy=ConfigurationDrawColoursBy.getDefault();
	private FacetWidget facet;
	@Inject
	public FacetView(BrowserResources resources) {
		this.resources = resources;
		initWidget(createUi());
		addStyleName(resources.css().facets());
	}

	@Override
	public void setFacets(List<FacetGroup> facets) {
		for (final FacetGroup facetDefinition : facets) {
			facet = new FacetWidget(resources.css(),drawColoursBy);
			facet.setHeight(new Integer((100/facets.size())-3).toString()+"%");
			facet.setLabel(LocaleUtil.getBestLabel(facetDefinition));
			for (Facet facetValue : facetDefinition.getFacets()) {
				String label = LocaleUtil.getBestLabel(facetValue);
				facet.addFacetSelectionOption(facetValue.getUri(), label);
			}
			facet.sort();

			facet.addFacetValueSelectionChangedHandler(new FacetValueSelectionChangedHandler() {
				@Override
				public void onSelectionChanged(FacetValueSelectionChangedEvent event){
					if (handler != null) {
						handler.onFacetSelectionChanged(facetDefinition.getUri(),event.getHexColour(), event.getSelectionOptionId(),
									event.getSelectionValue());							
						
					}
				}
			});

			panel.add(facet);
		}

	}
	@Override
	protected void onLoad(){

	}
	@Override
	public void setFacetSelectionChangedHandler(FacetSelectionHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setConfigurationDrawColours(ConfigurationDrawColoursBy drawColoursBy) {
		if(this.facet!=null){
			this.facet.setConfigurationDrawColours(drawColoursBy);
		}
		this.drawColoursBy = drawColoursBy;
	}

	/* ------------- Display API -- */
	@Override
	public Widget asWidget() {
		return this;
	}


	/* ---------------- helper methods -- */
	private Widget createUi() {
		panel = new FlowPanel();
		return panel;
	}

    @Override
    public void clear() {
        //panel.clear();
    }

	@Override
	public Boolean isEmpty() {
		
		return facet==null;
	}
}
