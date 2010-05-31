/* ************************************************************************************* *\
 * The MIT License
 * Copyright (c) 2007 Fabio Zendhi Nagao - http://zend.lojcomm.com.br
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
\* ************************************************************************************* */

var iFishEye = new Class({
	options: {
			container: document,
			targetImageClass: ".iFishEyeImg",
			targetCaptionClass: ".iFishEyeCaption",
			dimThumb: {width:64, height:64},
			dimFocus: {width:128, height:128},
			eyeRadius: 192,
			pupilRadius: 50,
			useAxis: 'x',
			norm: "L1",
			blankPath: "images/blank.gif",

			onEyeOver: Class.empty,
			onEyeOut: Class.empty,
			onPupilOver: Class.empty,
			onPupilOut: Class.empty
	},

	initialize: function(options) {
		this.setOptions(options);
		this.imgs = $$(this.options.targetImageClass);
		this.captions = $A($$(this.options.targetCaptionClass));

		this.imgs.each(function(obj, i) {
			obj.setStyles({
				width: this.options.dimThumb.width +"px",
				height: this.options.dimThumb.height +"px"
			});

			var src = obj.getProperty("src");
            if(src) {
                var ext = src.substr(src.length - 3);
                if(ext == "png" && window.ie) {
                    obj.setStyle("filter", "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+ src +"',sizingMethod='scale')");
                    obj.setProperty("src", this.options.blankPath);
                }
            }

            var caption = this.captions[i];
            if (caption)
                caption.setOpacity(0);
		}.bind(this));

		this.options.container.addEvents({
			"mousemove": function(event) {
				event = new Event(event);
				this._nextState(event);
			}.bind(this),

			"mouseleave": function() {
				this._initialState();
			}.bind(this)
		});
	},

	_initialState: function() {
		this.imgs.each(function(obj, i) {
			this.captions[i].setOpacity(0);
 			obj.effects({duration: 300, transition: Fx.Transitions.Sine.easeInOut}).start({
				"width": [obj.getStyle("width").toInt(), this.options.dimThumb.width],
				"height": [obj.getStyle("height").toInt(), this.options.dimThumb.height]
			})
		}.bind(this))
	},

	_nextState: function(event) {
		this.imgs.each(function(obj, i) {
			var h = this._getDistance(event, obj);
			var objProperties = this._getDimensions(h);
			obj.setStyles({
				width: objProperties.width +"px",
				height: objProperties.height +"px"
			});

			if(h < this.options.eyeRadius) this.fireEvent("onEyeOver", obj, 20);
			else this.fireEvent("onEyeOut", obj, 20);

			if(h < this.options.pupilRadius) {
				this.captions[i].setOpacity(1);
				this.fireEvent("onPupilOver", obj, 20);
			} else {
				this.captions[i].setOpacity(0);
				this.fireEvent("onPupilOut", obj, 20);
			}
		}.bind(this));
	},

	_getDistance: function(event, obj) {
		var objProperties = obj.getCoordinates();
		var curProperties = {
			x: event.page.x,
			y: event.page.y
		};
		objProperties.center = {
			x: (objProperties.left + (objProperties.width / 2)),
			y: (objProperties.top + (objProperties.height / 2))
		};
		if(this.options.useAxis.length > 1) {
			switch(this.options.norm.toUpperCase()) {
				case "L1":
					return Math.abs(curProperties.x - objProperties.center.x) + Math.abs(curProperties.y - objProperties.center.y);
					break;
				case "L2":
					return Math.round(Math.sqrt(Math.pow((curProperties.x - objProperties.center.x), 2) + Math.pow((curProperties.y - objProperties.center.y),2)));
					break;
			};
		} else {
			return Math.abs(curProperties[this.options.useAxis] - objProperties.center[this.options.useAxis]);
		}
	},

	_getDimensions: function(h) {
		if(h < this.options.eyeRadius) {
			var width = (((this.options.dimThumb.width - this.options.dimFocus.width) / this.options.eyeRadius) * h) + this.options.dimFocus.width;
			var height = (((this.options.dimThumb.height - this.options.dimFocus.height) / this.options.eyeRadius) * h) + this.options.dimFocus.height;
		} else {
			var width = this.options.dimThumb.width;
			var height = this.options.dimThumb.height;
		}
		return {width:width, height:height};
	}
});
iFishEye.implement(new Events); // Implements addEvent(type, fn), fireEvent(type, [args], delay) and removeEvent(type, fn)
iFishEye.implement(new Options);// Implements setOptions(defaults, options)
