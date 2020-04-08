# Spring boot vertx integration

## Description
Demo project showing how to handle REST api's using vertx\
and processing them through event bus

There is one sender and one receiver. Sender sends the message\
to consumer on a particular topic.\
Depending on the topic, consumer sends the corresponding message\
back to sender

## Running the project

```bash
./mvnw spring-boot:run
```

Navigate to
```html
http://localhost:8081/api/articles
```

This displays all articles.

To fetch a particular article navigate to

```html
http://localhost:8081/api/article/<articleid>
```
