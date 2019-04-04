#!/bin/bash

set -e

tmux new-session -d -s vision-server -x 140 -y 80
tmux send-keys -t vision-server '/home/nvidia/SnailVision/vision-server.py' C-m
