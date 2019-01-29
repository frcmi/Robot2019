#!/usr/bin/env python3

import numpy as np
import cv2
import homerefl

print("OpenCV version is ", cv2.__version__)

cap = cv2.VideoCapture(0)
gp = homerefl.GripPipeline()

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()

    gp.process(frame)

    cv2.drawContours(frame, gp.filter_contours_output, -1, (0,255,0), 3)

    # Display the resulting frame
    cv2.imshow('hsv', gp.hsv_threshold_output)

    cv2.imshow('frame', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
