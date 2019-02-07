#!/bin/bash
gst-launch-1.0 videotestsrc ! x264enc ! rtph264pay config-interval=10 pt=96 ! udpsink host=192.168.1.18 port=5000
