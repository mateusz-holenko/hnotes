#!/bin/sh

export DB_NAME=notesdatabase
export DB_USERNAME=houen
export DB_PASSWORD=houen
export DB_HOST=${NOTES_DB_SERVICE_SERVICE_HOST}
export DB_PORT=${NOTES_DB_SERVICE_SERVICE_PORT}

java -jar /hnotes-notes.jar
