#!/usr/bin/env python3
import sys
import os
from flask import Flask, jsonify, request, g, render_template, Response, make_response
import traceback
from SnailVision import FrameStream
from werkzeug.exceptions import HTTPException
import numpy as np
from time import sleep, monotonic
import logging
logging.basicConfig(level=logging.DEBUG)
import json
from threading import RLock

scriptDir = os.path.dirname(os.path.realpath(__file__))

propertyFile = os.path.join(scriptDir, "properties.json")
properties = {}
propertyLock = RLock()
try:
    with open(propertyFile) as fd:
        properties = json.load(fd)
except Exception as e:
    print("Unable to load properties file; starting with empty properties: %s" % str(e))
propertiesString = json.dumps(properties, indent=2, sort_keys=True)

def getProperty(name, default=None):
    with propertyLock:
        v = properties.get(name, default)
    return v

def getAllProperties():
    with propertyLock:
        v = dict(properties)
    return v

def updateProperties(newProperties):
    global properties
    global propertiesString
    with propertyLock:
        ps = json.dumps(newProperties, indent=2, sort_keys=True)
        if ps != propertiesString:
            with open(propertyFile, "w") as fd:
                fd.write(ps)
            propertiesString = ps
            properties = newProperties

def setProperty(name, v):
    with propertyLock:
        if isinstance(v, (dict, list)) or not name in properties or v != properties[name]:
            newProperties = dict(properties)
            newProperties[name] = v
            updateProperties(newProperties)

def delProperty(name):
    with propertyLock:
        if name in properties:
            newProperties = dict(properties)
            del newProperties[name]
            updateProperties(newProperties)

def delAllProperties():
    newProperties = {}
    updateProperties(newProperties)

stream = FrameStream(device=1)

app = Flask(__name__)

class ApiError(RuntimeError):
    def __init__(self, message, status_code=None, payload=None, class_name=None, default_status_code=200):
        super(ApiError, self).__init__(message)
        self.default_status_code = default_status_code
        self.message = message
        self.status_code_ = status_code
        self.payload = payload
        self.class_name_ = class_name
        self.stack_trace_ = None

    @property
    def original_exception(self):
        cause = getattr(self, '__cause__', None)
        if cause is None:
            cause = self
        return cause

    @property
    def class_name(self):
        if self.class_name_ is None:
            self.class_name_ = self.original_exception.__class__.__name__
        return self.class_name_

    @class_name.setter
    def class_name(self, v):
        self.class_name_ = v

    @property
    def status_code(self):
        result = self.status_code_
        if result is None:
            if isinstance(self.original_exception, HTTPException):
                result = self.original_exception.code
            else:
                result = self.default_status_code
        return result

    @status_code.setter
    def status_code(self, v):
        self.status_code = v

    @property
    def stack_trace(self):
        if self.stack_trace_ is None:
            self.stack_trace_ = traceback.format_exception(etype=type(self), value=self, tb=self.__traceback__)
        return self.stack_trace_

    @stack_trace.setter
    def stack_trace(self, v):
        self.stack_trace_ = v

    def to_dict(self):
        ev = dict(
            class_name=self.class_name,
            message=str(self),
            stack_trace=self.stack_trace
          )
        rv = dict(success=False, error=ev, data=self.payload, ts_request_mono=g.ts_req_mono_pre)
        return rv

    def __str__(self):
        result = super(ApiError, self).__str__()
        if not self.original_exception is self:
            result += ': ' + str(self.original_exception)
        return result

class ClientError(ApiError):
    def __init__(self, message, status_code=None, payload=None, class_name=None):
        super(ClientError, self).__init__(message, status_code=status_code, payload=payload, class_name=class_name,
                                          default_status_code=200)

class ServerError(ApiError):
    def __init__(self, message, status_code=None, payload=None, class_name=None):
        super(ServerError, self).__init__(message, status_code=status_code, payload=payload, class_name=class_name,
                                          default_status_code=200)

@app.errorhandler(Exception)
def handle_invalid_usage(error):
    if not isinstance(error, ApiError):
        try:
            raise ServerError(str(error)) from error
        except ApiError as e:
            error = e

    response = jsonify(error.to_dict())
    response.status_code = error.status_code
    return response

def ok_response(data=None):
    result = dict(success=True, data=data, ts_request_mono=g.ts_req_mono_pre)
    response = jsonify(result)
    return response

@app.before_request
def before_request():
    g.ts_req_mono_pre = monotonic()


def getTargetTypeArg():
    targType = request.args.get("target", None)
    if targType is None or targType == '':
        targType = "HATCH"
    targetType = targType.upper()
    if not targetType  in ["NONE", "HATCH", "PORT" ]:
        raise ValueError("Invalid target=%s; must be NONE, HATCH, or PORT" % targetType)
    return targetType

@app.route('/sv/api/v1.0/target-info', methods=['GET'])
def get_target_info():
    frame = stream.acquireLatest()
    frame.process()
    result = dict(
        ts_request_mono=g.ts_req_mono_pre,
        ts_pre_mono=frame.preMono,
        ts_pre=frame.preTs,
        ts_post_mono=frame.postMono,
        ts_post=frame.postTs,
        frame_num_pre=frame.preFrameNum,
        frame_num_post=frame.postFrameNum,
        calib=frame.calib.data,
        rvec=frame.rvec.tolist(),
        tvec=frame.tvec.tolist(),
        dst=frame.dst.tolist(),
        jacobian=frame.jacobian.tolist(),
        t=frame.t,
        x=frame.x,
        y=frame.y,
        rx=frame.Rx,
        ry=frame.Ry,
        originpt=frame.originpt.tolist(),
      )
    return ok_response(result)

@app.route('/sv/api/v1.0/property', methods=['GET'])
def get_property():
    name = request.args.get("name")
    result = getProperty(name)
    return ok_response(result)

@app.route('/sv/api/v1.0/properties', methods=['GET'])
def get_properties():
    result = getAllProperties()
    return ok_response(result)

@app.route('/sv/api/v1.0/clear-properties', methods=['GET'])
def clear_properties():
    result = delAllProperties()
    return ok_response(result)

@app.route('/sv/api/v1.0/set-property', methods=['POST'])
def set_property():
    name = request.args.get("name")
    if name is None or name == '':
        raise ValueError("A property name must be provided")
    v = request.get_json()
    setProperty(name, v)
    return ok_response()

@app.route('/sv/api/v1.0/set-url-property', methods=['GET'])
def set_url_property():
    name = request.args.get("name")
    if name is None or name == '':
        raise ValueError("A property name must be provided")
    v = request.args.get("value")
    setProperty(name, v)
    return ok_response()

@app.route('/sv/api/v1.0/delete-property', methods=['GET'])
def delete_property():
    name = request.args.get("name")
    delProperty(name)
    return ok_response()

@app.route('/sv/api/v1.0/time', methods=['GET'])
def get_time():
    result = dict(
        ts_request_mono=g.ts_req_mono_pre,
      )
    return ok_response(result)

@app.route('/cam.jpg', methods=['GET'])
def get_cam_jpeg():
    targetType = getTargetTypeArg()

    frame = stream.acquireLatest()
    try:
        # Do this just to draw on the frame if a target is found
        frame.process()
        if targetType == "HATCH":
            frame.drawHatch()
        elif targetType == "PORT":
            frame.drawPort()
    except:
        pass
    jpeg = frame.get_jpeg()
    response = make_response(jpeg)
    response.headers.set('Content-Type', 'image/jpeg')
    return response

@app.route("/")
def home():
    return render_template("home.html")

if __name__ == '__main__':
    # NOTE: use_reloader=False is required or gstreamer will not be able to initialize due to camera
    # resource in use error...
    app.run(host="0.0.0.0", port=5800, debug=True, use_reloader=False)
