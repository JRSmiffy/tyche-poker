# tyche-poker

Me and the bois like to play Poker. <br> 
In the COVID-19 lockdown of 2020, we have been playing Poker online using a combo of (http://playingcards.io + http://playingcards.io). With my new found Spring skills, I thought that I could make something better. 

<br>

<img width="1438" alt="Screen Shot 2020-05-25 at 13 00 25" src="https://user-images.githubusercontent.com/34093915/82811054-c7ea0e00-9e87-11ea-8015-c93f484b8e7e.png">

<br>
<br>

## Design

Architecture
* The application consists of 3 microservices (the frontend - React JS, the backend 'poker' - Spring Boot, the hand evaluator service 'evaluate' - Node JS) and is designed to run on Cloud Foundry. 

<br>

<img width="681" alt="Screen Shot 2020-05-25 at 13 32 46" src="https://user-images.githubusercontent.com/34093915/82813148-62e4e700-9e8c-11ea-884c-934d7940df19.png">

<br>

Topics:
* Security - Spring Security Basics :white_check_mark:
* Monitoring - actuator :white_check_mark:, logger factory :white_check_mark:, ELK :x:
* Testing - TDD :x:, JUNIT :x:
* Best Practises - 12 factors :x:, REST :white_check_mark:, MVC :white_check_mark:, SOLID :x:, Good OOP :x:
* JIRA - Best Practise :x:, Agile (fortnightly sprints, with plan, retro and demo) :white_check_mark:
* MySQL+PostgreSQL - JPA/Hibernate :white_check_mark:, Best Practise (Lock it down in production, etc) :x:
* Git - Branches :white_check_mark:, Best Practise :x:
* IntelliJ + Visual Studio Code :white_check_mark:
* Spring + Thymeleaf :white_check_mark:
* HTML/JS/CSS :white_check_mark:
* JS Frameworks - React JS :white_check_mark:, Node JS :white_check_mark:, Best Practise :x:
* PaaS/CaaS - Cloud Foundry :x:, Google App Engine :x:, K8's :x:
* POSTMAN :white_check_mark:
* AIML :x:, Maths :x:

<br>

See Jira Project for what is left to be developed:
https://jrs97-dev.atlassian.net/jira/software/projects/TYC0/boards/1/roadmap

<br>
<br>


## How to Use

Deployment
* git clone https://github.com/JRSmiffy/tyche-poker.git
* LocalHost instructions:
  * poker: cd tyche-poker/poker | mvn clean spring-boot:run
  * frontend: cd tyche-poker/frontend/src | npm start
  * poker: cd tyche-poker/evaluate | node app.js
* CF instructions: deploy the apps and cs + bs a MySQL DB (tyche-db) to Spring app

Gameplay
* I recommend you use Discord to chat with the poeple you are playing with
* API's
  * Start Game
     * curl --location --request GET 'localhost:8080/start'
  * Wipe Game
     * curl --location --request GET 'localhost:8080/flood' --header 'Authorization: Basic Z29kOmNyZWF0ZWR0aGV3b3JsZGluc2V2ZW5kYXlz' 
  * more already set up in my POSTMAN: https://www.getpostman.com/collections/c66419d95e9358739bef 

Current Git Flow
* git pull at the start of every session
* git add -A, git commit -m "", git push at the end of every session
* checkout a 'sprint branch' at the start of every sprint planning sesh (keep master demo ready)
* work exclusively in this branch
* merge the sprint branch with master and delete after the sprint demo
* clear up the deleted sprint branches on each development machine



