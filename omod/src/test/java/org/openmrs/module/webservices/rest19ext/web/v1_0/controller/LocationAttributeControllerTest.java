/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest19ext.web.v1_0.controller;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.LocationAttribute;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest19ext.test.Rest19ExtTestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link LocationAttributeController}.
 */
public class LocationAttributeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private LocationService service;
	
	private LocationAttributeController controller;
	
	private MockHttpServletRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() throws Exception {
		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
		this.service = Context.getLocationService();
		this.controller = new LocationAttributeController();
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void shouldGetALocationAttribute() throws Exception {
		Object result = controller.retrieve(Rest19ExtTestConstants.LOCATION_UUID,
		    Rest19ExtTestConstants.LOCATION_ATTRIBUTE_UUID, request);
		Assert.assertNotNull(result);
		Assert.assertEquals(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("", PropertyUtils.getProperty(result, "value"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldListAttributesForLocation() throws Exception {
		SimpleObject result = controller.getAll(Rest19ExtTestConstants.LOCATION_UUID, request, response);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, Util.getResultsSize(result));
	}
	
	@Test
	public void shouldAddAttributeToLocation() throws Exception {
		int before = service.getLocationByUuid(Rest19ExtTestConstants.LOCATION_UUID).getAttributes().size();
		String json = "{ \"attributeType\":\"b3b6d540-a32e-44c7-91b3-292d97667518\", \"value\":\"testing\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.create(Rest19ExtTestConstants.LOCATION_UUID, post, request, response);
		int after = service.getLocationByUuid(Rest19ExtTestConstants.LOCATION_UUID).getAttributes().size();
		Assert.assertEquals(before + 1, after);
	}
	
	@Test
	public void shouldEditLocationAttribute() throws Exception {
		String json = "{ \"attributeType\":\"9516cc50-6f9f-132r-5433-001e378eb67f\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		
		LocationAttribute locationAttribute = service
		        .getLocationAttributeByUuid(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID);
		Assert.assertEquals("Audit Date", locationAttribute.getAttributeType().getName());
		
		controller.update(Rest19ExtTestConstants.LOCATION_UUID, Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID, post,
		    request, response);
		
		locationAttribute = service.getLocationAttributeByUuid(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID);
		Assert.assertEquals("Care Date", locationAttribute.getAttributeType().getName());
	}
	
	@Test
	public void shouldVoidAttribute() throws Exception {
		LocationAttribute locationAttribute = service
		        .getLocationAttributeByUuid(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_UUID);
		Assert.assertFalse(locationAttribute.isVoided());
		controller.delete(Rest19ExtTestConstants.LOCATION_UUID, Rest19ExtTestConstants.LOCATION_ATTRIBUTE_UUID, "unit test",
		    request, response);
		locationAttribute = service.getLocationAttributeByUuid(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_UUID);
		Assert.assertTrue(locationAttribute.isVoided());
		Assert.assertEquals("unit test", locationAttribute.getVoidReason());
	}
	
	@Test
	@Ignore
	public void shouldPurgeAttribute() throws Exception {
		// TODO: TEST IGNORED AS PURGING LOCATIONATTRIBUTE IS NOT POSSIBLE		
	}
}
