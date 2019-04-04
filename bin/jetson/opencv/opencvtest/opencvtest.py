#!/usr/bin/env python3

import numpy as np
import cv2
import imutils
import numpy
import imutils.contours
import math


tapeAngleDegrees=14.5
tapeAngle = tapeAngleDegrees / 180.0 * math.pi  # angle of each piece of tape from verticle
tapeGapInches = 8.0      # space between closest point of two pieces of tape
tapeWidthInches = 2.0
tapeLengthInches = 5.5

# Compute normalized coordinates of tape corners in inches with (0,0) at the center
sinTapeAngle = math.sin(tapeAngle)
cosTapeAngle = math.cos(tapeAngle)
tapeVertShiftInches = tapeWidthInches * sinTapeAngle
tapeHorizShiftInches = tapeLengthInches * sinTapeAngle
tapeRotW = tapeWidthInches * cosTapeAngle
tapeRotH = tapeLengthInches * cosTapeAngle

tapeBoundingHeight =  tapeRotH + tapeVertShiftInches
tapeBoundingWidth = tapeRotW + tapeHorizShiftInches

trTopLeft = (tapeGapInches/2.0, tapeBoundingHeight/2.0 - tapeVertShiftInches)
trBottomLeft = (trTopLeft[0] + tapeHorizShiftInches, trTopLeft[1] - tapeRotH)
trTopRight = (trTopLeft[0] + tapeRotW, trTopLeft[0] + tapeVertShiftInches)
trBottomRight = (trBottomLeft[0] + tapeRotW, trBottomLeft[1] + tapeVertShiftInches)

def tapeRightToLeft(x):
    return (-x[0], x[1])

tlTopLeft = tapeRightToLeft(trTopRight)
tlBottomLeft = tapeRightToLeft(trBottomRight)
tlTopRight = tapeRightToLeft(trTopLeft)
tlBottomRight = tapeRightToLeft(trBottomLeft)

tcTop = (0.0, 0)
tcBottomLeft = (-2.0, -tapeBoundingHeight/2.0)
tcBottomRight = (2.0, -tapeBoundingHeight/2.0)

tapeRightContour = [trTopLeft, trTopRight, trBottomRight, trBottomLeft]
tapeLeftContour = [trTopLeft, trTopRight, trBottomRight, trBottomLeft]


tapeOuterCornersList = [tlTopLeft, trTopRight, trBottomRight, tlBottomLeft]
tapeOuterCornersNormalized = numpy.array(tapeOuterCornersList, dtype=numpy.float32)

print("tapeOuterCornersNormalized=", tapeOuterCornersNormalized)

tapeArrowList = [[tcTop], [tcBottomRight], [tcBottomLeft]]
tapeArrowNormalized = numpy.array(tapeArrowList, numpy.float32)

print("tapeArrowNormalized=", tapeArrowNormalized)

import GripPipeline

print("OpenCV version is ", cv2.__version__)

cap = cv2.VideoCapture("v4l2src device=/dev/video1 ! video/x-raw,framerate=30/1,width=1920,height=1080 ! appsink")
gp = GripPipeline.GripPipeline()

class Target(object):
    def __init__(self, contour, boundingBox):
        self.contour0 = contour
        self.boundingRect0 = boundingBox
        self.perimeter0 = cv2.arcLength(self.contour0, True)
        epsilon = 0.10 * self.perimeter0
        self.approx = cv2.approxPolyDP(self.contour0, epsilon, True)
        self.valid = True
        self.reason = None
        if len(self.approx) != 4:
            self.valid = False
            self.reason = "Wrong number of approx vertices: %d" % len(self.approx)

    @property
    def minx(self):
        return self.boundingRect0[0]

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()

    gp.process(frame)

    contours = gp.filter_contours_output

    cv2.drawContours(frame, contours, -1, (255,0,0), 3)

    if len(contours) < 2:
        print("Could not find 2 potential target contours")
    else:
        (contours, boundingBoxes) = imutils.contours.sort_contours(contours, method='left-to-right')
        targets = []
        for ic in range(len(contours)):
            contour = contours[ic]
            boundingBox = boundingBoxes[ic]
            target = Target(contour, boundingBox)
            if target.valid:
                targets.append(target)
            else:
                if not target.reason is None:
                    print("Excluded target: %s" % target.reason)

        cv2.drawContours(frame, [x.approx for x in targets], -1, (255,255,0), 3)

        if len(targets) == 2:
            print("Found exactly 2 targets!")
            # print("t0=%s", targets[0].approx)
            # print("t1=%s", targets[1].approx)
            allpts = numpy.concatenate((targets[0].approx, targets[1].approx))
            # print('allpts=', allpts)
            hull = cv2.convexHull(allpts, False)
            cv2.drawContours(frame, [hull], -1, (255,0,0), 3)
            if len(hull) != 6:
                print("Hull does not have 6 vertices :(")
            else:
                print("Hull has 6 vertices!")
                print("hull=", hull)
                # Hull is in counterclockwise order (X is to right, Y is down). Sort the segments by angle

                # interior angle in radians around vertex
                def interiorangle(i):
                    a = hull[(i+1)%6][0]
                    b = hull[i][0]
                    c = hull[(i-1)%6][0]
                    ang = math.degrees(math.atan2(c[1]-b[1], c[0]-b[0]) - math.atan2(a[1]-b[1], a[0]-b[0]))
                    print "angle for vertex " + str(i) + " is " + str(ang % 360)
                    return (ang+360) % 360

                vertices = [i for i in range(6)]

                vertices.sort(key=lambda x: interiorangle(x))

                ibig1 = vertices[4]
                ibig2 = vertices[5]
                print "ibig1="+str(ibig1)
                print "ibig2="+str(ibig2)
                dbig = (ibig1 - ibig2) % 6
                if dbig == 1:
                    ibig1, ibig2 = (ibig2, ibig1)
                    dbig = 5
                if dbig == 5:
                    # hull[ibig1] is the bottom right of the right tape strip
                    print("Determined target orientation")

                    # roll the convex hull matrix so that the bottom-right of the right strip is first
                    # we want the origin to be one vertex clockwise from ibig1, so ibig1 = 1
                    def rightRotate(lists, num): 
                        output_list = [] 
      
                        # Will add values from n to the new list 
                        for item in range(len(lists) - num, len(lists)): 
                            output_list.append(lists[item]) 
      
                        # Will add the values before 
                        # n to the end of new list     
                        for item in range(0, len(lists) - num):  
                            output_list.append(lists[item]) 
          
                        return output_list 

                    rollamount = (1-ibig1)%6
                    if (rollamount != 0):
                        hull = rightRotate(hull, rollamount)
                        print("Rolling by " + str(rollamount))
                    print hull;

                    cv2.circle(frame, (hull[0][0][0], hull[0][0][1]), 10, (0,255,0), -1)
                    outerCorners = numpy.array([hull[4][0], hull[5][0], hull[0][0], hull[3][0]], dtype=numpy.float32)

                    print("outerCorners=", outerCorners)
                    print("outerCornersNormalized=", tapeOuterCornersNormalized)

                    img2Normal = cv2.getPerspectiveTransform(outerCorners, tapeOuterCornersNormalized)
                    print("img2Normal=", img2Normal)
                    normal2Img = cv2.getPerspectiveTransform(tapeOuterCornersNormalized, outerCorners)
                    # normal2Img = cv2.invert(img2Normal)
                    print("normal2Img=", normal2Img)
                    tapeArrow = cv2.perspectiveTransform(tapeArrowNormalized, normal2Img)
                    print("tapeArrow=",tapeArrow)
                    tapeArrowCnt = numpy.array([(int(x[0][0]+0.5), int(x[0][1]+0.5)) for x in tapeArrow], dtype=numpy.int32)
                    print("tapeArrowCnt=",tapeArrowCnt)

                    cv2.drawContours(frame, [tapeArrowCnt], -1, (0, 255, 0), 3)

                else:
                    print("Two shortest segments of hull are not in correct position")

            # cv2.drawContours(frame, [hull], -1, (255, 0, 0), 3)

        else:
            print("Wrong number of matching targets: %d" % len(targets))


    # Display the resulting frame
    cv2.imshow('hsv', gp.hsv_threshold_output)

    cv2.imshow('frame', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break



# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
