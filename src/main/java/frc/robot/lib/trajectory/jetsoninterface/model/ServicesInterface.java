package frc.robot.lib.trajectory.jetsoninterface.model;

import frc.robot.lib.trajectory.WaypointSequence;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*

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
    v = request.get_json()
    setProperty(name, v)
    return ok_response()

@app.route('/sv/api/v1.0/set-url-property', methods=['GET'])
def set_url_property():
    name = request.args.get("name")
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


 */
@Path("/sv/api/v1.0")
public interface ServicesInterface {
    @GET
    @Path("/time")
    @Produces({ MediaType.APPLICATION_JSON })
    TimeResponse time();

    @GET
    @Path("/set-url-property")
    @Produces({ MediaType.APPLICATION_JSON })
    TimeResponse failtime();

    @GET
    @Path("/set-url-property")
    @Produces({ MediaType.APPLICATION_JSON })
    SimpleResponse setUrlProperty(
            @QueryParam("name") String name,
            @QueryParam("value") String value
        );

    @POST
    @Path("/set-property")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON })
    SimpleResponse setProperty(
            @QueryParam("name") String name,
            Object value
        );

    @GET
    @Path("/property")
    @Produces({ MediaType.APPLICATION_JSON })
    GetPropertyResponse getProperty(
            @QueryParam("name") String name
        );

    @POST
    @Path("/set-property")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON })
    SimpleResponse setWaypointSequenceProperty(
            @QueryParam("name") String name,
            WaypointSequence value
        );

    @GET
    @Path("/property")
    @Produces({ MediaType.APPLICATION_JSON })
    GetWaypointSequencePropertyResponse getWaypointSequenceProperty(
            @QueryParam("name") String name
        );

    @GET
    @Path("/target-info")
    @Produces({ MediaType.APPLICATION_JSON })
    TargetInfoResponse targetInfo();
}
