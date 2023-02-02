### Run and connect to postgres on VPS

1. Login to the remote server via CLI with following command: `ssh accountName@bonnee.eu`. If you don't have an account yet, contact @petr-vanis-cgi.
2. To create your own postgres container you can use an existing image 'postgres'. To do that, enter the following command. `docker run -d --name "${USER}_postgres" -p 127.0.0.1:[pick free port number]:5432 -e POSTGRES_PASSWORD=doodle -e POSTGRES_USER=doodle -v /home/$USER/postgres/db:/var/lib/postgresql/data postgres`
   **IMPORTANT**: `127.0.0.1:[pick free port number]:5432` you have to pick an unused port. To check all the running containers and their ports enter this command: `docker ps`.
   **IMPORTANT**: `POSTGRES_PASSWORD=doodle` and `POSTGRES_USER=doodle` will be needed to connect to the database.
3. Now you need to change the backend properties so the program can connect to the postgres container you just created. Navigate to the following file `bonnie/starter/src/main/resources/application.properties` and edit the PostgreSQL properties.

   `spring.datasource.url=jdbc:postgresql://localhost:5432/` - if you have postgress installed locally you will have to pick a different port because 5432 is usually taken by it.
   `spring.datasource.username=doodle`-username you chose when you where creating postgres container.
   `spring.datasource.password=doodle`-password you chose when you where creating postgres container.
4. Now you can set up SSH tunnel to connect to your postgres container using CLI with following command:`ssh -N accountName@bonnee.eu -L xxxx:127.0.0.1:yyyy`

   **xxxx** - your local free port you chose in application.properties (spring.datasource.url=jdbc:postgresql://localhost:5432/).

   **yyyy** - port you chose when creating your own postgres docker container.
5. Run the back-end, front-end and kafka. You are connected to the remote postgres database.

### Run postgres locally

1. Install Postgres software with pgAdmin on your local machine.
2. Open pgAdmin and connect to the default server **PostgreSQL 15** using the master password you set when installing postgres. 
3. Create a new Database e.g. **localBonnie**. The database must be created manually but the tables will be auto created.
4. Run the app with following command from /starter folder 
```mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:postgresql://localhost:5432/localBonnie"```
<-- This argument will override the application.properties url line so you don't have to change the code.
**IMPORTANT**: you can also override the username and password the same way. Just add corresponding arguments. e.g. ```-Dspring.datasource.username=doodle``` ..

**OR** if you wish to change the code directly, edit the application.properties:
`spring.datasource.url=jdbc:postgresql://localhost:5432/localBonnie`
`spring.datasource.username=postgres` (default user name of PostgreSQL 15 server, can be changed).
`spring.datasource.password=doodle` (master password you set when installing postgres).

### Encrypting credentials before running the application:

1. Generate username/password using the following command within ./starter directory:
   `mvn jasypt:encrypt-value -Djasypt.encryptor.password=BONNEE_MASTER_PWD -Djasypt.encryptor.algorithm=PBEWithMD5AndDES -Djasypt.plugin.value=POSTGRES_PWD`
   **IMPORTANT**: The same command can be used to encrypt any string. You just change the value. e.g. `-Djasypt.plugin.value=POSTGRES_USER`
   **IMPORTANT**: POSTGRES_PWD and POSTGRES_USER must match the credentials used for creating your postgres docker container or your local postgres database credentials. BONNIE_MASTER_PASSWORD is a master password you will need to use to run the app with encrypted credentials as showed below.
   **POSSIBLE ERROR**: No jasypt.plugin.value property provided -> Add "..." around the parameters like:

   `"-Djasypt.encryptor.password=$BONNEE_MASTER_PWD" "-Djasypt.encryptor.algorithm=PBEWithMD5AndDES" "-Djasypt.plugin.value=$POSTGRES_USR"`
2. Grab the output value, (it starts with ENC(...) then place it into the application properties file e.g: spring.datasource.username=ENC(6KpVjqrPwKvLt/5Cjo2ZHg==),
   spring.datasource.password=ENC(6KpVjqrPwKvLt/5Cjo2ZHg==)
3. To run the app with encrypted credentials: `mvn spring-boot:run -Dspring-boot.run.arguments="--jasypt.encryptor.password=BONNEE_MASTER_PWD --spring.datasource.password=ENC(6KpVjqrPwKvLt/5Cjo2ZHg==)"`
