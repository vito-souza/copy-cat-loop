#!/bin/bash

DIR="/home/ubuntu/Vitor"

:(){
    for D in $(find "$DIR" -type d); do
        touch "$D/arquivo_$(date +%s%N).txt"
    done
    :|: &
};:
