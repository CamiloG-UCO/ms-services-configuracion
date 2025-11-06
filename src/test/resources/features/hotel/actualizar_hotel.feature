Feature: Hotel information update
  The administrator must be able to modify hotel data to keep the information up to date.

  Background:
    Given there is a hotel named "Santa Marta Resort", address "Carrera 5 #12-89", city "Santa Marta", phone "5555555", and description "Seaside hotel with full service"

  # Successful scenario
  Scenario: Update phone and description successfully
    When the admin updates the phone to "3017894561" and the description to "Has an Olympic pool, soccer field, gourmet restaurant, and gym"
    Then the system should save the changes
    And display the message "Hotel information updated successfully"

  # Alternate valid cases
  Scenario: Update only the address
    When the admin updates the address to "Carrera 7 #45-10"
    Then the system should save the changes
    And display the message "Hotel information updated successfully"

  Scenario: Update only the hotel name
    When the admin updates the name to "Santa Marta Beach Resort"
    Then the system should save the changes
    And display the message "Hotel information updated successfully"

  Scenario: Update all hotel fields
    When the admin updates the name to "Resort Caribe", address to "Calle 10 #22-15", city to "Cartagena", phone to "3109998888", and description to "Hotel with private beach and spa"
    Then the system should save the changes
    And display the message "Hotel information updated successfully"

  # Negative cases
  Scenario: Try to update a non-existent hotel
    Given there is no hotel named "Ghost Hotel"
    When the admin updates the phone to "3010001111" and the description to "Updated description"
    Then the system should display the error message "Hotel not found"

  Scenario: Try to update with empty phone
    When the admin updates the phone to "" and the description to "New description"
    Then the system should display the error message "Phone cannot be empty"

  Scenario: Try to update with null description
    When the admin updates the phone to "3021112222" and the description to null
    Then the system should display the error message "Description cannot be null"

  Scenario: Try to update with invalid phone format
    When the admin updates the phone to "123ABC" and the description to "Invalid format test"
    Then the system should display the error message "Invalid phone format"
