# Parking management appication
Recruitment task implementation - REST API built using Spring Boot.

## User stories
1. As a driver, I want to start the parking meter, so I don’t have to pay the fine for the invalid
parking.
2. As a parking operator, I want to check if the vehicle has started the parking meter.
3. As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
4. As a driver, I want to know how much I have to pay for parking.
5. As a parking owner, I want to know how much money was earned during a given day.

## Notes

The parking rates are:

| Driver type | 1st hour | 2nd hour | 3rd and each next hour     |
| Regular     |  1 PLN   | 2 PLN    | 2x more than hour before   |
| VIP         |   free   | 2 PLN    | 1.5x more than hour before |

Don’t implement any payment transactions, penalties and so on.
For now, only one currency is accepted but it’s likely to change in the future.

## Application of recruitment task rules

1. Java, Spring Boot, tests: Groovy/Spock
2. Project built by Maven. Run by using start.sh script.
3. In memory H2 database.
4. Implementation notes and project assumptions in ASSUMPTIONS.md file.