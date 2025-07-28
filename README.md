# Getting Started

## Project description

[See the PDF](./docs/Crossjoin_Solutions_-_Dev_Backend_Challenge.pdf)

### Compile

```.../distributed-processor> mvn clean compile package``

### Run

- `application.properties`:
  - `app.input-directory`: location of the directory that contains the input files.
  - `app.master-instance`: By design, only this instance writes to resultado.csv the results in DB Redis  
  - `app.instance-id`: While all other instances read and process files in parallel, publishing results to Redis for aggregation.

1. **First process:** `java "-Dapp.instance-id=1" -jar target/processor-1.0.jar`  
2. **Second and subsequent processes:** `java "-Dapp.instance-id=n" -jar target/processor-1.0.jar`
     - Use a different n for each new process
     - n must be an Integer