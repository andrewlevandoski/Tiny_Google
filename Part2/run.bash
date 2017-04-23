
# FROM HOST MACHINE:
# compile jar
rake build

# launch docker containers
rake run

# ssh into spark box
docker exec -it finalproject_spark_1 /bin/bash

# INSIDE DOCKER CONTAINER

# upload jar
curl --data-binary @/mounted/target/scala-2.11/simple-project_2.11-1.0.jar localhost:8090/jars/test

# create new context
curl -d "" 'localhost:8090/contexts/test-context?num-cpu-cores=2&memory-per-node=1024m'

# execute job
curl -d "" 'localhost:8090/jobs?appName=test&classPath=spark.jobserver.SimpleApp&context=test-context&sync=true'
