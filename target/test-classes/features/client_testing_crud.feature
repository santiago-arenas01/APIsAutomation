@active
Feature: Client testing CRUD

  @smoke
  Scenario: Change the phone number of the first Client named Laura
    Given there are at least 10 registered clients in the system
    And I send a GET request to view all the clients
    And I send a POST request to create a client named Laura
    When I send a PUT request to update the client with ID "11"
    """
    {
      "name": "Laura",
      "lastName": "Smith",
      "country": "USA",
      "city": "New York",
      "email": "laura.smith@example.com",
      "phone": "555-987-6543"
    }
    """
    Then the response should have a status code of 200
    And the response should have the following details:
      | Name  | LastName | Country | City      | Email                  | Phone          | Id      |
      | Laura | Smith    | USA     | New York  | laura.smith@example.com | 555-987-6543 | 11 |
    And validates the response with client JSON schema
    And I delete all registered clients

  @smoke
  Scenario: Update and delete a New Client
    Given there are at least 1 registered clients in the system
    And I send a GET request to view all the clients
    When I send a PUT request to update the client with ID "1"
    """
    {
      "name": "Manuel",
      "lastName": "Muñoz",
      "country": "Colombia",
      "city": "Bogota",
      "email": "mf.munoz@globant.com",
      "phone": "999-543-1298"
    }
    """
    Then the response should have a status code of 200
    And validates the response with client JSON schema
    And I delete all registered clients