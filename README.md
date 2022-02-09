# Wallet App
A monetary wallet that holds the current balance for a player.

<br />

### How to run
Navigate to the Wallet repository and run `mvn clean install`, next `mvn spring-boot:run`

<br />

### Endpoints
#### CREDIT
`POST "/account/credit"` - accepts RequestBody that consists of `amount` and `userId`
<br/>


#### DEBIT
`POST "/account/debit"` - accepts RequestBody that consists of `amount` and `userId`
<br/>

#### ACCOUNT BALANCE
`GET "/account/{userId}/balance"` - accepts PathVariable `userId`
<br/>


#### ACCOUNT TRANSACTION HISTORY
`GET "/account/{userId}/history"` - accepts PathVariable `userId` 
<br/>

