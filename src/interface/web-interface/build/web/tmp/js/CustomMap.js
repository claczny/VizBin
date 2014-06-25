function CustomMap(mapDiv) {
	this.MIN_ZOOM=6;
	this.MAX_ZOOM=7;
	this.TILE_SIZE=256;
	this.PICTURE_SIZE=800;
	this.SERVLET_ADDR="../getlist";
  var param = (document.location+"").split("\/");
  this.ID = param[param.length-2];
	var customMapSelf = this;

	this.projection = new MercatorProjection();

	var mapOptions = this.creatMapOptions();

	this.map = new google.maps.Map(mapDiv, mapOptions);

	var cvTypeOptions = this.createTypeOptions();
	cvMapType = new google.maps.ImageMapType(cvTypeOptions)
	this.map.mapTypes.set('Normal', cvMapType);
	
	this.map.setMapTypeId('Normal');

  this.initDrawingLibrary();

}

CustomMap.prototype.initDrawingLibrary = function() {
  this.drawingManager = new google.maps.drawing.DrawingManager({
  	drawingMode: google.maps.drawing.OverlayType.POLYGON,
    drawingControl: true,
    drawingControlOptions: {
      position: google.maps.ControlPosition.TOP_CENTER,
      drawingModes: [
        google.maps.drawing.OverlayType.POLYGON,
      ]
    },
    polygonOptions: {
    fillColor: '#ffff00',
    fillOpacity: 0.5,

    strokeWeight: 5,
    clickable: true,
    zIndex: 1,
    editable: false
    }
  });
  
  this.drawingManager.setMap(this.map);
  
  customMap = this;
  google.maps.event.addListener(this.drawingManager, 'overlaycomplete', function(e) {
    if (e.type != google.maps.drawing.OverlayType.MARKER) {
    	// Switch back to non-drawing mode after drawing a shape.
    	customMap.drawingManager.setDrawingMode(null);

    	// Add an event listener that selects the newly-drawn shape when the user
    	// mouses down on it.
    	var newShape = e.overlay;
    	newShape.type = e.type;
    	google.maps.event.addListener(newShape, 'click', function(e) {
    	  newShape.position = e.latLng;
    		customMap.setSelection(newShape);

    		var len = newShape.getPath().length;
        var path = newShape.getPath();
        var res="";
        for (var i =0;i<len;i++) {
          var point = customMap.projection.fromLatLngToPoint(path.getAt(i));
      		res+=point.x+","+point.y+";";
        }
        var query = customMap.SERVLET_ADDR + "?points=" + encodeURIComponent(res)+"&id="+customMap.ID;
        $.get(query, function(data) {
          var infowindow = new google.maps.InfoWindow({
            content: data,
          });
          infowindow.setPosition(newShape.position);
          infowindow.open(customMap.map);
        });
    	});
    	customMap.setSelection(newShape);
    }
  });

  // Clear the current selection when the drawing mode is changed, or when the
  // map is clicked.
  google.maps.event.addListener(this.drawingManager, 'drawingmode_changed', function() {customMap.clearSelection();});
  google.maps.event.addListener(this.map, 'click', function() {customMap.clearSelection();});
  google.maps.event.addDomListener(document.getElementById('delete-button'), 'click', function() {customMap.deleteSelectedShape();});
  
}

CustomMap.prototype.centerMap = function(x, y) {
	loc = new google.maps.Point(x, y);
	p = this.projection.fromPointToLatLng(loc);
	this.map.setCenter(p);
}
CustomMap.prototype.createTypeOptions = function(param) {
	result = {
		getTileUrl : function(coord, zoom) {
			var tileRange = 1 << zoom;
			if (coord.y < 0 || coord.y >= tileRange || coord.x < 0
					|| coord.x >= tileRange) {
				return null;
			}
			var addr = "images/" + zoom + "/" + coord.x + ","
					+ coord.y + ".PNG";
			return addr;
		},
		tileSize : new google.maps.Size(this.TILE_SIZE, this.TILE_SIZE),
		maxZoom : this.MAX_ZOOM,
		minZoom : this.MIN_ZOOM,
		radius : 360,
		name : "Normal"
	};
	return result;
}
CustomMap.prototype.getCenterPointLatLng = function() {

	return this.projection
			.fromPointToLatLng(new google.maps.Point(
					this.PICTURE_SIZE / 2,
					this.PICTURE_SIZE / 2));
}
CustomMap.prototype.creatMapOptions = function() {
	var myLatlng = this.getCenterPointLatLng();
	var mtypes = [];
		mtypes.push('Normal');
	var result = {
		center : myLatlng,
		rotateControl : true,
		panControl : true,
		zoom : this.MIN_ZOOM,
		streetViewControl : false,
		mapTypeControlOptions : {
			mapTypeIds : mtypes
		}
	};
	return result;
}

CustomMap.prototype.clearSelection = function () {
  if (this.selectedShape) {
    this.selectedShape.setEditable(false);
    this.selectedShape = null;
  }
}

CustomMap.prototype.setSelection = function (shape) {
	if (this.selectedShape!=shape) {
  	this.clearSelection();
  	this.selectedShape = shape;
  	shape.setEditable(true);
	}
}

CustomMap.prototype.deleteSelectedShape =  function() {
  if (this.selectedShape) {
    this.selectedShape.setMap(null);
  }
}
