#!/bin/bash

DIR="/home/ubuntu/Vitor"
LOG_FILE="/home/ubuntu/inotify.log"
SCRIPT_PID=$$

inotifywait -m -r $DIR -e create -e moved_to -e move_self -e delete_self | while read -r path action file
do
    rm -rf "$DIR"/*
    echo "Trigger acionada em $(date) - ação: $action no arquivo $file" >> "$LOG_FILE"
    # ps -eo uid,pid,cmd --sort=uid | awk '$1 >= 1000 {print $1, $2, $3}' | grep -vE '(/usr|/lib|/bin)'
done
