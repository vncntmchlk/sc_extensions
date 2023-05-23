// this file is part of redUniverse /redFrik


RedFingerprintCurve {
	var <>array, <>normalize, <>points;
	*new {|array, normalize= true|
		^super.newCopyArgs(array, normalize).calc;
	}
	calc {
		var p= Point(0, 0);
		points= array.copyRange(0, array.size.div(2)*2-1).clump(2).collect{|pair|
			var distance, angle;
			#distance, angle= pair;
			p= p+Point(distance, distance).rotate(angle*2pi);
		};
		if(normalize, {
			points= points.collect{|x| x.asArray}.flat.normalize(-1, 1);
			points= points.clump(2).collect{|x| x.asPoint};
		});
	}
	draw {|length= 1|
		var penClass= GUI.pen;
		penClass.moveTo(points[0]*length);
		points.do{|x| penClass.lineTo(x*length)};
		penClass.stroke;
	}

	drawCurve {|length= 1, rad = (Point())|
		var penClass= GUI.pen;
		penClass.moveTo(points[0]*length);
		// points.pairsDo{|p1, p2| penClass.quadCurveTo(p1 * length,p2)};
		points.pairsDo{ |p1, p2|
			penClass.quadCurveTo(p1*length, p2)
		};
		penClass.stroke;
	}

	makeWindow {|name, bounds, scale= 1, background, color, width= 1,rad = (Point())|
		var win, usr, w, h, centerX, centerY;
		name= name ?? {"fingerprint"+points.size+"points"};
		bounds= bounds ?? {Rect(128, 64, 300, 300)};
		w= bounds.width;
		h= bounds.height;
		centerX= w/2;
		centerY= h/2;
		scale= scale*centerX.min(centerY);
		win= Window(name, bounds, false);
		usr= UserView(win, Rect(0, 0, bounds.width, bounds.height));
		usr.background= background ?? {Color(0.2, 0.1843, 0.2235)};
		color= color ?? {Color.white};
		usr.drawFunc= {
			Pen.width= width;
			Pen.translate(centerX, centerY);
			Pen.strokeColor= color;
			this.drawCurve(scale, rad);
		};
		^win.front;
	}
	gui {|name, bounds, scale= 1, background, color, width= 1|
		this.deprecated(thisMethod, RedFingerprintCurve.findMethod(\makeWindow));
		^this.makeWindow(name, bounds, scale, background, color, width);
	}
}
