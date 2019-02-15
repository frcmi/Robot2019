const express = require("express");
const bodyParser = require("body-parser");
const request = require("request");
var config = require("./config.json");

config.string = {
	width: `${config.screen.width / 1.5}px`,
	height: `${config.screen.height / 1.5}px`
};

var app = express();

var pathFiles = [];

app.use(express.static("static"))
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
	extended: true
})); 

app.get("/api/pathfiles", function(req, res) {
	res.send({
		files: pathFiles,
		status: true
	});
});

app.get("/api/pathfile", function(req, res) {
	if (req.body.name) {
		request.get(`http://tegra-ubuntu.local:5800/sv/api/v1.0/property?name=${req.body.name}`, function(error, response, body) {
			res.send({status: true, points: body});
		});
	} else{
		res.send({status: false, error: "missing param name"});
	}
});

app.post("/api/pathfiles", function(req, res) {
	if (req.body.name) {
		pathFiles.push(req.body.name);
		console.log(req.body.points);
		request.post(`http://tegra-ubuntu.local:5800/sv/api/v1.0/set-property?name=${req.body.name}`, {json: {points: req.body.points}}, function(error, response, body) {
			res.send({status: true});
		});
	} else{
		res.send({status: false, error: "missing param name"});
	}
});

app.get("/api/config", function(req, res) {
	res.send(config);
});

app.post("/api/calculatesplines", function(req, res) {
	var csvPath = "";
	var splines = req.body.splines;
	
	splines.forEach(function(spline) {
		csvPath += `${spline.x},${spline.y},${spline.rotation};`;
	});

	request.post("http://localhost:8080/api/calculate_splines", {body: csvPath}, function(error, response, body) {
		if (error || body == "no") {
			console.log(csvPath);
			res.send({status: false, error: error});
			return;
		}

		console.log(body);
		try {
			res.send(JSON.parse(body));	
		} catch (err) {
			res.send({status: false, error: err});
		}	
	});
});

app.listen("8081", function() {
	console.log("ok and good");
});