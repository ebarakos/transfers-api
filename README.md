# Transfers API

Sample API for transferring balances between accounts. 

## Getting Started
Run this using [sbt](http://www.scala-sbt.org/)

```
sbt run
```

and then go to http://localhost:9000 to see the API interface.

Swagger has been used for API documentation and test interface.
Populate the memory db by creating some accounts first through the Swagger interface

The service layer has been omitted for simplicity's sake.

Transaction atomicity is secured in TransactionsDAO. In case many transactions requested simultaneously, JDBC takes care of locking.
## Running the tests
Only controller tests have been included
```
sbt test
```

## Built With

* [Play Framework](https://www.playframework.com/) - The web framework used
* [Slick](http://slick.lightbend.com/) - ORM
* [Swagger](https://swagger.io/) - API Interface

## Authors

* **Evangelos Barakos** -

