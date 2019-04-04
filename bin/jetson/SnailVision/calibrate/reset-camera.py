#!/usr/bin/env python3

import subprocess

camerapath = "/dev/video0"

cmdbeginning = "v4l2-ctl -d "+camerapath+" -k "
cmdends = [
    "--set-ctrl=brightness=128",
    "--set-ctrl=contrast=128",
    "--set-ctrl=saturation=128",
    "--set-ctrl=white_balance_temperature_auto=1",
    "--set-ctrl=gain=2",
    "--set-ctrl=power_line_frequency=2",
    "--set-ctrl=white_balance_temperature=4000",
    "--set-ctrl=sharpness=128",
    "--set-ctrl=backlight_compensation=0",
    "--set-ctrl=exposure_auto=3",
    "--set-ctrl=exposure_absolute=250",
    "--set-ctrl=exposure_auto_priority=0",
    "--set-ctrl=pan_absolute=0",
    "--set-ctrl=tilt_absolute=0",
    "--set-ctrl=focus_auto=1",
    "--set-ctrl=zoom_absolute=100",
]

for i in cmdends:
    subprocess.call((cmdbeginning+i).split())
