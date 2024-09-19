FROM mongo:latest

COPY mongo-key /data/configdb/mongo-key

RUN chmod 600 /data/configdb/mongo-key

CMD ["mongod", "--replSet", "repl", "--bind_ip_all", "--keyFile", "/data/configdb/mongo-key"]


