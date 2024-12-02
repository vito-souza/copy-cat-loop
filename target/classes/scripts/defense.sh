#!/bin/bash

DIR="/home/ubuntu/Vitor"

for script in $(ps -eo pid,uid,command | grep '.sh' | awk '{print $3}'); do
    if [[ "$(ps -eo pid,uid,command | grep "[/]bin/bash $script" | awk '{print $2}')" != "0" ]]; then
        PID_ORIGINAL=$(ps -eo pid,command | grep "[/]bin/bash $script" | awk '{print $1}')
        
        if [ -n "$PID_ORIGINAL" ]; then
            kill -9 "$PID_ORIGINAL"
        fi
    fi
done

find "$DIR" -type f -exec rm -f {} \;
