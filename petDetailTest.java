package com.example.sandbox.getPet;

import static com.example.sandbox.util.constans.Tags.SMOKE;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.example.sandbox.Common;

import io.restassured.response.Response;
import utils.report.TestListener;

@Listeners(TestListener.class)
public class petDetailTest extends Common {

	@Test(enabled = true, groups = { SMOKE }, description = "description")
	public void Test1() {
		Map<String, String> queryParams = new TreeMap<>();
		queryParams.put("status", "available");

		Response response = getUrl(findByStatus, queryParams);
		Assert.assertEquals(response.getStatusCode(), 200, "Invalid response code");

		String id = response.jsonPath().get("find{it.status.equals('available')}.id").toString();

		Response response2 = getUrl(petById.replace("{petId}", id));
		Assert.assertEquals(response2.getStatusCode(), 200, "Invalid response code");

		// mandatory field: name
		Assert.assertNotNull(response2.path("name"), "Mandatory field 'name' should not be null");
		Assert.assertTrue(response2.path("name") instanceof String, "Mandatory field 'name' should be a String");

		// mandatory field: photoUrls
		Assert.assertNotNull(response2.path("photoUrls"), "Mandatory field 'photoUrls' should not be null");
		Assert.assertTrue(response2.path("photoUrls") instanceof List, "Mandatory field 'photoUrls' should be a List");

		// field: status
		if (response.path("status") != null) {
			String statusString = (String) response2.path("status");
			List<String> validStatuses = Arrays.asList("available", "pending", "sold");
			Assert.assertTrue(validStatuses.contains(statusString), "Unexpected status value: " + statusString);
		}
	}

	@Test(enabled = true, groups = { SMOKE }, description = "description")
	public void Test_Negative_petIdAsStringInsteadOfInt() {
		Response resp = getUrl(petById.replace("{petId}", "NotInt"));
		Assert.assertEquals(resp.getStatusCode(), 404, "Invalid response code");
		Assert.assertTrue(resp.getTime() < MAX_RESPONSE_TIME, "Response time is greater than expected");
	}
}
