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

import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetFacetDefinitions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetFacetDefinitionsResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.ConfigurationException;
import es.upm.fi.dia.oeg.map4rdf.server.conf.FacetedBrowserConfiguration;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetGroup;

/**
 * @author Alexander De Leon
 */
public class GetFacetDefinitionsHandler implements ActionHandler<GetFacetDefinitions, GetFacetDefinitionsResult> {

	private final Map4rdfDao dao;
	private final FacetedBrowserConfiguration facetedBrowserConfiguration;
	private final boolean automaticFacets;
	private final String defaultProjection;
    
    
	@Inject
	public GetFacetDefinitionsHandler(Map4rdfDao dao, FacetedBrowserConfiguration facetedBrowserConfiguration,
			@Named(ParameterNames.FACETS_AUTO) boolean automaticFacets,
			@Named(ParameterNames.DEFAULT_PROJECTION) String defaultProjection) {
		this.dao = dao;
		this.facetedBrowserConfiguration = facetedBrowserConfiguration;
		this.automaticFacets = automaticFacets;
		this.defaultProjection = defaultProjection;
    }

	@Override
	public GetFacetDefinitionsResult execute(GetFacetDefinitions action, ExecutionContext context)
			throws ActionException {

		List<FacetGroup> groups;
		try {
			groups = facetedBrowserConfiguration.getFacetGroups();
		} catch (ConfigurationException e) {
			throw new ActionException(e);
		}
		if (automaticFacets) {
			for (FacetGroup group : groups) {
				try {
					
					if(action.getBoundingBox()!=null
						&& !action.getBoundingBox().getProjection().toLowerCase().trim().equals(defaultProjection.toLowerCase().trim())){
						throw new ActionException("Bounding box projection of GetFacesDefinitions (action) isn't equals to server projection");
					}
					for (Facet value : dao.getFacets(group.getUri(), action.getBoundingBox())) {
						group.addFacet(value);
					}
				} catch (DaoException e) {
					throw new ActionException(e);
				}
			}
		}

		return new GetFacetDefinitionsResult(groups);
	}

	@Override
	public Class<GetFacetDefinitions> getActionType() {
		return GetFacetDefinitions.class;
	}

	@Override
	public void rollback(GetFacetDefinitions action, GetFacetDefinitionsResult result, ExecutionContext context)
			throws ActionException {
		// nothing to do
	}
}
