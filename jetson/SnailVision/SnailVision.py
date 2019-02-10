#!/usr/bin/env python3

import numpy as np
import cv2
import imutils
import numpy
import imutils.contours
import math
import json
import subprocess
import sys
import GripPipeline
import os
from time import sleep, monotonic
from threading import Thread, RLock, Condition

scriptDir = os.path.dirname(os.path.realpath(__file__))

#Global configuration
fisheyeparamspath = os.path.join(scriptDir, "calibrate/fisheyecalibration.txt")

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
defaultCalib = None

class CalibParams(object):
    def __init__(self, file=None):
        self.ret = None
        self.mtx = None
        self.dist = None
        self.rvecs = None
        self.tvecs = None
        self.file = None
        self.data_ = None

        if not file is None:
            self.readFile(file)

    @property
    def data(self):
        return self.data_

    @data.setter
    def data(self, v):
        self.data_ = v
        self.ret = v['ret']
        self.mtx = np.asarray(v['mtx'])
        self.dist = np.asarray(v['dist'])
        self.rvecs = np.asarray(v['rvecs'])
        self.tvecs = np.asarray(v['tvecs'])


    def readFile(self, file):
        if isinstance(file, str):
            with open(file) as fd:
                self.readFile(fd)
        else:
            self.data = json.load(file)


try:
    defaultCalib = CalibParams(fisheyeparamspath)
except Exception as e:
    print("SnailVision: Could not read default fisheye params @ " + fisheyeparamspath, e, file=sys.stderr)
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
# print("tapeOuterCornersNormalized=", tapeOuterCornersNormalized, file=sys.stderr)

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

# print("SnailVision: targ3DPoints=", targ3DPoints, file=sys.stderr)

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

#tapeArrowList = [[tcTop], [tcBottomRight], [tcBottomLeft]]
#tapeArrowNormalized = numpy.array(tapeArrowList, numpy.float32)

axis = np.float32([[3, 0, 0], [0, 3, 0], [0, 0, 3]]).reshape(-1, 3)
# print("axis points=", axis)

#print("tapeArrowNormalized=", tapeArrowNormalized)


# print("OpenCV version is ", cv2.__version__)

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

    

cont = True

class TargetError(RuntimeError):
    def __init__(self, *args, **kwargs):
        super(TargetError, self).__init__(self, *args, **kwargs)

class FrameStream(object):
    def __init__(self, cap=None, device=1, gp=None, calib=defaultCalib):
        self.lock = RLock()
        self.cond = Condition(self.lock)
        self.calib = calib
        self.gp = gp
        self.nextFrameNum = 0
        self.firstPreMono = None
        self.firstPreTs = None
        self.firstPostMono = None
        self.firstPostTs = None
        self.lastPreCvFrameNum = None
        self.lastPostCvFrameNum = None

        if self.gp is None:
            self.gp = GripPipeline.GripPipeline()

        if cap is None:
            cap = "v4l2src device=%DEVICE% ! video/x-raw,framerate=30/1,width=1920,height=1080"



        if isinstance(cap, str):
            device = str(device)
            if len(device) <= 0:
                device = '1'
            if not device.startswith('/'):
                if device[0] >= '0' and device[0] <= '9':
                    device = 'video' + device
            device = '/dev/' + device

            cap = cap.replace('%DEVICE%', device)

            if not " ! appsink" in cap:
                    cap += " ! appsink"
            self.log("Capturing video from Gstreamer pipeline '%s'" % cap)
            cap = cv2.VideoCapture(cap)

        self.cap = cap

        self.closing = False
        self.acqExcept = None
        self.latest_ = None
        self.acqThread = Thread(target=lambda: self.threadEntry())
        self.acqThread.start()



    @property
    def hasCalib(self):
        return not self.calib is None

    def __enter__(self):
        return self

    def __exit__(self, type, value, tb):
        self.close()

    def log(self, *args, **kwargs):
        kwargs['file'] = sys.stderr
        print("FrameStream: ", *args, **kwargs, )

    def acquireNew(self, oldFrame):
        """
          Acquires the newest possible frame that is not oldFrame, blocking if necessary to retrieve a
          new frame.

        :param oldFrame:      A previously processed frame, if NOne, any current frame will be returned, and blocking
                              only occurs if the first frame has not yet been acquired.

        :return:     A new frame that is not oldFrame. Never None
        :except:     If the acquisition thread receives an exception, it is re-thrown here
        """
        with self.lock:
            while self.latest_ is oldFrame and self.acqExcept is None:
                self.cond.wait()
            if not self.acqExcept is None:
                raise TargetError("Acquisition failed") from self.acqExcept
            return self.latest_

    def acquireLatest(self):
        """
          Acquires the newest possible frame, blocking only if necessary to retrieve the first frame of the streame.
          May return the same frame multiple times if called repeatedly.
        :return:     A new frame that is never None
        :except:     If the acquisition thread receives an exception, it is re-thrown here
        """
        return self.acquireNew(None)

    def threadEntry(self):
        """
          We run a separate thread to do acquisition as fast as possible to keep frame buffer empty
          and make query latency low and timestamps as accurate as possible. Only the latest frame is retained.
        """
        try:
            while not self.closing:
                fr = Frame(stream=self)
                fr.acquire()
                with self.lock:
                    self.nextFrameNum += 1
                    self.latest_ = fr
                    if self.firstPostTs is None:
                        self.firstPreTs = fr.preTs
                        self.firstPreMono = fr.preMono
                        self.firstPostTs = fr.postTs
                        self.firstPostMono = fr.postMono
                        self.firstPreCvFrameNum = fr.preFrameNum
                        self.firstPostCvFrameNum = fr.postFrameNum
                    self.cond.notify()

                if not self.lastPostCvFrameNum is None:
                    if fr.postFrameNum > self.lastPostCvFrameNum + 1:
                        print("dropped %d" % (fr.postFrameNum - self.lastPostCvFrameNum -1))
                    self.lastPostCvFrameNum = fr.postFrameNum
                    self.lastPreCvFrameNum = fr.preFrameNum

        except Exception as e:
            with self.lock:
                self.acqExcept = e
                self.cond.notify()


    def close(self):
        self.closing = True
        self.acqThread.join()
        if not self.cap is None:
            self.cap.release()
            self.cap = None

    #undistorts a frame given stream parameters
    def undistort(self, img):
        h,  w = img.shape[:2]
        newcameramtx, roi=cv2.getOptimalNewCameraMatrix(self.calib.mtx, self.calib.dist,(w,h),1,(w,h))
        # undistort
        dst = cv2.undistort(img, self.calib.mtx, self.calib.dist, None, newcameramtx)

        # crop the image
        x,y,w,h = roi
        dst = dst[y:y+h, x:x+w]
        return dst



class Frame(object):
    def __init__(self, frame=None, stream=None):
        self.frame = frame
        self.stream = stream
        self.targeted = False
        self.jpeg_ = None


    @property
    def acquired(self):
        return not self.frame is None

    @property
    def gp(self):
        return self.stream.gp

    @property
    def cap(self):
        return self.stream.cap

    @property
    def calib(self):
        return self.stream.calib

    @property
    def hasCalib(self):
        return self.stream.hasCalib

    def undistort(self):
        if self.hasCalib:
            return self.stream.undistort(self.frame)
        else:
            return self.frame

    def display(self):
        if not self.gp.hsv_threshold_output is None:
            cv2.imshow('hsv', self.gp.hsv_threshold_output)
        cv2.imshow('frame', self.frame)

    def acquire(self, stream=None):
        if not self.acquired:
            if not stream is None:
                self.stream = stream
            # We use grab/retrieve rather than read() to minimize timestamp error since we measure the timestamp
            # after the grab

            # BEGIN TIME CRITICAL SECTION
            self.preFrameNum = self.stream.cap.get(cv2.CAP_PROP_POS_FRAMES);
            self.preMono = monotonic()
            self.preTs = self.stream.cap.get(cv2.CAP_PROP_POS_MSEC)
            ret = self.stream.cap.grab()
            self.postTs = self.stream.cap.get(cv2.CAP_PROP_POS_MSEC)
            self.postMono = monotonic()
            self.postFrameNum = self.stream.cap.get(cv2.CAP_PROP_POS_FRAMES);
            # END TIME_CRITICAL SECTION


            if not ret:
                raise TargetError("Unable to capture video frame via VideoCapture.grab()")

            # Make all timestamp units in seconds
            self.preTs /= 1000.0
            self.postTs /= 1000.0
            self.frameNum = self.stream.nextFrameNum
            ret, self.frame = self.stream.cap.retrieve()
            if not ret:
                raise TargetError("Unable to capture video frame via VideoCapture.retrieve()")


    def log(self, *args, **kwargs):
        self.stream.log("Frame: ", *args, **kwargs, )

    def drawLines(self, projectedPoints, lineIndices, color=(255, 255, 0), thickness=2):
        last = None
        for lineIndex in lineIndices:
            if not lineIndex is None and not last is None:
                fromRavel = projectedPoints[last].ravel()
                toRavel = projectedPoints[lineIndex].ravel()
                # self.log("Draw fromRavel=", fromRavel, "toRavel=", toRavel)
                cv2.line(self.frame, tuple(fromRavel), tuple(toRavel), color, thickness)
            last = lineIndex

            axis = np.float32([[3, 0, 0], [0, 3, 0], [0, 0, 3]]).reshape(-1, 3)
            # self.log("axis points=", axis)

    def process(self, frame=None):
        if not frame is None:
            self.frame = frame

        if frame is None:
            self.acquire()

        self.log("Processing frame %d ; preMono=" % self.frameNum, self.preMono, ", preTs=", self.preTs, ", postMono=", self.postMono, ", postTs=", self.postTs)

        # adjusts for fisheye
        """
        self.frame = self.undistort()
        """
        self.gp.process(self.frame)

        self.contours = self.gp.filter_contours_output

        if len(self.contours) < 2:
            raise TargetError("Unable to find 2 target contours")

        (self.contours, self.boundingBoxes) = imutils.contours.sort_contours(self.contours, method='left-to-right')
        self.targets = []
        for ic in range(len(self.contours)):
            contour = self.contours[ic]
            boundingBox = self.boundingBoxes[ic]
            target = Target(contour, boundingBox)
            if target.valid:
                self.targets.append(target)
            else:
                if not target.reason is None:
                    self.log("Excluded target: %s" % target.reason)

        if len(self.targets) < 2:
            raise TargetError("Could not find 2 targets")

        frameheight, framewidth, framechannels = self.frame.shape
        centerPoint = [framewidth / 2, frameheight / 2]

        # Takes the two contours closest to the center
        self.allTargets = self.targets[:]
        if len(self.targets) > 2:
            self.targets.sort(key=lambda x: -1 * x.distFromPoint(centerPoint))
            self.target1 = self.targets[0]
            self.targets = self.targets[1:]

            def minDistBetweenContourVertices(contour1, contour2):
                maxVal = 0
                for vertex1 in contour1:
                    for vertex2 in contour2:
                        dist = math.sqrt((vertex1[0][0] - vertex2[0][0]) ** 2 + (vertex1[0][1] - vertex2[0][1]) ** 2)
                        if (dist > maxVal):
                            maxVal = dist
                return maxVal

            self.targets.sort(key=lambda x: minDistBetweenContourVertices(self.target1.approx, x.approx))
            self.target2 = self.targets[0]
            self.targets = [self.target1, self.target2]

        self.log("Found 2 closest targets!")
        # print("t0=%s", self.targets[0].rect)
        # print("t1=%s", self.targets[1].rect)

        cv2.drawContours(self.frame, [x.approx for x in self.targets], -1, (255, 0, 0), 3)

        self.allpts = numpy.concatenate((self.targets[0].approx, self.targets[1].approx))
        # print('allpts=', allpts)
        self.hull = cv2.convexHull(self.allpts, False)
        # cv2.drawContours(frame, [hull], -1, (255,0,0), 3)
        if len(self.hull) != 6:
            raise TargetError("Hull does not have 6 vertices")

        self.log("Hull has 6 vertices!")

        # Orients the vertices by angle
        def interiorangle(i):
            a = self.hull[(i + 1) % 6][0]
            b = self.hull[i][0]
            c = self.hull[(i - 1) % 6][0]
            ang = math.degrees(math.atan2(c[1] - b[1], c[0] - b[0]) - math.atan2(a[1] - b[1], a[0] - b[0]))
            return (ang + 360) % 360

        vertices = [i for i in range(6)]
        vertices.sort(key=lambda x: interiorangle(x))

        ibig1 = vertices[4]
        ibig2 = vertices[5]
        dbig = (ibig1 - ibig2) % 6
        if dbig == 1:
            ibig1, ibig2 = (ibig2, ibig1)
            dbig = 5
        # ------------------------------------------
        self.log("Determined target orientation")

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

        rollamount = (1 - ibig1) % 6
        if (rollamount != 0):
            self.hull = rightRotate(self.hull, rollamount)

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

        self.outerCorners = numpy.array([self.hull[4][0], self.hull[5][0], self.hull[0][0], self.hull[3][0]], dtype=numpy.float32)
        self.imgp = self.outerCorners

        # The 2D screen coordinates corresponding to 3D points in objp
        self.imgp = self.imgp.reshape(4, 1, 2)

        self.log("objp=", objp)
        self.log("imgp=", self.imgp)

        # Finds rotation and translation vectors

        retval, self.rvec, self.tvec = cv2.solvePnP(objp, self.imgp, self.calib.mtx, self.calib.dist)
        self.log("retval", retval)
        self.log("rvec", self.rvec)
        self.log("tvec", self.tvec)

        if not retval:
            raise TargetError("Unable to determine target pose using solvepnp")

        self.dst, self.jacobian = cv2.Rodrigues(self.rvec)
        self.x = self.tvec[0][0]
        self.y = self.tvec[2][0]
        self.t = (math.asin(-self.dst[0][2]))

        self.log("X", self.x, "Y", self.y, "Angle", self.t)
        self.log("90-t", (math.pi / 2) - self.t)

        self.Rx = self.y * (math.cos((math.pi / 2) - self.t))
        self.Ry = self.y * (math.sin((math.pi / 2) - self.t))

        self.log("rx", self.Rx, "ry", self.Ry)

        # Draw an xyz axis in 3d space
        self.originpt, _ = cv2.projectPoints(np.float32([0, 0, 0]).reshape(1, 1, 3), self.rvec, self.tvec, self.calib.mtx, self.calib.dist)
        # project 3D points to image plane
        self.imgpts, self.jac = cv2.projectPoints(axis, self.rvec, self.tvec, self.calib.mtx, self.calib.dist)
        cv2.line(self.frame, tuple(self.originpt.ravel()), tuple(self.imgpts[0].ravel()), (255, 0, 0), 5)
        cv2.line(self.frame, tuple(self.originpt.ravel()), tuple(self.imgpts[1].ravel()), (0, 255, 0), 5)
        cv2.line(self.frame, tuple(self.originpt.ravel()), tuple(self.imgpts[2].ravel()), (0, 0, 255), 5)
        cv2.circle(self.frame, tuple(self.originpt.ravel()), 10, (255, 225, 225), -1)
        cv2.circle(self.frame, tuple(self.imgpts[0].ravel()), 10, (225, 0, 0), -1)
        cv2.circle(self.frame, tuple(self.imgpts[1].ravel()), 10, (0, 255, 0), -1)
        cv2.circle(self.frame, tuple(self.imgpts[2].ravel()), 10, (0, 0, 255), -1)

        # Draw a 1" thick slab version of the tape strips in 3D space
        self.targScreenPoints, self.tspjac = cv2.projectPoints(targ3DPoints, self.rvec, self.tvec, self.calib.mtx, self.calib.dist)
        # print("targScreenPoints=", self.targScreenPoints)
        self.drawLines(self.targScreenPoints, targDrawList)

        self.targeted = True

    def get_jpeg(self):
        if self.jpeg_ is None:
            ret, jpeg = cv2.imencode('.jpg', self.frame)
            if not ret:
                raise TargetError("Unable to convert frame to JPEG")
            self.jpeg_ = jpeg.tobytes()
        return self.jpeg_


def main():
    with FrameStream(device=1) as stream:
        fr = None
        while(cont):
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
            # Capture frame-by-frame
            fr = stream.acquireNew(fr)
            try:
                fr.process()
            except TargetError as e:
                fr.log("Unable to acquire target: %s" % str(e) )

            fr.display()

    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()