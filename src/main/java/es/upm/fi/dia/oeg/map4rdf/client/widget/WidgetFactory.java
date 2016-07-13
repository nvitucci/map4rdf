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
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import name.alexdeleon.lib.gwtblocks.client.widget.loading.LoadingWidget;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Alexander De Leon
 */
public class WidgetFactory {
	
	private final ConfIDInterface configID;
	private final BrowserMessages messages;
	private final BrowserResources resources;
	private final EventBus eventBus;
	private final DispatchAsync dispatchAsync;
	private static LoadingWidget loadingWidget;

	@Inject
	public WidgetFactory(ConfIDInterface configID,EventBus eventBus,BrowserMessages messages, BrowserResources resources, DispatchAsync dispatchAsync) {
		this.configID = configID;
		this.messages = messages;
		this.resources = resources;
		this.eventBus = eventBus;
		this.dispatchAsync=dispatchAsync;
	}

	public GeoResourceSummary createGeoResourceSummary() {
		return new GeoResourceSummary(configID,dispatchAsync,eventBus, messages, resources,this);
	}

	public Timeline createTimeline() {
		return new Timeline(resources.css());
	}
	public LoadingWidget getLoadingWidget() {
		if (loadingWidget == null) {
			loadingWidget = new LoadingWidget(resources.loadingIcon(), messages.loading(), resources.css());
		}
		return loadingWidget;
	}
	
	public Map4RDFMessageDialogBox getDialogBox(){
		Map4RDFMessageDialogBox dialogBox=new Map4RDFMessageDialogBox("Map4RDF", "",resources.doneImage(),resources.errorImage());
		return dialogBox;
	}
}
