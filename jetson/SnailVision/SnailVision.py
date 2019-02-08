#!/usr/bin/env python3

import numpy as np
import cv2
import imutils
import numpy
import imutils.contours
import math
import json
import subprocess

import GripPipeline

#Global configuration
fisheyeparamspath = "calibrate/fisheyecalibration.txt"
camerapath = "/dev/video1"

#Camera configuration
exposureTime = 41
contrast = 128
saturation = 128
sharpness = 128
gain = 0
brightness = 107
white_balance_temperature = 5850

cmdbeginning = "v4l2-ctl -d "+camerapath+" -k "
cmdends = [
    "--set-ctrl=exposure_auto=1",
    "--set-ctrl=exposure_auto_priority=1",
    "--set-ctrl=exposure_absolute="+str(exposureTime),
    "--set-ctrl=contrast="+str(contrast),
    "--set-ctrl=saturation="+str(saturation),
    "--set-ctrl=sharpness="+str(sharpness),
    "--set-ctrl=gain=0",
    "--set-ctrl=brightness="+str(brightness),
    "--set-ctrl=white_balance_temperature_auto=0",
    "--set-ctrl=white_balance_temperature="+str(white_balance_temperature),
    "--set-ctrl=power_line_frequency=2",
    "--set-ctrl=backlight_compensation=1",
    "--set-ctrl=focus_auto=0",
    "--set-ctrl=focus_absolute=0"
]

for i in cmdends:
    subprocess.call((cmdbeginning+i).split())

# Reads fisheye configuration
noFisheye = False

try:
    with open(fisheyeparamspath) as json_file:
        data = json.load(json_file)
        global ret
        global mtx
        global dist
        global rvecs
        global tvecs
        ret=data['ret']
        mtx = np.asarray(data['mtx'])
        dist = np.asarray(data['dist'])
        rvecs = np.asarray(data['rvecs'])
        tvecs = np.asarray(data['tvecs'])
except Exception as e:
    print("Could not read fisheye params @ " + fisheyeparamspath, e)
    noFisheye = True

#Calculations for locations of vertices on the board
tapeAngleDegrees=14.5
tapeAngle = (tapeAngleDegrees / 180.0) * math.pi  # angle of each piece of tape from vertical
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
trTopRight = (trTopLeft[0] + tapeRotW, trTopLeft[1] + tapeVertShiftInches)
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
tapeLeftContour = [tlTopLeft, tlTopRight, tlBottomRight, tlBottomLeft]


tapeOuterCornersList = [tlTopLeft, trTopRight, trBottomRight, tlBottomLeft]
# tapeOuterCornersNormalized = numpy.array(tapeOuterCornersList, dtype=numpy.float32)
# print("tapeOuterCornersNormalized=", tapeOuterCornersNormalized)

def target2DTuplesTo3DNumpy(pts):
    nm = numpy.array(pts, dtype=numpy.float32)
    op = np.concatenate((nm, np.zeros((len(pts), 1), dtype=nm.dtype)), axis=1)
    op = op.reshape(-1, 1, 3)
    return op


# The 3D coordinates of the target in the target coordinate system are just the 2D coordinates
# with Z values all set to 0.0
objp = target2DTuplesTo3DNumpy(tapeOuterCornersList)

ITLTOPLEFT = 0
ITLTOPRIGHT = 1
ITLBOTTOMRIGHT = 2
ITLBOTTOMLEFT = 3
ITRTOPLEFT = 4
ITRTOPRIGHT = 5
ITRBOTTOMRIGHT = 6
ITRBOTTOMLEFT = 7

XITLTOPLEFT = 8
XITLTOPRIGHT = 9
XITLBOTTOMRIGHT = 10
XITLBOTTOMLEFT = 11
XITRTOPLEFT = 12
XITRTOPRIGHT = 13
XITRBOTTOMRIGHT = 14
XITRBOTTOMLEFT = 15


targ2DPoints = [tlTopLeft, tlTopRight, tlBottomRight, tlBottomLeft, trTopLeft, trTopRight,
                  trBottomRight, trBottomLeft]
targ3DFlatPoints = target2DTuplesTo3DNumpy(targ2DPoints)
targ3DRaisedPoints = np.copy(targ3DFlatPoints)
for i in range(len(targ3DRaisedPoints)):
    targ3DRaisedPoints[i][0][2] += 1.0

targ3DPoints = numpy.concatenate((targ3DFlatPoints, targ3DRaisedPoints), axis=0).reshape(-1, 3)

print("targ3DPoints=", targ3DPoints)

targDrawList = [
    ITLTOPLEFT, ITLTOPRIGHT, ITLBOTTOMRIGHT, ITLBOTTOMLEFT, ITLTOPLEFT,
    XITLTOPLEFT, XITLTOPRIGHT, XITLBOTTOMRIGHT, XITLBOTTOMLEFT, XITLTOPLEFT, None,
    ITLTOPRIGHT, XITLTOPRIGHT, None,
    ITLBOTTOMRIGHT, XITLBOTTOMRIGHT, None,
    ITLBOTTOMLEFT, XITLBOTTOMLEFT, None,
    ITRTOPLEFT, ITRTOPRIGHT, ITRBOTTOMRIGHT, ITRBOTTOMLEFT, ITRTOPLEFT,
    XITRTOPLEFT, XITRTOPRIGHT, XITRBOTTOMRIGHT, XITRBOTTOMLEFT, XITRTOPLEFT, None,
    ITRTOPRIGHT, XITRTOPRIGHT, None,
    ITRBOTTOMRIGHT, XITRBOTTOMRIGHT, None,
    ITRBOTTOMLEFT, XITRBOTTOMLEFT, None,
]

def drawLines(frame, projectedPoints, lineIndices, color=(255, 255, 0), thickness=2):
    last = None
    for lineIndex in lineIndices:
        if not lineIndex is None and not last is None:
            fromRavel = projectedPoints[last].ravel()
            toRavel = projectedPoints[lineIndex].ravel()
            #print("Draw fromRavel=", fromRavel, "toRavel=", toRavel)
            cv2.line(frame, tuple(fromRavel), tuple(toRavel), color, thickness)
        last = lineIndex


#tapeArrowList = [[tcTop], [tcBottomRight], [tcBottomLeft]]
#tapeArrowNormalized = numpy.array(tapeArrowList, numpy.float32)

#print("tapeArrowNormalized=", tapeArrowNormalized)


print("OpenCV version is ", cv2.__version__)

cap = cv2.VideoCapture("v4l2src device="+camerapath+" ! video/x-raw,framerate=30/1,width=1920,height=1080 ! appsink")
gp = GripPipeline.GripPipeline()

class Target(object):
    def __init__(self, contour, boundingBox):
        self.contour0 = contour
        self.boundingRect0 = boundingBox
        self.perimeter0 = cv2.arcLength(self.contour0, True)
        epsilon = 0.05 * self.perimeter0
        self.approx = cv2.approxPolyDP(self.contour0, epsilon, True)
        #self.rect = cv2.boxPointsminAreaRect(self.contour0)
        self.valid = True
        self.reason = None
        if len(self.approx) != 4:
            self.valid = False
            self.reason = "Wrong number of approx vertices: %d" % len(self.approx)

    @property
    def minx(self):
        return self.boundingRect0[0]

    def distFromPoint(self, point):
        dist = cv2.pointPolygonTest(self.approx,(point[0],point[1]),True)
        return dist

    

#undistorts a frame given parameters
def undistort(img):
    h,  w = img.shape[:2]
    newcameramtx, roi=cv2.getOptimalNewCameraMatrix(mtx,dist,(w,h),1,(w,h))
    # undistort
    dst = cv2.undistort(img, mtx, dist, None, newcameramtx)

    # crop the image
    x,y,w,h = roi
    dst = dst[y:y+h, x:x+w]
    return dst

cont = True

# Display the resulting frame
def displayFrame(gp, frame):
    cv2.imshow('hsv', gp.hsv_threshold_output)

    cv2.imshow('frame', frame)


while(cont):
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
    # Capture frame-by-frame
    ret, frame = cap.read()
    
    #adjusts for fisheye
    """
    try:
        frame = undistort(frame)
    except:
        if not noFisheye:
            print("Could not undistort frame")
    """
    gp.process(frame)

    contours = gp.filter_contours_output

    if len(contours) < 2:
        print("Could not find 2 contours")
        displayFrame(gp, frame)
        continue

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

    if len(targets) < 2:
        print("Could not find 2 targets")
        displayFrame(gp, frame)
        continue

    frameheight, framewidth, framechannels = frame.shape
    centerPoint = [framewidth/2, frameheight/2]

    #Takes the two contours closest to the center
    if len(targets) > 2:
        targets.sort(key = lambda x: -1*x.distFromPoint(centerPoint))
        target1 = targets[0]
        targets = targets[1:]

        def minDistBetweenContourVertices(contour1, contour2):
            maxVal = 0
            for vertex1 in contour1:
                for vertex2 in contour2:
                    dist = math.sqrt((vertex1[0][0]-vertex2[0][0])**2 + (vertex1[0][1]-vertex2[0][1])**2)
                    if (dist > maxVal):
                        maxVal = dist
            return maxVal
        targets.sort(key = lambda x: minDistBetweenContourVertices(target1.approx, x.approx))
        targets = [target1, targets[0]]

    cv2.drawContours(frame, [x.approx for x in targets], -1, (255,0,0), 3)
        
    #Unindent to here
    print("Found exactly 2 targets!")
    # print("t0=%s", targets[0].rect)
    # print("t1=%s", targets[1].rect)
    allpts = numpy.concatenate((targets[0].approx, targets[1].approx))
    # print('allpts=', allpts)
    hull = cv2.convexHull(allpts, False)
    #cv2.drawContours(frame, [hull], -1, (255,0,0), 3)
    if len(hull) != 6:
        print("Hull does not have 6 vertices")
        displayFrame(gp, frame)
        continue
    
    print("Hull has 6 vertices!")

    # Orients the vertices by angle
    def interiorangle(i):
        a = hull[(i+1)%6][0]
        b = hull[i][0]
        c = hull[(i-1)%6][0]
        ang = math.degrees(math.atan2(c[1]-b[1], c[0]-b[0]) - math.atan2(a[1]-b[1], a[0]-b[0]))
        return (ang+360) % 360

    vertices = [i for i in range(6)]

    vertices.sort(key=lambda x: interiorangle(x))

    ibig1 = vertices[4]
    ibig2 = vertices[5]
    dbig = (ibig1 - ibig2) % 6
    if dbig == 1:
        ibig1, ibig2 = (ibig2, ibig1)
        dbig = 5
    # ------------------------------------------
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

    # Note coordinate system assumptions:
    #
    # Target-relative coordinate system:
    #
    #   +X ==> To the right looking at target from front
    #   +Y ==> Upwards from the ground
    #   +Z ==> Towards the front of the target (This is a right-handed coordinate system)
    #   (0,0,0) is the center of the flat target. Z=0 is the target plane. On the Y axis,
    #           0 is the midpoint between the lowest corners of tape and the highest corners
    #           of tape. On the X axis, 0 is the symetric midpoint between the left and right tapes.
    #           The target coordinate system units are in inches.
    #
    # Camera coordinate system:
    #
    #   +X ==> To the right from the camera's point of view
    #   +Y ==> Down from the camera's point of view
    #   +X ==> Toward the scene (Right-handed)
    #
    # Screen coordinate system:
    #
    #   +X ==> To the right from viewer's perspective
    #   +Y ==> Down from viewer's perspective
    #   Units are in pixels
    #   (0, 0) is top-left of screen

    outerCorners = numpy.array([hull[4][0], hull[5][0], hull[0][0], hull[3][0]], dtype=numpy.float32)
    imgp = outerCorners


    # The 2D screen coordinates corresponding to 3D points in objp
    imgp = imgp.reshape(4,1,2)

    print("objp=", objp)
    print("imgp=", imgp)

    #Finds rotation and translation vectors

    retval, rvec, tvec = cv2.solvePnP(objp, imgp, mtx, dist)
    print("retval", retval)
    print("rvec", rvec)
    print("tvec", tvec)

    dst, jacobian = cv2.Rodrigues(rvec)
    x = tvec[0][0]
    y = tvec[2][0]
    t = (math.asin(-dst[0][2]))

    print("X", x, "Y", y, "Angle", t)
    print("90-t", (math.pi/2) - t)

    Rx = y * (math.cos((math.pi/2) - t))
    Ry = y * (math.sin((math.pi/2) - t))

    print("rx", Rx, "ry", Ry)

    #Draw an xyz axis in 3d space
    originpt, _ = cv2.projectPoints(np.float32([0,0,0]).reshape(1,1,3), rvec, tvec, mtx, dist)
    axis = np.float32([[3,0,0], [0,3,0], [0,0,3]]).reshape(-1,3)
    #print("axis points=", axis)
    # project 3D points to image plane
    imgpts, jac = cv2.projectPoints(axis, rvec, tvec, mtx, dist)
    cv2.line(frame, tuple(originpt.ravel()), tuple(imgpts[0].ravel()), (255,0,0), 5)
    cv2.line(frame, tuple(originpt.ravel()), tuple(imgpts[1].ravel()), (0,255,0), 5)
    cv2.line(frame, tuple(originpt.ravel()), tuple(imgpts[2].ravel()), (0,0,255), 5)
    cv2.circle(frame, tuple(originpt.ravel()), 10, (255,225,225), -1)
    cv2.circle(frame, tuple(imgpts[0].ravel()), 10, (225,0,0), -1)
    cv2.circle(frame, tuple(imgpts[1].ravel()), 10, (0,255,0), -1)
    cv2.circle(frame, tuple(imgpts[2].ravel()), 10, (0,0,255), -1)

    # Draw a 1" thick slab version of the tape strips in 3D space
    targScreenPoints, tspjac = cv2.projectPoints(targ3DPoints, rvec, tvec, mtx, dist)
    #print("targScreenPoints=", targScreenPoints)
    try:
        drawLines(frame, targScreenPoints, targDrawList)
    except:
        print("could not drawLines, something is wrong")

    displayFrame(gp, frame)






# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
