function bound(value, opt_min, opt_max) {
	if (opt_min != null)
		value = Math.max(value, opt_min);
	if (opt_max != null)
		value = Math.min(value, opt_max);
	return value;
}

function degreesToRadians(deg) {
	return deg * (Math.PI / 180);
}

function radiansToDegrees(rad) {
	return rad / (Math.PI / 180);
}

function MercatorProjection() {
  this.PICTURE_SIZE = 800.0;
	var TILE_SIZE =256.0;
	var MIN_ZOOM=6;
	this.zoomFactor = 
			 (1 << MIN_ZOOM);

	var tileSize = TILE_SIZE;
	this.pixelOrigin_ = new google.maps.Point(tileSize / 2, tileSize / 2);
	this.pixelsPerLonDegree_ = tileSize / 360;
	this.pixelsPerLonRadian_ = tileSize / (2 * Math.PI);
}

MercatorProjection.prototype.fromLatLngToPoint = function(latLng, opt_point) {
	var me = this;
	var point = opt_point || new google.maps.Point(0, 0);
	var origin = me.pixelOrigin_;

  var lng = latLng.lng();
  if (lng>0) {
    lng-=360;
  }
	point.x = origin.x + lng * me.pixelsPerLonDegree_;

	// NOTE(appleton): Truncating to 0.9999 effectively limits latitude to
	// 89.189. This is about a third of a tile past the edge of the world
	// tile.
	var siny = bound(Math.sin(degreesToRadians(latLng.lat())), -0.9999, 0.9999);
	point.y = origin.y + 0.5 * Math.log((1 + siny) / (1 - siny))
			* -me.pixelsPerLonRadian_;

	point.x *= me.zoomFactor;
	point.y *= me.zoomFactor;
	return point;
};

MercatorProjection.prototype.fromPointToLatLng = function(param) {
	var me = this;
	var point = new google.maps.Point(param.x / me.zoomFactor, param.y
			/ me.zoomFactor);
	var origin = me.pixelOrigin_;
	var lng = (point.x - origin.x) / me.pixelsPerLonDegree_;
	var latRadians = (point.y - origin.y) / -me.pixelsPerLonRadian_;
	var lat = radiansToDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
	return new google.maps.LatLng(lat, lng);
};
