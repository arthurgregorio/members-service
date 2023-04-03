## Running the project

Before start, check if your docker enviroment is running, the project is using testcontainers for testing and if no docker 
env is found, tests won't pass.

1. Run maven build with `mvn clean package`
2. Start the database using the compose file inside *docker* folder in the root the project with the command `docker compose -p members-service start`
3. Start the application with `java -jar .\target\members-service-0.1.0.jar`

If everything worked fine now you are able to call the endpoints using `http://localhost:8085/api/members` or using 
postman and importing the collection provided with the project (look for a folder called postman in the root).

### Important notes

- Test will run at build time as usual
- Database is running on port 5433, to change it you will need to update the `docker-compose.yml` file and the `application.yml`
- Project is running on port 8085, you can change it on the `application.yml` file
