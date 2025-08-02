# Getting Started

A beginner-level project developed in 2 days to get hands-on experience with the Java Spring Boot framework and Redis as an in-memory data store.

This project was created as part of my initial learning journey with Spring Boot.

⚠️ Note: This project is not representative of the level of structure, polish, or design found in my other distributed systems work.
Its primary value lies in the exploration of Spring Boot patterns and distributed messaging within time and support constraints.

## Project description

[See the PDF](./docs/Crossjoin_Solutions_-_Dev_Backend_Challenge.pdf)

### Compile

```.../distributed-processor> docker-compose up```
- Launches a Docker container with Apache Pulsar in standalone mode

- **Warning**: Launch Pulsar before compiling

```.../distributed-processor> mvn clean compile package```
- Run Redis after this, so the test Run does not create entries on the Redis Real-time Data Platform

```docker run -p 6379:6379 redis```
- **Warning**: The keys in Redis are not being removed after processing. Ideally, they should be cleared, but the solution to do so while avoiding race conditions was not implemented. Therefore, please make sure to manually clear the Redis database before each experimental run to ensure accurate results. You can do this by restarting Redis.

### Run

### Launch Redis and Pulsar before starting

- `application.properties`:
  - `app.input-directory`: location of the directory that contains the input files.
  - `app.master-instance`: By design, only this instance writes to resultado.csv the results stored in Redis  
  - `app.instance-id`: All instances read and process files/lines in parallel, publishing results to Redis for aggregation  
  - `app.output-file`: Name of the output file

1. **First process:** `java "-Dapp.instance-id=1" -jar target/processor-1.0.jar`
  - By default the master instance is 1, but you can change this value and update the id accordingly
3. **Second and subsequent processes:** `java "-Dapp.instance-id=n" -jar target/processor-1.0.jar`
     - Use a different n for each new process
     - n must be an Integer
