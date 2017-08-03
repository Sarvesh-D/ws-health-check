[![Build Status](https://travis-ci.org/Sarvesh-D/ws-health-check.svg?branch=master)](https://travis-ci.org/Sarvesh-D/ws-health-check)
[![codecov](https://codecov.io/gh/Sarvesh-D/ws-health-check/branch/master/graph/badge.svg)](https://codecov.io/gh/Sarvesh-D/ws-health-check)


# ws-health-check
Checks health of Web Services and creates reports

# Road Map

### MileStone 1
 - Code Coverage 80+%
 - Exception Handling
 - Logging
 - Documentation
### MileStone 2
 - Admin Functionalities Using Spring Data
	 - Configure Environment Hirerachy
	 - Configure Scheduler Intervals
	 - Configure Report Properties
	 - Configure Users for mailing report
 - Securing Admin Functionalities Using Spring Security
 - Saving Report to DB (currently its in-memory)
### MileStone 3
 - RabitMQ for sending admin requests to queue
 - Spring Boot for self configuring app server
 - Circuit Breakers for Admin/Monitoring APIs
 - Containerise using docker for easy deployment
### MileStone 4
 - Immediate mail trigger for frequently failing services (decide frequency for mail trigger)
 - Immediate mail trigger for critical services, providers, environments (using observer pattern)
 - Support different rules for determining status of service (using strategy pattern)
	 - Support for POST/PUT/DELETE APIs
	 - Support rules to read the API response to determine its status
 - Log/Store in DB, the reason why a particular API failed
 - Provide UI to view Audit logs of reasons of failed APIs
### MileStone 5
 - Make the application Software as a Service (SaaS). Provide UI for clients to register themselves to the application. Only registered user will have access to all application features.
 - Use OAUTH 2.0 for authentication.
