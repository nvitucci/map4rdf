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

import java.util.Collections;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Alexander De Leon
 */
public class GetGeoResourcesHandler implements
		ActionHandler<GetGeoResources, ListResult<GeoResource>> {

	private static final List<GeoResource> EMPTY_RESULT = Collections
			.emptyList();
	private MultipleConfigurations configurations;

	@Inject
	public GetGeoResourcesHandler(MultipleConfigurations configurations) {
		this.configurations = configurations;
	}

	@Override
	public ListResult<GeoResource> execute(GetGeoResources action,
			ExecutionContext context) throws ActionException {
		if(!configurations.existsConfiguration(action.getConfigID())){
			throw new ActionException("Bad Config ID");
		}
		List<GeoResource> resources;
		try {
			if (action.getFacetConstraints() == null) {
				resources = EMPTY_RESULT;
			} else {
				String defaultProjection= configurations.getConfiguration(action.getConfigID()).getConfigurationParamValue(ParameterNames.DEFAULT_PROJECTION);
				if(action.getBoundingBox()!=null &&
						!action.getBoundingBox().getProjection().toLowerCase().trim().equals(defaultProjection.toLowerCase().trim())){
						throw new ActionException("Bounding box projection of GetGeoResources (action) isn't equals to server projection");
				}
				//TODO Add Query Limit to config file.
				resources = configurations.getConfiguration(action.getConfigID())
						.getMap4rdfDao().getGeoResources(action.getBoundingBox(),
						action.getFacetConstraints(), 1000);
			}

		} catch (Exception e) {
			throw new ActionException("Data access error", e);
		}
		ListResult<GeoResource> result = new ListResult<GeoResource>(resources);
		return result;
	}

	@Override
	public Class<GetGeoResources> getActionType() {
		return GetGeoResources.class;
	}

	@Override
	public void rollback(GetGeoResources action,
			ListResult<GeoResource> result, ExecutionContext context)
			throws ActionException {
		// Nothing to to this is a get
	}

}
