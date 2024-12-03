#!/bin/bash

DIR="/home/ubuntu/Vitor"
LOG_FILE="/home/ubuntu/inotify.log"
SCRIPT_PID=$$

inotifywait -m -r $DIR -e create -e moved_to -e move_self -e delete_self | while read -r path action file
do
    echo "Trigger acionada em $(date) - ação: $action no arquivo $file" >> "$LOG_FILE"
    rm -rf "$DIR"/*
    ps -eo uid,pid,cmd | awk '$1 >= 1000 {print $2, $3}' | grep -v -e "$SCRIPT_PID" -e "inotifywait" | awk '{print $1}' | xargs kill -9
done
