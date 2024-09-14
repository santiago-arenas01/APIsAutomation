@active
Feature: Resource testing CRUD

  @smoke
  Scenario: Get the list of active resources
    Given there are at least 5 active resources in the system
    And I send a GET request to view all active resources
    When I update the status of all active resources to inactive
    And I delete all registered resources

  @smoke
  Scenario: Update the last created resource
    Given there are at least 15 active resources in the system
    And I find the latest resource
    And I update all the parameters of this resource
    Then the resource response should have a status code of 200
    And the resource response should have the following details:
      | Name      | Trademark | Stock | Price  | Description    | Tags                          | Active |
      | Computer  | Globant   | 55    | 200.05 | New Computer   | computer, PC, tech, coding    | false  |
    And validates the response with resource JSON schema
    And I delete all registered resources