#!/usr/bin/env python3
from flask import Flask, jsonify, request, g
import traceback
from SnailVision import FrameStream
from werkzeug.exceptions import HTTPException
import numpy as np
from time import sleep, monotonic

stream = FrameStream(device=1)

app = Flask(__name__)

class ApiError(RuntimeError):
    def __init__(self, message, status_code=None, payload=None, class_name=None, default_status_code=500):
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
                                          default_status_code=400)

class ServerError(ApiError):
    def __init__(self, message, status_code=None, payload=None, class_name=None):
        super(ServerError, self).__init__(message, status_code=status_code, payload=payload, class_name=class_name,
                                          default_status_code=500)

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

def ok_response(data):
    result = dict(success=True, data=data, ts_request_mono=g.ts_req_mono_pre)
    response = jsonify(result)
    return response

@app.before_request
def before_request():
    g.ts_req_mono_pre = monotonic()


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
        frame_num_post_post=frame.postFrameNum,
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

@app.route('/sv/api/v1.0/time', methods=['GET'])
def get_time():
    result = dict(
        ts_request_mono=g.ts_req_mono_pre,
      )
    return ok_response(result)

if __name__ == '__main__':
    # NOTE: use_reloader=False is required or gstreamer will not be able to initialize due to camera
    # resource in use error...
    app.run(host="0.0.0.0", debug=True, use_reloader=False)
