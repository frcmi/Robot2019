#!/usr/bin/env python3
import numpy as np
import cv2
import glob
import json

bw = 9
bh = 7

# termination criteria
criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 30, 0.001)

# prepare object points, like (0,0,0), (1,0,0), (2,0,0) ....,(6,5,0)
objp = np.zeros((bw*bh,3), np.float32)
objp[:,:2] = np.mgrid[0:bh,0:bw].T.reshape(-1,2)

# Arrays to store object points and image points from all the images.
objpoints = [] # 3d point in real world space
imgpoints = [] # 2d points in image plane.

images = glob.glob('images/*.jpg')

for fname in images:
    print("Processing %s" % fname)
    img = cv2.imread(fname)
    gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)

    # Find the chess board corners
    ret, corners = cv2.findChessboardCorners(gray, (bh,bw),None)

    # If found, add object points, image points (after refining them)
    if ret == True:
        objpoints.append(objp)

        corners2 = cv2.cornerSubPix(gray,corners,(11,11),(-1,-1),criteria)
        imgpoints.append(corners2)

        # Draw and display the corners
        img2 = cv2.drawChessboardCorners(img, (bh,bw), corners2,ret)
        cv2.imshow('img',img2)
        ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpoints, imgpoints, gray.shape[::-1],None,None)
        assert ret
        h,  w = img.shape[:2]
        newcameramtx, roi=cv2.getOptimalNewCameraMatrix(mtx,dist,(w,h),1,(w,h))
        dst = cv2.undistort(img, mtx, dist, None, newcameramtx)

        # crop the image
        if roi != (0, 0, 0, 0):
          x,y,w,h = roi
          dst = dst[y:y+h, x:x+w]
        else:
          print("No ROI found for %s" % fname)
        #cv2.imwrite('calibresult.png', dst)
        #cv2.imshow('corrected', dst)
        #cv2.waitKey(0)

    else:
        print("unable to locate corners in %f" % fname)

#cv2.destroyAllWindows()

ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpoints, imgpoints, gray.shape[::-1],None,None)

print("ret: ", ret)
print("mtx: ", mtx)
print("dist: ", dist)
print("rvecs: ", rvecs)
print("tvecs: ",tvecs)

#stores output as json in fisheyecalibration.txt

data = {}

data['ret'] = ret
data['mtx'] = mtx
data['dist'] = dist
data['rvecs'] = rvecs
data['tvecs'] = tvecs

class NumpyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return json.JSONEncoder.default(self, obj)

with open('fisheyecalibration.txt', 'w') as outfile:
    json.dump(data, outfile, cls=NumpyEncoder)

"""
To load the the json into an opencv file, use this code:

with open(calibration/fisheyecalibration.txt as json_file:
    data = json.load(json_file)
    ret=data['ret']
    mtx = data['mtx']
    dist = data['dist']
    rvecs = np.asarray(data['rvecs'])
    tvecs = np.asarray(data['tvecs'])

Then, you can do:

def undistort(img):
    h,  w = img.shape[:2]
    newcameramtx, roi=cv2.getOptimalNewCameraMatrix(mtx,dist,(w,h),1,(w,h))
    # undistort
    dst = cv2.undistort(img, mtx, dist, None, newcameramtx)

    # crop the image
    x,y,w,h = roi
    dst = dst[y:y+h, x:x+w]
    return dst
"""
