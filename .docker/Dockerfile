FROM java:8
WORKDIR /
ADD ["build/libs/REST Service-1.0-SNAPSHOT.jar", "Rest_Service.jar"]
ADD plugins plugins
EXPOSE 8089
EXPOSE 8088
CMD java -jar Rest_Service.jar