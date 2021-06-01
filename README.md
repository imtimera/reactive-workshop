# Backend Challenge

## Story


You work for the `Music Artist` company as a backend developer.
This company owns the largest database referencing music artists all around planet Earth.

You are part of the `Music Artist API` Team which develops various APIs to access to the artist database.
A new Product Owner has been recently hired and has just presented the new Roadmap for the next weeks.

A new partnership has just been setup with the `Music Fans Social Network`. This platform allows music fans to post comments
about their favorite artists. The partnership will be announced soon with a lot of advertising and on your company side,
it has been decided to quickly provide some fan comments along with artist information through the API you are working on.

Your mission is to update the existing API to add the comments to artist info. In addition, new streaming API will be added
as part of the initiative.



## Technical Overview

There are 2 components

![macro architecture](doc/macro_architecture.svg)



### `external-service`

It simulates an external (distant) REST service. *There is no need to modify it as part of this challenge*.

It runs on port `3004` by default and provides `comments` API

See `com.bonitasoft.reactiveworkshop.external.ExternalApplication` 

### `backend`

The backend to work on.

It runs on port `8080`


## How to run

The only requirement is **JDK 11**.

### IDE configuration

The projects use [Lombok](https://projectlombok.org/) for code generation. In order to make the projects build in your IDE
* Eclipse: https://projectlombok.org/setup/eclipse
* IntelliJ: https://projectlombok.org/setup/intellij and also enable the Annotation Processors in the IDE project
* others: see the `Install` tab in the Lombok documentation



### `external-service`

Run the `external-service` with command `./gradlew :external-service:bootRun`

It  will launch the spring boot application on `http://localhost:3004/`

Try [this url to verify it works](http://localhost:3004/comments/last10)

### `backend`

Run the `backend` with command `./gradlew :backend:bootRun`

It will serve the application on `http://localhost:8080/`

Try [this url to verify it works](http://localhost:8080/artists) (it contains currently a large amount of artists)

In the current state, it uses an embedded h2 database


## What to do

:information_source: All code produced must be production-ready.

### Add a new API method that returns one artist and its 10 last comments

It should be called on this URL: `http://localhost:8080/artist/{artistId}/comments`

and should return something like
```json
{ 
  "artistId": "dfjksl342f32",
  "artistName": "Radiohead",
  "genre": "Rock",
  "comments": [
      {
        "userName": "johndoe",
        "comment": "Nice band!"
      },
      {
        "userName": "walterbates",
        "comment": "It rocks"
      }
  ]
}
```

### Add a new API method that return a stream of comments and associated artists filtered by a genre name

It should be called on this URL: `http://localhost:8080/genre/{genre}/comments/stream`

and return an infinite stream of json like this:

```json
{
  "artistId": "dfjksl342f32",
  "artistName": "Radiohead",
  "userName": "johndoe",
  "comment": "Nice band!"
}
{
  "artistId": "fdsklj39fsjl",
  "artistName": "Archive",
  "userName": "walterbates",
  "comment": "It rocks"
}
```

To do that, you will need to use a `WebClient` instead of the provided `RestTemplate`

### Containerize the application

Package the application in a docker image and write a docker-compose to run it on Postgres.


