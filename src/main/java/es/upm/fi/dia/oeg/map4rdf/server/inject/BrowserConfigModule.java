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
package es.upm.fi.dia.oeg.map4rdf.server.inject;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import es.upm.fi.dia.oeg.map4rdf.server.conf.AddInfoConfigServer;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.FacetedBrowserConfiguration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.GetServletContext;
import es.upm.fi.dia.oeg.map4rdf.server.conf.MapsConfigurationServer;
import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterDefaults;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Alexander De Leon
 */
public class BrowserConfigModule extends AbstractModule {

	private final Configuration config;
	private final FacetedBrowserConfiguration facetedBrowserConfiguration;
	private final GetServletContext getServletContext;
	public BrowserConfigModule(Configuration config, FacetedBrowserConfiguration facetedBrowserConfiguration,GetServletContext getServletContext) {
		this.config = config;
		this.facetedBrowserConfiguration = facetedBrowserConfiguration;
		this.getServletContext=getServletContext;
	}

	@Override
	protected void configure() {
		bind(Configuration.class).toInstance(config);
		bind(GetServletContext.class).toInstance(getServletContext);
		bind(FacetedBrowserConfiguration.class).toInstance(facetedBrowserConfiguration);

		bindConstant().annotatedWith(Names.named(ParameterNames.ENDPOINT_URL)).to(
				config.getConfigurationParamValue(ParameterNames.ENDPOINT_URL));
		
		bindConstant().annotatedWith(Names.named(ParameterNames.ENDPOINT_URL_GEOSPARQL)).to(
				config.getConfigurationParamValue(ParameterNames.ENDPOINT_URL_GEOSPARQL));

		bindConstant().annotatedWith(Names.named(ParameterNames.GEOMETRY_MODEL)).to(
				Constants.GeometryModel.valueOf(config.getConfigurationParamValue(ParameterNames.GEOMETRY_MODEL)));
		
		bindConstant().annotatedWith(Names.named(ParameterNames.ROUTES_SERVICE_TIMEOUT_MILISECONDS)).to(
				config.getConfigurationParamValue(ParameterNames.ROUTES_SERVICE_TIMEOUT_MILISECONDS));

		if (config.getConfigurationParamValue(ParameterNames.FACETS_AUTO)!=null) {
			bindConstant().annotatedWith(Names.named(ParameterNames.FACETS_AUTO)).to(
					Boolean.parseBoolean(config.getConfigurationParamValue(ParameterNames.FACETS_AUTO)));
		} else {
			bindConstant().annotatedWith(Names.named(ParameterNames.FACETS_AUTO)).to(
					ParameterDefaults.FACETS_AUTO_DEFAULT);
		}
		if (config.getConfigurationParamValue(ParameterNames.DEFAULT_PROJECTION)!=null){
			bindConstant().annotatedWith(Names.named(ParameterNames.DEFAULT_PROJECTION)).to(
				config.getConfigurationParamValue(ParameterNames.DEFAULT_PROJECTION));
		}
		if(config.getConfigurationParamValue(ParameterNames.ADDITIONAL_INFO)!=null
				&& !config.getConfigurationParamValue(ParameterNames.ADDITIONAL_INFO).isEmpty()){
			bind(AddInfoConfigServer.class).toInstance(new AddInfoConfigServer(getServletContext, config.getConfigurationParamValue(ParameterNames.ADDITIONAL_INFO)));
		}else{
			bind(AddInfoConfigServer.class).toInstance(new AddInfoConfigServer());
		}
		bind(MapsConfigurationServer.class).toInstance(new MapsConfigurationServer(getServletContext, config.getConfigurationParamValue(ParameterNames.SPHERICAL_MERCATOR)));
	}
}
