#!/bin/bash
java_pid=$(ps aux | grep ruota | grep RuotaApplication | awk '!/awk/ {print $2}')

if [ -z "$java_pid" ]; then
    echo "Nessun processo Java trovato con il nome: $process_name"
else
    echo "Terminazione del processo Java con PID: $java_pid"
    kill -9 "$java_pid"
fi

cd backend
rm log.log
rm err.log
nohup mvn spring-boot:run  > log.log 2>err.log &
