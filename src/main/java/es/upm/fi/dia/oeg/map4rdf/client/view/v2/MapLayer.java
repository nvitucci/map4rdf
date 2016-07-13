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
package es.upm.fi.dia.oeg.map4rdf.client.view.v2;



import org.gwtopenmaps.openlayers.client.Map;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;

import es.upm.fi.dia.oeg.map4rdf.client.style.StyleMapShape;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.share.Circle;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.WKTGeometry;

/**
 * @author Alexander De Leon
 */
public interface MapLayer {
    
	HasClickHandlers draw(Point point, DrawPointStyle pointStyle);

	HasClickHandlers drawPolygon(StyleMapShape<Polygon> polygon, DrawPointStyle pointStyle);

	HasClickHandlers drawPolyline(StyleMapShape<PolyLine> polyline, DrawPointStyle pointStyle);

	HasClickHandlers drawCircle(StyleMapShape<Circle> circle);

	HasClickHandlers drawCircle(StyleMapShape<Circle> circle, String text);
	
	HasClickHandlers drawWKTGeometry(StyleMapShape<WKTGeometry> createStyle, DrawPointStyle drawStyle);
	
	void removePolylines();
	
	void removePointsStyle(DrawPointStyle pointStyle);
	
	PopupWindow createPopupWindow();

	void clear();
	
	MapView getMapView();

	Map getOLMap();
	
	void unselectFeatures();
	
	interface PopupWindow extends HasWidgets {

		void open(Point location);

		void close();
	}
	
}
