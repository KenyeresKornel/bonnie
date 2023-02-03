Jenkins and Docker are installed on the remote server so there is no need to have them on your local machine.
To be able to operate them you have to receive your accounts. Currently the VPS is owned and maintained
by @petr-vanis-cgi. If you need SSH access to the VPS he can create a user for you.

**Login to:**

-web UI https://bonnee.eu/ci/
-SSH ssh accountName\@bonnee.eu

### Jenkins

The current Bonnie-Jenkins multibranch build is using
Jenkinsfile located in the parent folder (Hexagonal).

To see all the available plugins on Jenkins from the web UI go to
Dashboard>Manage Jenkins>Manage Plugins>Installed Plugins.

To create a new build, click on Dashboard>New Item, then choose a name and type
of the project. Fill out the configuration and click on Apply + Save.
Then click on Build Now, to start the building process.

### Docker

The pipeline from Jenkinsfile contains, among others,
instructions to create docker images based on Dockerfiles located in the
parent folder. Also, there is a "docker-compose.yml" file to
automatically run created containers and the message services, so there
is no need to do it manually.

##### Basic CLI commands to operate docker images/containers manually.

**docker images** - show all images.
**docker ps** - show running containers.
**docker run repository | imageId** - create a new container of an image, and execute the container.
**docker run -p portOfHost:containerPort repository | imageId** - run an image on specific port.
**docker stop containerName | containerId** - stop a continer.
**docker start containerName | containerId** - launch a container previously stopped.
