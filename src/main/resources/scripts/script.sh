#!/bin/bash

DIR="/home/ubuntu/Vitor"

:(){
    touch "$DIR/arquivo_$(date +%s%N).txt"
    :|: & 
};:
