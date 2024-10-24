#!/bin/bash

docker-compose up db -d

sleep 10

mongosh --host localhost --port 27017 --username user --password user --quiet<<EOF
rs.initiate({
             _id: "repl",
             members: [
               {_id: 0, host: "localhost"}
             ]
            });
EOF