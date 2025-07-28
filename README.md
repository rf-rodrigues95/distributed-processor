# Getting Started

## Project description

[See the PDF](./docs/Crossjoin_Solutions_-_Dev_Backend_Challenge.pdf)

### Compile

```.../distributed-processor> mvn clean compile package``

### Run

```.../distributed-processor> docker-compose up``
- Launches a Docker container with Apache Pulsar in standalone mode

```.../distributed-processor> docker-compose up``
- docker run -p 6379:6379 redis
- **Warning**: The keys in Redis are not being removed after processing. Ideally, they should be cleared, but due to time constraints, a robust solution to avoid overwriting conflicts was not implemented. Therefore, please make sure to manually clear the Redis database before each test run to ensure accurate results.

### Launch Redis and Pulsar before starting

- `application.properties`:
  - `app.input-directory`: location of the directory that contains the input files.
  - `app.master-instance`: By design, only this instance writes to resultado.csv the results in DB Redis  
  - `app.instance-id`: While all other instances read and process files in parallel, publishing results to Redis for aggregation.

1. **First process:** `java "-Dapp.instance-id=1" -jar target/processor-1.0.jar`  
2. **Second and subsequent processes:** `java "-Dapp.instance-id=n" -jar target/processor-1.0.jar`
     - Use a different n for each new process
     - n must be an Integer