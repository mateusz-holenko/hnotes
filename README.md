# hNOTES

The place to keep your notes and ideas organized.

## About the project

This is a playground project to explore technologies, tools and good patterns to build scalable distributed systems.
Even though it's not intended to be ever put into real production, the general aim is to keep it as close to real solutions as possible.

hNotes are experimental; the design might (and probably will) change over time, perhaps even the domain itself (memo-taking app) might morph into something else.

# Components

In order to keep the development process as simple as possible, I decided to keep all the code in a single repository.

*In a real system it would probably be best to split components into separate repositories and use git submodules, git repo or other tools to bring all the elements together*.

The current structure of the system is depicted in the diagram below:

```mermaid

flowchart TB
  direction TB
  subgraph WORLD[" "]
    direction TB
    U1["User1"]
    U2["..."]
    UN["UserN"]
  end
  subgraph HN["hnotes"]
    direction TB
    subgraph NG["nginx"]
        direction TB
        Angular(["fronted"])
        API(["API proxy"])
    end
    US["users"]
    ST["status"]
    VE["verification"]
    SE["search"]
    subgraph NO["notes"]
        direction LR
        logic <--> db[("db")]
    end

    BR[["Artemis / Active MQ"]]

    API --> NO
    API --> US

    NO --> SE
    
    NO <-.-> BR
    VE <-.-> BR
    ST <-.-> BR 
   end

  U1 --> NG
  UN --> NG
  NG --- ST

  click Angular "https://github.com/mateusz-holenko/hnotes/tree/readme/frontend" "Frontend application source code"
```

## Frontend application

Users interact with the system via the [frontend web application](frontend) implemented in Angular.

It allows user to log into the system, view existing notes and edit them: create new ones, update or delete existing ones.

Under the hood it communicates with [Users](backend/users_service) and [Notes](backend/notes_service) services hidden behind the proxy API provided by [nginx](k8s/dockerfiles/frontend/default.conf.template).

Future plans:
- [ ] Search functionality
- [ ] Improved login form verification
- [ ] Websocket connections with [Status](backend/status_service) service for async updates

## Notes Service

Notes is the main service in the system exposing API for managing notes.
*Due to the nature of the system, this service will probably be the most loaded one and should be considered the first to scale horizontally.*

The data itself is kept in a database that is accessed in the code via a [hibernate-based interface](/backend/notes_service/src/main/java/houen/hnotes/NotesRepository.java).
*For the time being, a "standard" SQL DBMS is being used, but to improve future performance a plan is to adpot a noSQL backaned and switch to fully reactive (WebFlux) flow.*

To provide a more complex functionality, the service integrates with additional components as described in the following sections.

## Search Service integration

[Serach](backend/notes_service/src/main/java/houen/hnotes/ElasticSearchService.java) support is provided by a third-party service running [ElasticSearch](http://elastic.co).

Each new note added to `Notes` is additionally indexed in `Search` so that it can be later fetched by its content.
Later, when fetching items from `Notes`, an optional `query` parameter can be passed.
In such case `Notes` asks `Search` to provide a filtered list that is passed back to the user.

## Verification Service integration

[Verification](backend/verification_service) is an additional service that checks content of newly added or updated notes looking for [distrurbing patterns](backend/verification_service/veri.py) that should be reported.

The communication between services is asynchronous and carried on via an Apache Artemis message broker.

## Users Service
## Search Service
## Verification Service
## Status Service
## Message broker
## Monitoring

# Configuration

## Helper scripts
## Docker images
## Docker compose
## Kubernetes

# Current status & future plans

## Frontend application

TODO:
* 
