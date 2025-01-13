#!/bin/bash

docker-compose up test -d

until mongosh --host localhost --port 27018 --username user --password user --quiet --eval "db.runCommand({ ping: 1 })" > /dev/null 2>&1; do
    echo "Oczekiwanie na mongoDB..."
    sleep 1
done

mongosh --host localhost --port 27018 --username user --password user --quiet<<EOF
rs.initiate({
             _id: "repl",
             members: [
               {_id: 0, host: "localhost"}
             ]
            });
EOF

mvn test

docker-compose down test

docker volume rm multiple_dataTest
