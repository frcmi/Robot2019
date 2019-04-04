var animation;

function uuidv4() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}

function epicFail(msg) {
	var bid = uuidv4();
	$("body").append(`<div id="crashHandler-${bid}" title="Crash Handler (${bid})">${msg}</div>`)
	$(`#crashHandler-${bid}`).dialog({
		height: 500,
		width: 1000
	});
}

class Translation2d {
	constructor(x, y) {
		this.x = x;
		this.y = y;
	}

	norm() {
		return Math.hypot(this.x, this.y);
	}

	norm2() {
		return this.x * this.x + this.y * this.y;
	}

	translateBy(other) {
		return new Translation2d(this.x + other.x, this.y + other.y);
	}

	rotateBy(rotation) {
		return new Translation2d(this.x * rotation.cos - this.y * rotation.sin, this.x * rotation.sin + this.y * rotation.cos);
	}

	direction() {
		return new Rotation2d(this.x, this.y, true);
	}

	inverse() {
		return new Translation2d(-this.x, -this.y);
	}
	
	interpolate(other, x) {
		if (x <= 0) {
			return new Translation2d(this.x, this.y);
		} else if (x >= 1) {
			return new Translation2d(other.x, other.y);
		}
		return this.extrapolate(other, x);
	}

	extrapolate(other, x) {
		return new Translation2d(x * (other.x - this.x) + this.x, x * (other.y - this.y) + this.y);
	}

	scale(s) {
		return new Translation2d(this.x * s, this.y * s);
	}

	static dot(a, b) {
		return a.x * b.x + a.y * b.y;
	}
	
	static getAngle(a, b) {
		var cos_angle = this.dot(a, b) / (a.norm() * b.norm());
		if (Double.isNaN(cos_angle)) {
			return new Rotation2d(1, 0, false);
		}

		return Rotation2d.fromRadians(Math.acos(Math.min(1.0, Math.max(cos_angle, -1.0))));
	}

	static cross(a, b) {
		return a.x * b.y - a.y * b.x;
	}

	distance(other) {
		return this.inverse().translateBy(other).norm();
	}
}

class Rotation2d {
	constructor(x, y, normalize) {
		this.cos = x;
		this.sin = y;
		this.normalize = normalize;
		if (normalize) {
			this.normalizeFunc();
		}
	}

	static fromRadians(angle_radians) {
		return new Rotation2d(Math.cos(angle_radians), Math.sin(angle_radians), false);
	}

	static fromDegrees(angle_degrees) {
		return this.fromRadians(d2r(angle_degrees));
	}

	normalizeFunc() {
		var magnitude = Math.hypot(this.cos, this.sin);
		if (magnitude > kEps) {
			this.cos /= magnitude;
			this.sin /= magnitude;
		} else {
			this.sin = 0;
			this.cos = 1;
		}
	}

	tan() {
		if (Math.abs(this.cos) < kEps) {
			if (this.sin >= 0.0) {
				return Number.POSITIVE_INFINITY;
			} else {
				return Number.NEGATIVE_INFINITY;
			}
		}
		return this.sin / this.cos;
	}

	getRadians() {
		return Math.atan2(this.sin, this.cos);
	}

	getDegrees() {
		return r2d(this.getRadians());
	}

	rotateBy(other) {
		return new Rotation2d(this.cos * other.cos - this.sin * other.sin,
				this.cos * other.sin + this.sin * other.cos, true);
	}

	normal() {
		return new Rotation2d(-this.sin, this.cos, false);
	}

	inverse() {
		return new Rotation2d(this.cos, -this.sin, false);
	}

	interpolate(other, x) {
		if (x <= 0) {
			return new Rotation2d(this.cos, this.sin, this.normalize);
		} else if (x >= 1) {
			return new Rotation2d(other.cos, other.sin, other.normalize);
		}
		var angle_diff = this.inverse().rotateBy(other).getRadians();
		return this.rotateBy(Rotation2d.fromRadians(angle_diff * x));
	}

	distance(other) {
		return this.inverse().rotateBy(other).getRadians();
	}
}

class Pose2d {
	constructor(translation, rotation, comment) {
		this.translation = translation;
		this.rotation = rotation;
		this.comment = comment || "";
	}

	static exp(delta) {
		var sin_theta = Math.sin(delta.dtheta);
		var cos_theta = Math.cos(delta.dtheta);
		var s, c;

		if (Math.abs(delta.dtheta) < kEps) {
			s = 1.0 - 1.0 / 6.0 * delta.dtheta * delta.dtheta;
			c = .5 * delta.dtheta;
		} else {
			s = sin_theta / delta.dtheta;
			c = (1.0 - cos_theta) / delta.dtheta;
		}

		return new Pose2d(new Translation2d(delta.dx * s - delta.dy * c, delta.dx * c + delta.dy * s),
				new Rotation2d(cos_theta, sin_theta, false));
	}
	
	static log(transform) {
		var dtheta = transform.getRotation().getRadians();
		var half_dtheta = 0.5 * dtheta;
		var cos_minus_one = transform.getRotation().cos() - 1.0;
		var halftheta_by_tan_of_halfdtheta;
		if (Math.abs(cos_minus_one) < kEps) {
			halftheta_by_tan_of_halfdtheta = 1.0 - 1.0 / 12.0 * dtheta * dtheta;
		} else {
			halftheta_by_tan_of_halfdtheta = -(half_dtheta * transform.getRotation().sin()) / cos_minus_one;
		}
		var translation_part = transform.getTranslation()
				.rotateBy(new Rotation2d(halftheta_by_tan_of_halfdtheta, -half_dtheta, false));
		return new Twist2d(translation_part.x(), translation_part.y(), dtheta);
	}

	get getTranslation() {
		return this.translation;
	}

	get getRotation() {
		return this.rotation;
	}

	transformBy(other) {
		return new Pose2d(this.translation.translateBy(other.translation.rotateBy(this.rotation)),
				this.rotation.rotateBy(other.rotation));
	}

	inverse() {
		var rotation_inverted = this.rotation.inverse();
		return new Pose2d(this.translation.inverse().rotateBy(rotation_inverted), rotation_inverted);
	}

	normal() {
		return new Pose2d(this.translation, this.rotation.normal());
	}

	interpolate(other, x) {
		if (x <= 0) {
			return new Pose2d(this.translation, this.rotation, this.comment);
		} else if (x >= 1) {
			return new Pose2d(other.translation, other.rotation, other.comment);
		}
		var twist = Pose2d.log(this.inverse().transformBy(other));
		return this.transformBy(Pose2d.exp(twist.scaled(x)));
	}

	distance(other) {
		return Pose2d.log(this.inverse().transformBy(other)).norm();
	}

	heading(other) {
		return Math.atan2(this.translation.y - other.translation.y, this.translation.x - other.translation.x);
	}
	
	toString() {
		return `new Pose2d(new Translation2d(this.translation.x, this.translation.y), new Rotation2d(${this.rotation.cos}, ${this.rotation.sin}, ${this.rotation.normalize}))`;
	}

	transform(other) {
		other.position.rotate(this.rotation);
		this.translation.translate(other.translation);
		this.rotation.rotate(other.rotation);
	}
}

function d2r(d) {
	return d * (Math.PI / 180);
}

function r2d(r) {
	return r * (180 / Math.PI);
}

class Spline {
	constructor() {

	}

	getPoint(t) {

	}

	getHeading(t) {

	}

	getCurvature(t) {

	}

	getDCurvature(t) {

	}

	getVelocity(t) {

	}

	getPose2d(t) {
		return new Pose2d(this.getPoint(t), this.getHeading(t));
	}

	getPose2dWithCurvature(t) {
		return new this.getPose2dWithCurvature(this.getPose2d(t), this.getCurvature(t), this.getDCurvature(t) / this.getVelocity(t));
	}
}

class QuinticHermiteSpline extends Spline {
	constructor(p0, p1) {
		var scale = 1.2 * p0.getTranslation().distance(p1.getTranslation);
		this.x0 = p0.getTranslation().x();
        this.x1 = p1.getTranslation().x();
        this.dx0 = p0.getRotation().cos() * scale;
        this.dx1 = p1.getRotation().cos() * scale;
        this.ddx0 = 0;
        this.ddx1 = 0;
        this.y0 = p0.getTranslation().y();
        this.y1 = p1.getTranslation().y();
        this.dy0 = p0.getRotation().sin() * scale;
        this.dy1 = p1.getRotation().sin() * scale;
        this.ddy0 = 0;
		this.ddy1 = 0;
		
		this.computeCoefficients();
	}
}

var app = angular.module("snailpath", []);

app.controller("app", function($scope, $http) {
	$scope.config = {
		thisShouldNotBe: true
	};

	$scope.points = [];

	function getConfig() {
		$scope.getConfigPromise = $http({
			method: "GET",
			url: "/api/config"
		});

		$scope.getConfigPromise.then(
			function success(res) {
				$scope.config = res.data;
			},
	
			function failure(res) {
				epicFail(res.data);
			}
		);
	}

	getConfig();
});

app.controller("info", function($scope) {
	$scope.seltab = "file";
});

app.controller("infoConfig", function($scope) {

});

app.controller("infoAbout", function($scope) {
	$scope.aboutInfo = `SnailPath 1.0
By Carver Harrison
Based on code from Team 254`;
});

app.controller("infoFile", function($scope, $http) {
	$scope.creatingFilename = "";
	$scope.loadingFilename = "";
	$scope.files = [];

	function reloadFiles() {
		$http({
			method: "GET",
			url: "/api/pathfiles",
		}).then(
			function success(res) {
				$scope.files = res.data.files;
			},

			function failure(res) {
				epicFail(res.data);
			}
		)
	}

	$scope.saveFile = function() {
		$http({
			method: "POST",
			url: "/api/pathfiles",
			data: {
				name: $scope.creatingFilename,
				points: $scope.points
			}
		}).then(
			function success(res) {
				$scope.loadingFilename = $scope.creatingFilename;
			},

			function failure(res) {
				epicFail(res.data);
			}
		);
	}

	$scope.loadFile = function() {
		$http({
			method: "GET",
			url: `/api/pathfile?name=${$scope.creatingFilename}`
		}).then(
			function success(res) {
				$scope.points = res.data.points;
			},

			function failure(res) {
				epicFail(res.data);
			}
		);
	}

	reloadFiles();
});

app.controller("infoReleaseNotes", function($scope) {

});

app.controller("infoQuickLinks", function($scope) {

});

app.controller("pointManager", function($scope) {
	$scope.newPoint = function() {
		if ($scope.points.length == 0) {
			$scope.points.push({
				id: uuidv4(),
				x: 100,
				y: 100,
				rotation: 0,
				name: "Initial Point",
				enabled: true
			});
		} else {
			$scope.points.push({
				id: uuidv4(),
				x: _.last($scope.points).x+50,
				y: _.last($scope.points).y+50,
				rotation: _.last($scope.points).rotation+50,
				name: "",
				enabled: true
			});

			$scope.draw();
		}
	};

	$scope.deletePoint = function(point) {
		$scope.points = _.without($scope.points, _.findWhere($scope.points, { id: point.id }));
	};

	$scope.swapPoint = function(point, pos) {
		var v = $scope.points.findIndex(function(i) {
			return i.id == point.id;
		});

		var a = $scope.points[v];
		var b = $scope.points[v+pos];

		$scope.points[v] = b;
		$scope.points[v+pos] = a;
	};

	$scope.newPoint();
});

app.controller("canvases", function($scope, $http) {
	function pointDrawX(point) {
		return (point.x + $scope.config.offset.x) * ($scope.config.screen.width / $scope.config.field.width);
	}

	function pointDrawY(point) {
		return $scope.config.screen.height - (point.y + $scope.config.offset.y) * ($scope.config.screen.height / $scope.config.field.height);
	}

	function drawRobot(position, heading) {
		var h = heading;
		var t = Math.atan2($scope.config.robot.height, $scope.config.robot.width);
		var r = Math.sqrt(Math.pow($scope.config.robot.width, 2) + Math.pow($scope.config.robot.height, 2)) / 2;

		var angles = [h + (Math.PI / 2) + t, h - (Math.PI / 2) + t, h + (Math.PI / 2) - t, h - (Math.PI / 2) - t];
		var points = [];

		angles.forEach(function(angle) {
			var point = new Translation2d(position.translation.x + (r * Math.cos(angle)), position.translation.y + (r * Math.sin(angle)));
			points.push(point);
			drawPoint(point, Math.abs(angle - heading) < Math.PI / 2 ? "#00AAFF" : "#0066FF", $scope.config.spline.width);
		});

		console.log("Drawing done");
	}

	function fillRobot(position, heading, color) {
		var previous = $scope.fgctx.globalCompositeOperation;
		$scope.fgctx.globalCompositeOperation = "destination-over";
	
		var translation = position.translation;
	
		$scope.fgctx.translate(translation.drawX, translation.drawY);
		$scope.fgctx.rotate(-heading);
	
		var w = $scope.config.robot.width * ($scope.config.screen.width / $scope.config.field.width);
		var h = $scope.config.robot.height * ($scope.config.screen.height / $scope.config.field.height);

		console.log([h,w]);
		$scope.fgctx.fillStyle = color || "rgba(0, 0, 0, 0)";
		$scope.fgctx.fillRect(-h / 2, -w / 2, h, w);
	
		$scope.fgctx.rotate(heading);
		$scope.fgctx.translate(-translation.drawX, -translation.drawY);
	
		$scope.fgctx.globalCompositeOperation = previous;
	}

	function drawPoint(point, color, radius) {
		color = color || "#2CFF2C";
		$scope.fgctx.beginPath();
		$scope.fgctx.arc(pointDrawX(point), pointDrawY(point), radius, 0, 2 * Math.PI, true);
		$scope.fgctx.fillStyle = color;
		$scope.fgctx.strokeStyle = color;
		$scope.fgctx.fill();
		$scope.fgctx.lineWidth = 0;
		$scope.fgctx.stroke();
		console.log(`pointdraw [${[pointDrawX(point), pointDrawY(point), radius, 0, 2 * Math.PI, false]}]`);
	}

	function calculateWaypoints() {
		var waypoints = [];

		$scope.points.forEach(function(point) {
			if (point.enabled) {
				console.log(`waypoint calc X: ${point.x} Y: ${point.y}`);
				waypoints.push(new Pose2d(new Translation2d(point.x, point.y), Rotation2d.fromDegrees(point.rotation), point.name));
			}
		});

		return waypoints;
	}

	function drawWaypoints() {
		var waypoints = calculateWaypoints();

		waypoints.forEach(function(waypoint) {
			drawPoint(waypoint, true, $scope.config.waypoint.radius);
			drawRobot(waypoint, waypoint.rotation.getRadians());
		});
	}

	function drawSplines(fill, animate) {
		$http({
			method: "POST",
			url: "/api/calculatesplines",
			data: {
				splines: $scope.points
			}
		}).then(
			function success(res) {
				drawSplinesReal(fill, animate, res.data.points);
			},

			function failure(res) {
				epicFail(res.data);
			}
		);
	}

	function drawSplinesReal(fill, animate, sp) {
		var splinePoints = [];

		if (sp === undefined) return;
		
		sp.forEach(function(point) {
			splinePoints.push(new Pose2d(new Translation2d(point.x, point.y), Rotation2d.fromDegrees(point.rotation), point.name));
		});

		animate = animate || false;
		var i = 0;
	
		if (animate) {
			clearInterval(animation);
	
			animation = setInterval(function() {
				if (i === splinePoints.length) {
					animating = false;
					clearInterval(animation);
					return;
				}
	
				animating = true;
	
				var hue = Math.round(180 * (i++ / splinePoints.length));
	
				var splinePoint = splinePoints[i];
				
				var previous = $scope.fgctx.globalCompositeOperation;
				fillRobot(splinePoint, splinePoint.rotation.getRadians(), `hsla(${hue}, 100%, 50%, 0.025)`);
				$scope.fgctx.globalCompositeOperation = "source-over";
				drawRobot(splinePoint, splinePoint.rotation.getRadians());
				drawPoint(splinePoint, false, $scope.config.spline.width);
				$scope.fgctx.globalCompositeOperation = previous;
			}, 25);
		} else {
			splinePoints.forEach(function(splinePoint) {
				drawPoint(splinePoint, false, $scope.config.spline.width);
	
				if (fill) {
					var hue = Math.round(180 * (i++ / splinePoints.length));
					fillRobot(splinePoint, splinePoint.rotation.getRadians(), `hsla(${hue}, 100%, 50%, 0.025)`);
				} else {
					drawRobot(splinePoint, splinePoint.rotation.getRadians());
				}
			});
		}
	}

	$scope.fgel = document.getElementById("cvForeground");
	$scope.bgel = document.getElementById("cvBackground");
	$scope.fgctx = $scope.fgel.getContext("2d");
	$scope.bgctx = $scope.bgel.getContext("2d");

	$scope.flipped = false;
	$scope.imageNormal = new Image();
	$scope.imageNormal.src = '/img/field.png';
	$scope.imageFlipped = new Image();
	$scope.imageFlipped.src = '/img/fieldFlipped.png';
	$scope.image = $scope.imageNormal;

	$scope.flip = function() {
		if ($scope.flipped) {
			$scope.image = $scope.imageNormal;
		} else {
			$scope.image = $scope.imageFlipped;
		}

		$scope.flipped = !$scope.flipped;

		$scope.draw();
	};

	$scope.animate = function() {
		drawSplines(true, true);
	};

	$scope.drawBg = function() {
		$scope.bgctx.clearRect(0, 0, $scope.bgctx.canvas.width, $scope.bgctx.canvas.height);
		$scope.bgctx.drawImage($scope.image, 0, 0, $scope.bgctx.canvas.width, $scope.bgctx.canvas.height);
	};

	$scope.drawFg = function() {
		$scope.fgctx.clearRect(0, 0, $scope.fgctx.canvas.width, $scope.fgctx.canvas.height);
		drawWaypoints();
		drawSplines(true, false);
		drawSplines(false, false);
	};

	$scope.draw = function() {
		$scope.drawBg();
		$scope.drawFg();
	}

	$scope.getConfigPromise.then(function() {
		$scope.fgctx.canvas.width = $scope.config.screen.width;
		$scope.fgctx.canvas.height = $scope.config.screen.height;
		$scope.bgctx.canvas.width = $scope.config.screen.width;
		$scope.bgctx.canvas.height = $scope.config.screen.height;

		$scope.fgel.style.width = $scope.config.string.width;
		$scope.fgel.style.height = $scope.config.string.height;
		$scope.bgel.style.width = $scope.config.string.width;
		$scope.bgel.style.height = $scope.config.string.height;

		$scope.draw();
	});
});