## User story assumptions

1. As a driver, when I stop a parking meter, I also get information on how much money was spent on this single parking. 
When querying for summary, I get sum of my history payments.
2. As a parking operator, I check if specific slot is taken in the system, which means that some driver will pay for it.
I do not check ID of driver or vehicle (in case some user would like to pay for friend's parking)
3. Every stated hour adds to the payment.

## Implementation notes

1. Payment rates must be present in database, as well as drivers and parking meters, before one can make a query. 
This implemetation is of required API, and does not include user registration, parking meter registration etc.
2. As payment plans have specific tempate (1st hour, 2nd hour, 3rd and every next is worth x times more than hour 
before), this is clearly a geometric progression. It was implemented as such, because there is a ready formula for 
calculating sum of geometric series (no need for looping).
3. All payments and rate plans have assigned currency. Adding new currency is only a matter of adding new enum value. 
Then, payment summaries are being calculated for every currency separately.
4. Due to small amount of business logic, most of code is covered by integration tests. Calculating payments 
(geometric series formula) is covered in separate unit tests.