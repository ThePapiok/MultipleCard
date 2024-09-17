#!/bin/bash

docker-compose up db -d

sleep 10

mongosh --host localhost --port 27018 --username user --password user --quiet<<EOF
rs.initiate();
EOF