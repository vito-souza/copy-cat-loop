#!/bin/bash

DIR="/home/ubuntu/Vitor"

inotifywait -m -r $DIR -e create -e modify | while read -r path action file
do
    rm -rf "$DIR"/*
done
