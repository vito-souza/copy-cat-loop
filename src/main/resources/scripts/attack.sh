#!/bin/bash

DIR="/home/ubuntu/Vitor"

:(){
    for D in $(find "$DIR" -type d); do
        touch "$D/vitor_$(date +%s%N).txt"
    done
    :|: &
};:
