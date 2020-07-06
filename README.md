# Load balancer

### Project structure
All the source files are located under src/main/kotlin

All the test files are located under src/test/kotlin

The project is partitioned as follow:
* capacity -> contains the cluster capacity limit calculator
* heartbeat -> contains the heart beat which select when to 
activate or deactivate a provider
* providerselector -> contains the mechanism for excluding / including a provider
* scheduler -> contains the random and the round robin scheduler


The load balancer is composed of a list of provider. 
The boolean variable simulateServer aims at simulating a running server for a limited period of time (6 seconds).
The 2seconds heartbeat check can be verified easier like this

### Run
In order to build with test, type the following command in the root folder of the project
```
gradle clean build
```

### Personal environment
* windows 10 pro version 1909 - OS build 18363.900
* gradle version: 5.6.2
* kotling version: 1.3.41
* JVM version: 1.8.0_201 (Oracle Corporation 25.201-b09)
