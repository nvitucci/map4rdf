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
package es.upm.fi.dia.oeg.map4rdf.client.maplet.stats;

import java.util.List;

import name.alexdeleon.lib.gwtblocks.client.widget.prettypopup.PrettyPopup;
import name.alexdeleon.lib.gwtblocks.client.widget.prettypopup.PrettyPopupStylesheetFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;

/**
 * @author Alexander De Leon
 */
public class StatisticsSelectionDialog extends PrettyPopup implements HasSelectionHandlers<StatisticDefinition> {

	private final ListBox datasetList = new ListBox();
	private final Button selectButton;
	private final BrowserMessages messages;

	@Inject
	public StatisticsSelectionDialog(BrowserMessages messages) {
		super(PrettyPopupStylesheetFactory.getDefaultStylesheet(), true);
		this.messages = messages;
		this.selectButton = new Button(messages.select());
		setModal(true);
		createUi();
	}

	public void show(List<Resource> datasets) {
		initDatasetList(datasets);
		center();
	}

	@Override
	public void hide() {
		datasetList.clear();
		super.hide();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<StatisticDefinition> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	protected String getSelectedDataset() {
		return datasetList.getValue(datasetList.getSelectedIndex());
	}

	protected void initDatasetList(List<Resource> datasets) {
		for (Resource dataset : datasets) {
			datasetList.addItem(LocaleUtil.getBestLabel(dataset), dataset.getUri());
		}
	}

	private void createUi() {

		getContentPanel().add(new HTML("<h2>" + messages.statistics() + ":</h2>"));
		getContentPanel().add(datasetList);
		getContentPanel().add(new HTML("<br />"));
		selectButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StatisticDefinition statistic = new StatisticDefinition(getSelectedDataset());
				SelectionEvent.fire(StatisticsSelectionDialog.this, statistic);
				hide();
			}
		});
		selectButton.setText(messages.select());
		getContentPanel().add(selectButton);

	}

}
