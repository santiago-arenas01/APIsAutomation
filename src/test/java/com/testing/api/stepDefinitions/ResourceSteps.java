package com.testing.api.stepDefinitions;

import com.testing.api.models.Resource;
import com.testing.api.requests.ResourceRequest;
import com.testing.api.utils.Randoms;
import com.testing.api.utils.ScenarioContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

/**
 * The type Resource steps.
 */
public class ResourceSteps {
    private static final Logger logger = LogManager.getLogger(ResourceSteps.class);

    private final ResourceRequest resourceRequest = new ResourceRequest();

    private Response response;
    private Resource resource;

    /**
     * There are at least n registered resources in the system.
     *
     * @param quantity the quantity
     */
    @Given("there are at least {int} active resources in the system")
    public void thereAreAtLeastNRegisteredResourcesInTheSystem(int quantity) {
        response = resourceRequest.getResources();
        List<Resource> resourceList = resourceRequest.getResourcesEntity(response);

        if (resourceList.size() < quantity) {
            for (int i = resourceList.size(); i < quantity; i++) {
                Resource randomResource = Randoms.createRandomResource();
                response = resourceRequest.createResource(randomResource);
                Assert.assertEquals(201, response.statusCode());
                logger.info("Created resource: " + randomResource);
            }
        }
    }

    /**
     * Send aget request to view all active resources.
     */
    @And("I send a GET request to view all active resources")
    public void iSendAGETRequestToViewAllActiveResources() {
        response = resourceRequest.getResources();
        List<Resource> allResources = resourceRequest.getResourcesEntity(response);

        List<Resource> activeResources = allResources.stream()
                .filter(Resource::isActive)
                .toList();

        logger.info("Active Resources: " + activeResources);
    }

    /**
     * Have a resource with the following details.
     *
     * @param resourceData the resource data
     */
    @And("the resource response should have the following details:")
    public void iHaveAResourceWithTheFollowingDetails(DataTable resourceData) {
        Map<String, String> resourceDataMap = resourceData.asMaps().get(0);
        resource = Resource.builder()
                .name(resourceDataMap.get("Name"))
                .trademark(resourceDataMap.get("Trademark"))
                .stock(Integer.parseInt(resourceDataMap.get("Stock")))
                .price(Float.parseFloat(resourceDataMap.get("Price")))
                .description(resourceDataMap.get("Description"))
                .tags(resourceDataMap.get("Tags"))
                .active(Boolean.parseBoolean(resourceDataMap.get("Active")))
                .build();
        logger.info("Resource mapped: " + resource);
    }

    /**
     * The resource response should have a status code of.
     *
     * @param statusCode the status code
     */
    @Then("the resource response should have a status code of {int}")
    public void theResourceResponseShouldHaveAStatusCodeOf(int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
    }

    /**
     * User validates response with resource json schema.
     */
    @Then("validates the response with resource JSON schema")
    public void userValidatesResponseWithResourceJSONSchema() {
        String path = "schemas/resourceSchema.json";
        Assert.assertTrue(resourceRequest.validateSchema(response, path));
        logger.info("Successfully Validated schema from Resource object");
    }

    /**
     * Delete all registered resources.
     */
    @And("I delete all registered resources")
    public void deleteAllRegisteredResources() {
        response = resourceRequest.getResources();
        List<Resource> resources = resourceRequest.getResourcesEntity(response);
        resources.forEach(logger::info);

        for (Resource resource : resources) {
            response = resourceRequest.deleteResource(resource.getId());
            Assert.assertEquals(200, response.statusCode());
            logger.info("Deleted resource with ID: " + resource.getId());
        }
    }

    /**
     * Update all active resources to inactive.
     */
    @When("I update the status of all active resources to inactive")
    public void iUpdateAllActiveResourcesToInactive() {
        response = resourceRequest.getResources();
        List<Resource> allResources = resourceRequest.getResourcesEntity(response);
        List<Resource> activeResources = allResources.stream()
                .filter(Resource::isActive)
                .toList();

        for (Resource resource : activeResources) {
            resource.setActive(false);
            response = resourceRequest.updateResource(resource, resource.getId());
            Assert.assertEquals(200, response.statusCode());
            logger.info("Updated resource to inactive: " + resource);
            String path = "schemas/resourceSchema.json";
            Assert.assertTrue(resourceRequest.validateSchema(response, path));
            logger.info("Successfully Validated schema from Resource object");
        }
    }

    /**
     * Find the latest resource.
     */
    @When("I find the latest resource")
    public void iFindTheLatestResource() {
        response = resourceRequest.getResources();
        List<Resource> allResources = resourceRequest.getResourcesEntity(response);

        Resource latestResource = allResources.get(allResources.size() - 1);
        ScenarioContext.set("latestResource", latestResource);
        logger.info("Latest resource found: " + latestResource);
    }

    /**
     * Update all the parameters of this resource.
     */
    @And("I update all the parameters of this resource")
    public void IUpdateAllTheParametersOfThisResource() {
        Resource latestResource = (Resource) ScenarioContext.get("latestResource");

        latestResource.setName("Computer");
        latestResource.setTrademark("Globant");
        latestResource.setStock(55);
        latestResource.setPrice(200.05f);
        latestResource.setDescription("New Computer");
        latestResource.setTags("computer, PC, tech, coding");
        latestResource.setActive(false);

        response = resourceRequest.updateResource(latestResource, latestResource.getId());
        logger.info("Updated resource: " + latestResource);
    }

}
