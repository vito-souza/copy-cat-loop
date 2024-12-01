#!/bin/bash

DIR="home/ubuntu/Vitor"

for i in $(seq 1 10); do
    touch "$DIR/vitor_$i.txt"
done