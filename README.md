# ws-health-check
Checks health of Web Services and creates reports

*** Upcoming ***

*** Refactoring ***
- refactor code to java 8
- use lombok
- refactor code to use spring java config

*** Admin Functionalities ***
- add admin functionality for add/update/delete configured environment/providers/services
- add admin functionality to update scheduler time intervals
- add admin functionality to add/update/delete users for sending emails.
- add admin functionality to set alert mail when a environment/provider/service goes down. (Use Observer pattern to observe state of environment/provider/service)
- secure admin APIs with spring security

*** Resilience ***
- use rabbit MQ for sending/receiving admin requests
- add circuit breakers for services

*** Scaling ***
- migrate to spring boot
