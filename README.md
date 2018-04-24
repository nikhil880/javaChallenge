# javaChallenge
AssetManagement-JavaChallenge

This is a basic bank account rest service to create account, get account details and transfer funds between accounts.
It has the test coverage of more than 90% for all the classes excluding the model and exception.

Few points I would like to mention to make it production ready are as follows.

1.	As this has the thread safe methods like transfer funds, we can add jmeter tests to simulate load for performance testing under multithreaded environment. The jmeter tests can be integrated into the build process for continuous integration
2.	The Actuator dependency is added to POM , which provides endpoints for health check , heap dump, etc which can be used effectively to monitor the application.
3.	This is a spring boot project which runs on embedded tomcat manager, further we can containerize it using docker and run multiple containers in a cluster to distribute the load, this will also provide fault tolerance in case a node is down.
4.	Further to enhance the quality , we can use static code analysis tolls like sonar, which are easy to integrate using maven plugins and can also be integrated in the bamboo plan.
5.	We can impose quality gates using sonar plugin, to ensure quality code deployment to production.
6. Also rest fixtures can be added using fitenesse tool, to include integration tests in the build plan.
7. Add authentication and authorization to the service methods. Spring boot provides security starter package to add authentication mechanism like basic auth, oath2 , etc along with testing capabilities.