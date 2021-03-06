/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informatica, Universidad 
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
package es.upm.fi.dia.oeg.map4rdf.client;


import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.controllers.AppController;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.Header;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;

/**
 * @author Alexander De Leon
 */
public class BrowserUi extends ResizeComposite implements AppController.Display {

	private final LayoutPanel appView;
	@Inject
	public BrowserUi(BrowserResources resources, BrowserMessages messages, ConfIDInterface configID, DispatchAsync dispatchAsync, EventBus eventBus, WidgetFactory widgetFactory) {
		appView = new LayoutPanel();
		initWidget(createUi(resources,messages,configID,dispatchAsync,eventBus,widgetFactory));
                
		StyleInjector.inject(resources.css().getText());
	}

	private Widget createUi(BrowserResources resources,BrowserMessages messages, ConfIDInterface configID, DispatchAsync dispatchAsync, EventBus eventBus, WidgetFactory widgetFactory) {
		DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
		panel.addNorth(new Header(resources, messages, configID, dispatchAsync, eventBus, widgetFactory), 88);
		//panel.addSouth(new es.upm.fi.dia.oeg.map4rdf.client.widget.Footer(resources), 25);
		panel.add(appView);

		return panel;
	}

	@Override
	public void setContent(Widget widget) {
        appView.clear();
        appView.add(widget);
		appView.setWidgetTopHeight(widget, 0, Unit.EM, 100, Unit.PCT);
		appView.forceLayout();
	}

	@Override
	public Widget asWidget() {
		return this;
	}

}
