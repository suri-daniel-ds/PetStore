package com.example.sandbox.businessProcesses;

import static com.example.sandbox.util.Tools.generateRandomNumber;
import static com.example.sandbox.util.body.pet.JsonBody.createJsonBody;
import static com.example.sandbox.util.body.pet.JsonBody.updateJsonBody;
import static com.example.sandbox.util.constans.Tags.SMOKE;
import static com.example.sandbox.util.constans.TestData.HYDRAIMAGE;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.example.sandbox.Common;
import com.example.sandbox.util.body.pet.PostCreatePet;
import com.example.sandbox.util.body.pet.PutUpdatePet;
import com.example.sandbox.util.swagger.definitions.Item;
import com.example.sandbox.util.swagger.definitions.PetBody;

import io.restassured.response.Response;
import utils.report.TestListener;

@Listeners(TestListener.class)
public class PetLifeCycle extends Common {
    @Test(enabled = true,groups = {SMOKE},description ="description")
    public void Test1(){

        PostCreatePet body = PostCreatePet.builder()
                .petBody(PetBody.builder()
                        .id(generateRandomNumber())
                        .category(Item.builder()
                                .id(1)
                                .name("Hydra")
                                .build())
                        .name("Princess")
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(2)
                                .name("cute")
                                .build())
                        .status("available")
                        .build()
                ).build();


        Response response = postUrl(newPet,createJsonBody(body));
        Assert.assertEquals(response.getStatusCode(),200,"Invalid response code");

        String id = response.jsonPath().get("id").toString();

        Response  response2 = getUrl(petById.replace("{petId}",id));
        Assert.assertEquals(response2.getStatusCode(),200,"Invalid response code");
    }
    
    @Test(enabled = true, groups = { SMOKE }, description = "description")
	public void Test_FullFlow() {
    	//create
    	PostCreatePet createBody = PostCreatePet.builder()
                .petBody(PetBody.builder()
                        .id(generateRandomNumber())
                        .category(Item.builder()
                                .id(1)
                                .name("newHydra")
                                .build())
                        .name("newPrincess")
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(2)
                                .name("cute")
                                .build())
                        .status("available")
                        .build()
                ).build();

		Response createResponse = postUrl(newPet, createJsonBody(createBody));
		Assert.assertEquals(createResponse.getStatusCode(), 200, "Invalid response code");

		String id = createResponse.jsonPath().get("id").toString();
		
		//update
		PutUpdatePet updateBody = PutUpdatePet.builder()
                .petBody(PetBody.builder()
                        .id(Integer.parseInt(id))
                        .category(Item.builder()
                                .id(1)
                                .name("newHydra")
                                .build())
                        .name("newPrincess")
                        .photoUrl(HYDRAIMAGE)
                        .tag(Item.builder()
                                .id(2)
                                .name("cute")
                                .build())
                        .status("sold")
                        .build()
                ).build();

		Response updateResponse = putUrl(updatePet, updateJsonBody(updateBody));
		Assert.assertEquals(updateResponse.getStatusCode(), 200, "Invalid response code");
		
		//verify update
		Response getResponse = getUrl(petById.replace("{petId}", id));
		Assert.assertEquals(getResponse.getStatusCode(), 200, "Invalid response code");
		Assert.assertTrue("sold".equals(getResponse.path("status")), "Status should be sold after update");
		
		//delete
		Response deleteResponse = deleteUrl(deletePet.replace("{petId}", id));
		Assert.assertEquals(deleteResponse.getStatusCode(), 200, "Invalid response code");
		Assert.assertEquals(deleteResponse.path("message"), id);
		
		//verify delete
		getResponse = getUrl(petById.replace("{petId}", id));
		Assert.assertEquals(getResponse.getStatusCode(), 404, "Invalid response code");
		Assert.assertEquals(getResponse.path("message"), "Pet not found");
	}
}
