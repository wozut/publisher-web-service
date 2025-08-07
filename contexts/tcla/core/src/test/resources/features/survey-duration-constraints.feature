Feature: Survey duration constraints

  Scenario: a team owner tries to create a survey with a duration above the maximum
    Given the maximum duration of a survey is 14 days
    When a team owner creates a survey with a duration of 15 days
    Then failure occurs because it is not possible

  Scenario: a team owner tries to modify the survey start date after survey has started
    Given a survey that has already started
    When a team owner tries to modify the start date of that survey
    Then failure occurs because start date cannot be modified after survey has already started
