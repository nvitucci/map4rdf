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
package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourcesAsFormattedFileUrl;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.util.ConfigurationUtil;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;

/**
 * @author Alexander De Leon
 */

public class GetGeoResourcesAsFormattedFileUrlHandler implements ActionHandler<GetGeoResourcesAsFormattedFileUrl, SingletonResult<String>> {

	@Override
	public Class<GetGeoResourcesAsFormattedFileUrl> getActionType() {
		return GetGeoResourcesAsFormattedFileUrl.class;
	}

	@Override
	public SingletonResult<String> execute(GetGeoResourcesAsFormattedFileUrl action, ExecutionContext context)
			throws ActionException {
		StringBuilder queryBuilder = new StringBuilder();
		if (action.getFacetConstraints() != null) {
			try {
				for (FacetConstraint constratint : action.getFacetConstraints()) {
					queryBuilder.append(URLEncoder.encode(constratint.getFacetId(), "utf-8"));
					queryBuilder.append("=");
					queryBuilder.append(URLEncoder.encode(constratint.getFacetValueId(), "utf-8"));
					queryBuilder.append("&");
				}
				queryBuilder.deleteCharAt(queryBuilder.length() - 1);
				queryBuilder.append("&");
				queryBuilder.append(ConfigurationUtil.CONFIGURATION_ID);
				queryBuilder.append("=");
				queryBuilder.append(action.getConfigID());
			} catch (UnsupportedEncodingException e) {
				assert false : "utf-8 exists";
			}
		}else{
			queryBuilder.append(ConfigurationUtil.CONFIGURATION_ID);
			queryBuilder.append("=");
			queryBuilder.append(action.getConfigID());
		}
		switch (action.getServiceType()) {
		case GEOJSON:
			return new SingletonResult<String>("geojson?" + queryBuilder.toString());
		case KML:
			return new SingletonResult<String>("kml?" + queryBuilder.toString());
		default:
			return new SingletonResult<String>("kml?" + queryBuilder.toString());
		}
	}

	@Override
	public void rollback(GetGeoResourcesAsFormattedFileUrl action, SingletonResult<String> result, ExecutionContext context)
			throws ActionException {
		// nothing to do
	}

}
