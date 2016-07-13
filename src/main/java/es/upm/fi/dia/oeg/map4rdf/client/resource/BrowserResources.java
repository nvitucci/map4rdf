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
package es.upm.fi.dia.oeg.map4rdf.client.resource;

import name.alexdeleon.lib.gwtblocks.client.widget.loading.LoadingWidget;
import name.alexdeleon.lib.gwtblocks.client.widget.togglebutton.ToggleButton;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import es.upm.fi.dia.oeg.map4rdf.client.view.FiltersView;
import es.upm.fi.dia.oeg.map4rdf.client.view.GeoprocessingView;
import es.upm.fi.dia.oeg.map4rdf.client.view.SelectConfigurationView;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DataToolBar;
import es.upm.fi.dia.oeg.map4rdf.client.widget.FacetWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.GeoResourceSummaryInfoDefault;
import es.upm.fi.dia.oeg.map4rdf.client.widget.GeoResourceSummaryInfoViajero;
import es.upm.fi.dia.oeg.map4rdf.client.widget.PopupGeoprocessingView;
import es.upm.fi.dia.oeg.map4rdf.client.widget.Timeline;


/**
 * @author Alexander De Leon
 */
public interface BrowserResources extends ClientBundle {

	interface BrowserCss extends LoadingWidget.Stylesheet, ToggleButton.Stylesheet, FacetWidget.Stylesheet,
			DataToolBar.Stylesheet, Timeline.Stylesheet, GeoResourceSummaryInfoDefault.Stylesheet, PopupGeoprocessingView.Stylesheet,
			GeoprocessingView.Stylesheet,FiltersView.Stylesheet,GeoResourceSummaryInfoViajero.Stylesheet,
			SelectConfigurationView.Stylesheet,es.upm.fi.dia.oeg.map4rdf.client.view.OpenLayersMapView.Stylesheet,
			CssResource{
		
		String header();

		String footer();

		String facets();

		String leftMenu();
		
		String treeRoot();
		
		String popup();
		
		String mainPopup();

	}

	@Source("style.css")
	BrowserCss css();

	@Source("kml.png")
	ImageResource kmlButton();
	
	@Source("geojson.png")
	ImageResource geoJSONButton();

	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("top_bg.gif")
	ImageResource topBackground();

	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("facet_bg.png")
	ImageResource facetBackground();

	@Source("ajax-loader.gif")
	ImageResource loadingIcon();

	@Source("pencil.png")
	ImageResource pencilIcon();
	
	@Source("erase.png")
	ImageResource eraserIcon();
	@Source("beta-trans.png")
	ImageResource betaBadge();
	
	@Source("plus.png")
	ImageResource plusIcon();
	
	@Source("stats.png")
	ImageResource statsButton();

	@Source("back.png")
	ImageResource backButton();
	
	@Source("save.png")
	ImageResource saveButton();
	
	@Source("close.png")
	ImageResource closeButton();
	
	@Source("transparent_image.png")
	ImageResource transparentImage();
	
	@Source("bg_summary.png")
	ImageResource summaryBackGround();
	
	@Source("close_icon.png")
	ImageResource closeIcon();
	
	@Source("info_icon.png")
	ImageResource infoIcon();
	
	@Source("routes_icon.png")
	ImageResource routesIcon();
	
	@Source("buffer_icon.png")
	ImageResource bufferIcon();
	
	@Source("pruebas_icon.png")
	ImageResource pruebasIcon();
	
	@Source("twitter_icon.png")
	ImageResource twitterIcon();
	
	@Source("wikipedia_icon.png")
	ImageResource wikipediaIcon();
	
	@Source("rdf_icon.png")
	ImageResource rdfIcon();
	
	@Source("edit_icon.png")
	ImageResource editIcon();
	
	@Source("statsSummary.png")
	ImageResource statsSummaryIcon();
	
	@Source("chartPIE.png")
	ImageResource chartPieIcon();
	
	@Source("chartLINE.png")
	ImageResource chartLineIcon();
	
	@Source("chartBAR.png")
	ImageResource chartBarIcon();
	
	@Source("refresh.png")
	ImageResource refreshImage();
	
	@Source("doneDialog.png")
	ImageResource doneImage();
	
	@Source("errorDialog.png")
	ImageResource errorImage();
	
	@Source("image_missing.png")
	DataResource missingImage();
}
