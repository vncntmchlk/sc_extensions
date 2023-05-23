// this file is part of redUniverse /redFrik

//--todo:
//drawing normalization

RedLTurtleAnimate {
	var	<>lsystem, <>length, <>theta, <>scale, <>noise, <>waitDur, <>currentNote,
	<>commands, <>preCommandAction, <>playCommands, <>noteScale,
	n, index, <drawHist, win, usr, b, noteStack, saveInitTranslate, saveInitAngle,initNote, noteStep,
	noteCommands;
	*new {|lsystem, length= 40, theta= 20, scale= 1, noise= 0, waitDur= 0.2, currentNote= 0, noteScale = (Scale.chromatic)|
		^super.newCopyArgs(lsystem, length, theta, scale, noise, waitDur, currentNote).initRedLTurtle;
	}
	initRedLTurtle {
		initNote = currentNote;
		drawHist = List.new;
		noteStack = List.new;
		// divide half circle
		noteStep = theta / 180 * noteScale.size; //12;
		noteCommands = (
			$F: {//|depth, depthLength, index|
				//(dur: waitDur, note: currentNote).play;
			},
			/*$B: {//|depth, depthLength, index|
			(dur: waitDur, note: currentNote).play;
			},*/
			$G: {|depth, depthLength, index|
				//Pen.translate(0, depthLength.neg);
			},
			$+: {
				currentNote = currentNote + noteStep;
				//Pen.rotate(20/360*2pi+0.rand2);
			},
			$-: {
				currentNote = currentNote - noteStep;
				//Pen.rotate(20/360* -2pi+0.rand2)
			},
			$[: {
				noteStack.add(currentNote);
				//Pen.push;
			},
			$]: {
				currentNote = noteStack.pop;
			},
			$|: {|depth, depthLength, index|
				//(dur: waitDur, note: currentNote).play;
				/*Pen.line(Point(0, 0), Point(0, depthLength.neg));
				Pen.stroke;
				Pen.translate(0, depthLength.neg);*/
			}
		);
		playCommands = (
			$F: { (dur: waitDur, note: currentNote.postln.wrap(-36,36), pan: -1).play; },
			$|: { (dur: waitDur, note: currentNote.postln.wrap(-36,36), pan: 1).play; }
		);
		commands= this.defaultCommands;
	}
	draw {
		n= 0;
		index= 0;
		if(lsystem.isString, {
			this.prDrawStr(lsystem, 0);
		}, {
			this.prDrawSys(lsystem.production, 0);
		});
	}
	defaultCommands {
		^(
			$F: {|depth, depthLength, index|
				Pen.color = Color.blue;
				Pen.line(Point(0, 0), Point(0, depthLength.neg));
				Pen.stroke;
				Pen.translate(0, depthLength.neg);
			},
			$G: {|depth, depthLength, index|
				Pen.translate(0, depthLength.neg);
			},
			$+: {
				Pen.rotate(theta/360*2pi);
			},
			$-: {
				Pen.rotate(theta/360* -2pi)
			},
			$[: {
				Pen.push;
			},
			$]: {
				Pen.pop;
			},
			$|: {|depth, depthLength, index|
				Pen.color = Color.red;
				Pen.line(Point(0, 0), Point(0, depthLength.neg));
				Pen.stroke;
				Pen.translate(0, depthLength.neg);
			}
		);
	}
	addCommand {|chr, func|
		commands.put(chr, func);
	}
	makeWindow {|bounds, initAngle= 0, initTranslate|
		initTranslate = initTranslate ? Point(0.5, 0.5);
		saveInitTranslate = initTranslate;
		saveInitAngle = initAngle;
		b= bounds ?? {Rect(100, 200, 700, 700)};
		win= Window(this.class.name, b, false);
		usr= UserView(win, Rect(0, 0, b.width, b.height));

		usr.drawFunc= {
			Pen.rotate(initAngle, b.width*0.5, b.height*0.5);
			Pen.translate(b.width*initTranslate.x, b.height*(1-initTranslate.y));
			this.draw;
		};
		win.front;
		^this;
	}

	animateNow {
		{
			var animSize = drawHist.size;
			animSize.do {|histIndex|
				{
					usr.drawFunc = {
						//Pen.color = Color.red;
						Pen.rotate(saveInitAngle, b.width*0.5, b.height*0.5);
						Pen.translate(b.width*saveInitTranslate.x, b.height*(1-saveInitTranslate.y));

						drawHist[..histIndex].do { |arr|
							var drawCmd = commands[arr[0][0]];
							var fargs = arr[0][1..3];
							if(drawCmd.notNil){
								drawCmd.value(*fargs);
							};
						};
						/*if(drawHist[histIndex][0][4] == $F){
						(dur: waitDur, note: currentNote.postln % 36).play;
						};*/
						//Pen.stroke;
					};
					usr.refresh;
				}.defer(0.1);
				//sound stuff
				currentNote = initNote;
				drawHist[..histIndex].do { |arr|
					var drawCmd = commands[arr[0][0]];
					var cmd = arr[0][4];
					if(drawCmd.notNil){
						noteCommands[cmd].value;
					};
				};
				if(playCommands[drawHist[histIndex][0][4]].notNil){
					playCommands[drawHist[histIndex][0][4]].value
				};
				waitDur.wait;
			}
		}.fork;
	}

	//--private
	prDrawStr {|x, depth|
		var depthLength;
		x.do{|chr, i|
			if(chr.isDecDigit, {
				n= n*10+x.digit;
			}, {
				var entry;
				depthLength= scale*length;			//depth cannot be calculated for strings
				preCommandAction.value(0, depthLength, i);
				entry = n.max(1).collect {
					[commands[chr],0, depthLength, i]
					//commands[chr].value(0, depthLength, i);
				};
				drawHist.add(entry);
				n= 0;
			});

		};
	}
	prDrawSys {|x, depth|
		var depthLength;
		if(x.size==0, {
			if(x.isArray, {^nil});
			if(x.isDecDigit, {
				n= n*10+x.digit;
			}, {
				var entry;
				depthLength= scale**depth*length;
				preCommandAction.value(depth, depthLength, index);
				entry = n.max(1).collect {
					[x,depth, depthLength, index, x]
				};
				drawHist.add(entry);
				n= 0;
				index= index+1;
			});
		}, {
			^x.do{|y| this.prDrawSys(y, depth+1)};
		});
	}
}
