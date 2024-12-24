Feature: Opinion & Transalation for Elpais.com

  Background:
    Given I visit the website "https://elpais.com"
    Then I am on the "EL PAÍS: el periódico global" homepage


  Scenario: Ensure the website's text is displayed in Spanish
    Given I am on the "EL PAÍS: el periódico global" homepage
    Then the website's text should be displayed in "Spanish"


  Scenario: Scrape articles from the Opinion section
    Given I am on the "EL PAÍS: el periódico global" homepage
    When I navigate to the "Opinion" section of the website
    And I fetch the first 5 articles
    Then I print the title and content of each article in "Spanish"
    And I download and save the cover image of each article, if available


  Scenario: Translate the titles of articles from the Opinion section
    Given I navigate to the "opinion" section of the website
    When I fetch the first 5 articles titles
    Then I translate the article titles to "English"
    And I print the translated titles
1
  @smoke
  Scenario: Identify repeated words from translated titles
    Given I navigate to the "opinion" section of the website
    When I fetch the first 5 articles titles
    Then I translate the article titles to "English"
    And I have the translated headers
    When I identify words repeated more than 2 times across all headers
    Then I print each repeated word with its count of occurrences



