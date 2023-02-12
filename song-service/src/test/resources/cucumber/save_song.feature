Feature: save song

  Scenario: save correct song
    Given song with resource id 1
    When attempt to save it
    Then it should return the id of the saved song