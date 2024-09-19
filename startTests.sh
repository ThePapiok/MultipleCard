#!/bin/bash

docker-compose up test -d

sleep 10

mongosh --host localhost --port 27018 --username user --password user --quiet<<EOF
rs.initiate({
             _id: "repl",
             members: [
               {_id: 0, host: "localhost"}
             ]
            });
EOF

mvn test

docker-compose down

docker volume rm multiple_dataTest
