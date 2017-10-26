Feature: Test home screen

Scenario: Check title for home screen
    Given we are on the homepage
    Then the header should be Enterprise Rifidi Management Dashboard

Scenario: Check management tabs
    Given we are on the homepage
    When I open the server menu
    Then I should see the Sensor Management menu
    And I should see the Command Management menu
    And I should see the App Management menu

Scenario: Change server display name
    Examples:
        | name   |
        | test   |
        | rifidi |
    Given we are on the homepage
    When I click on the server
    And I change the server display name to <name>
    And I click save
    Then the server name should be <name>

