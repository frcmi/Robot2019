const fieldWidth = 648; // inches
const fieldHeight = 324; // inches
const xOffset = 120;
const yOffset = 180;
const width = 1604; //pixels
const height = 651; //pixels
const widthString = `${width / 1.5}px`;
const heightString = `${height / 1.5}px`

const robotWidth = 31; // inches
const robotHeight = 28.5; // inches

const waypointRadius = 7;
const splineWidth = 2;

const kEps = 1E-9;
const pi = Math.PI;

const angleInputOptions = {
	max: 360,       // maximum value
	min: 0,         // minimum value
	step: 1,        // [min, min+step, ..., max]
	name: 'angle',  // used for <input name>
};

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

	draw(color, radius) {
		color = color || "#2CFF2C";
		ctx.beginPath();
		ctx.arc(this.drawX, this.drawY, radius, 0, 2 * Math.PI, false);
		ctx.fillStyle = color;
		ctx.strokeStyle = color;
		ctx.fill();
		ctx.lineWidth = 0;
		ctx.stroke();
	}

	get drawX() {
		return (this.x + xOffset) * (width / fieldWidth);
	}

	get drawY() {
		return height - (this.y + yOffset) * (height / fieldHeight);
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

	draw(drawHeading, radius) {
		this.translation.draw(null, radius);

		if (!drawHeading) {
			return;
		}

		var x = this.translation.drawX;
		var y = this.translation.drawY;

		ctx.beginPath();
		ctx.moveTo(x, y);
		ctx.lineTo(x + 25 * Math.cos(-this.rotation.getRadians()), y + 25 * Math.sin(-this.rotation.getRadians()));
		ctx.lineWidth = 3;
		ctx.stroke();
		ctx.closePath();
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

/** EPIC KNOCKOUT CODE **/
ko.bindingHandlers.storeElement = {
	init: function(element, valueAccessor) {
		valueAccessor()(element);
	}
}

function SnailPathModel() {
	this.createPoint = function() {

	}.bind(this);

	this.update = function() {

	}.bind(this);

	this.animate = function() {

	}.bind(this);

	this.flipField = function() {
		if (this.imageIsFlipped) {
			this.backgroundCtx.drawImage(this.bgImageFlipped, 0, 0, width, height);
		} else {
			this.backgroundCtx.drawImage(this.bgImage, 0, 0, width, height);
		}
		this.imageIsFlipped = !this.imageIsFlipped;
	}.bind(this);

	this.newPathFile = function() {
		var filename = prompt("Filename?");
		$.post("/api/pathfiles", {
			name: filename,
			success: this.pullFiles
		});
	}.bind(this);

	this.pullFiles = function() {
		this.pathFiles([]);
		$.getJSON("/api/pathfiles", (data) => {
			this.pathFiles(data.files);
		});
	}.bind(this);

	this.drawRobot = function(position, heading) {
		var h = heading;
		var angles = [h + (pi / 2) + t, h - (pi / 2) + t, h + (pi / 2) - t, h - (pi / 2) - t];
		var points = [];

		angles.forEach(function(angle) {
			var point = new Translation2d(position.translation.x + (r * Math.cos(angle)),
			position.translation.y + (r * Math.sin(angle)));
			points.push(point);
			point.draw(Math.abs(angle - heading) < pi / 2 ? "#00AAFF" : "#0066FF", splineWidth);
		});
	}

	this.drawSplines = function(fill, animate) {
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
	
				var splinePoint = splinePoints[i];
				var hue = Math.round(180 * (i++ / splinePoints.length));
	
				var previous = $scope.fgctx.globalCompositeOperation;
				fillRobot(splinePoint, splinePoint.rotation.getRadians(), 'hsla(' + hue + ', 100%, 50%, 0.025)');
				$scope.fgctx.globalCompositeOperation = "source-over";
				drawRobot(splinePoint, splinePoint.rotation.getRadians());
				splinePoint.draw(false, splineWidth);
				$scope.fgctx.globalCompositeOperation = previous;
			}, 25);
		} else {
			splinePoints.forEach(function(splinePoint) {
				splinePoint.draw(false, splineWidth);
	
				if (fill) {
					var hue = Math.round(180 * (i++ / splinePoints.length));
					fillRobot(splinePoint, splinePoint.rotation.getRadians(), 'hsla(' + hue + ', 100%, 50%, 0.025)');
				} else {
					drawRobot(splinePoint, splinePoint.rotation.getRadians());
				}
			});
		}
	};

	this.backgroundCtx = ko.observable();
	this.fieldCtx = ko.observable();

	this.backgroundCanvas = ko.observable();
	this.backgroundCanvas.subscribe(function initCanvas(element) {
		element.setAttribute("width", widthString);
		element.setAttribute("height", heightString);
		var ctx = element.getContext("2d");
		ctx.canvas.width = width;
		ctx.canvas.height = height;
		this.backgroundCtx = ctx;
	}.bind(this));

	this.fieldCanvas = ko.observable();
	this.fieldCanvas.subscribe(function initCanvas(element) {
		element.setAttribute("width", widthString);
		element.setAttribute("height", heightString);
		var ctx = element.getContext("2d");
		ctx.canvas.width = width;
		ctx.canvas.height = height;
		ctx.clearRect(0, 0, width, height);
		this.fieldCtx = ctx;
	}.bind(this));

	this.canvases = ko.observable();
	this.canvases.subscribe(function initCanvas(element) {
		element.setAttribute("width", widthString);
		element.setAttribute("height", heightString);
	});

	this.waypoints = ko.observableArray();
	this.pathFiles = ko.observableArray();
	this.pathFile = ko.observable();

	this.bgImageIsFlipped = false;
	this.bgImage = new Image();
	this.bgImage.src = '/img/field.png';
	this.bgImageFlipped = new Image();
	this.bgImageFlipped.src = '/img/fieldFlipped.png';
	this.bgImage.onload = function() {
		this.backgroundCtx.drawImage(this.bgImage, 0, 0, width, height);
	}.bind(this);

	this.pullFiles();
}

ko.applyBindings(new SnailPathModel());