package com.testing.api.stepDefinitions;

import com.testing.api.models.Client;
import com.testing.api.requests.ClientRequest;
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
import java.util.Stack;

/**
 * The type Client steps.
 */
public class ClientSteps {
    private static final Logger logger = LogManager.getLogger(ClientSteps.class);

    private final ClientRequest clientRequest = new ClientRequest();

    private Response response;
    private Client client;

    /**
     * There are at least 10 registered clients in the system.
     *
     * @param quantity the quantity
     */
    @Given("there are at least {int} registered clients in the system")
    public void thereAreAtLeast10RegisteredClientsInTheSystem(int quantity) {
        response = clientRequest.getClients();
        List<Client> clientList = clientRequest.getClientsEntity(response);

        if (clientList.size() < quantity) {
            for (int i = clientList.size(); i < quantity; i++) {
                Client randomClient = Randoms.createRandomClient();
                response = clientRequest.createClient(randomClient);
                Assert.assertEquals(201, response.statusCode());
                logger.info("Created client: " + randomClient);
            }
        }

        // Verify that we now have at least 10 clients
        response = clientRequest.getClients();
        clientList = clientRequest.getClientsEntity(response);
        Assert.assertTrue(clientList.size() >= quantity);
    }

    /**
     * Have a client with the following details.
     *
     * @param clientData the client data
     */
    @Given("I have a client with the following details:")
    public void iHaveAClientWithTheFollowingDetails(DataTable clientData) {
        Map<String, String> clientDataMap = clientData.asMaps().get(0);
        client = Client.builder()
                .name(clientDataMap.get("Name"))
                .lastName(clientDataMap.get("LastName"))
                .country(clientDataMap.get("Country"))
                .city(clientDataMap.get("City"))
                .email(clientDataMap.get("Email"))
                .phone(clientDataMap.get("Phone"))
                .build();
        logger.info("Client mapped: " + client);
    }

    /**
     * Send get request.
     *
     * @param clientId the client id
     */
    @When("I retrieve the details of the client with ID {string}")
    public void sendGETRequest(String clientId) {
        response = clientRequest.getClient(clientId);
        logger.info(response.jsonPath()
                .prettify());
        logger.info("The status code is: " + response.statusCode());
    }

    /**
     * Send adelete request to delete the client with id.
     *
     * @param clientId the client id
     */
    @When("I send a DELETE request to delete the client with ID {string}")
    public void iSendADELETERequestToDeleteTheClientWithID(String clientId) {
        response = clientRequest.deleteClient(clientId);
    }

    /**
     * The response should have a status code of.
     *
     * @param statusCode the status code
     */
    @Then("the response should have a status code of {int}")
    public void theResponseShouldHaveAStatusCodeOf(int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
    }

    /**
     * The response should have the following details.
     *
     * @param expectedData the expected data
     */
    @Then("the response should have the following details:")
    public void theResponseShouldHaveTheFollowingDetails(DataTable expectedData) {
        client = clientRequest.getClientEntity(response);
        Map<String, String> expectedDataMap = expectedData.asMaps().get(0);

        Assert.assertEquals(expectedDataMap.get("Name"), client.getName());
        Assert.assertEquals(expectedDataMap.get("LastName"), client.getLastName());
        Assert.assertEquals(expectedDataMap.get("Country"), client.getCountry());
        Assert.assertEquals(expectedDataMap.get("City"), client.getCity());
        Assert.assertEquals(expectedDataMap.get("Email"), client.getEmail());
        Assert.assertEquals(expectedDataMap.get("Phone"), client.getPhone());
        Assert.assertEquals(expectedDataMap.get("Id"), client.getId());

        String oldPhoneNumber = (String) ScenarioContext.get("oldPhoneNumber");
        Assert.assertNotEquals(oldPhoneNumber, client.getPhone(), "Phone number should be updated.");
    }

    /**
     * User validates response with client json schema.
     */
    @Then("validates the response with client JSON schema")
    public void userValidatesResponseWithClientJSONSchema() {
        String path = "schemas/clientSchema.json";
        Assert.assertTrue(clientRequest.validateSchema(response, path));
        logger.info("Successfully Validated schema from Client object");
    }

    /**
     * Send apost request to create a client named laura.
     */
    @And("I send a POST request to create a client named Laura")
    public void iSendAPOSTRequestToCreateAClientNamedLaura() {
        response = clientRequest.createDefaultClient();
    }

    /**
     * Send aget request to view all the clients.
     */
    @When("I send a GET request to view all the clients")
    public void iSendAGETRequestToViewAllTheClients() {
        response = clientRequest.getClients();
        logger.info(response.jsonPath().prettify());
    }

    /**
     * Find first client named.
     *
     * @param name the name
     */
    @When("I find the first client named {string}")
    public void findFirstClientNamed(String name) {
        client = clientRequest.findClientByName(name, response);
        logger.info("Found client: " + client);
    }

    /**
     * Save her current phone number.
     */
    @When("I save her current phone number")
    public void saveHerCurrentPhoneNumber() {
        String oldPhoneNumber = client.getPhone();
        logger.info("Current phone number: " + oldPhoneNumber);
        ScenarioContext.set("oldPhoneNumber", oldPhoneNumber);
    }

    /**
     * Update client phone number.
     *
     * @param clientId    the client id
     * @param requestBody the request body
     */
    @When("I send a PUT request to update the client with ID {string}")
    public void updateClientPhoneNumber(String clientId, String requestBody) {
        client = clientRequest.getClientEntity(requestBody);
        response = clientRequest.updateClient(client, clientId);
        logger.info("Updated client: " + client);
    }

    /**
     * Delete all registered clients.
     */
    @Then("I delete all registered clients")
    public void deleteAllRegisteredClients() {
        response = clientRequest.getClients();
        List<Client> clients = clientRequest.getClientsEntity(response);
        clients.forEach(logger::info);

        for (Client client : clients) {
            response = clientRequest.deleteClient(client.getId());
            Assert.assertEquals(200, response.statusCode());
            logger.info("Deleted client with ID: " + client.getId());
        }

    }
}
