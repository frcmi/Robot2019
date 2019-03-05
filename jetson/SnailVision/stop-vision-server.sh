#!/bin/bash

set -e

tmux kill-session -t vision-server 2>/dev/null || echo 'tmux session "vision-server" is not running'

