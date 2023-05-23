+ ArrayedCollection	{

	sliceWrap { arg ... cuts;
		var firstCut, index, list;
		if (cuts.size == 0) { ^this.copy };

		firstCut = cuts[0];
		if (firstCut.isNil) { list = this.copy } { list = this.wrapAt(firstCut.asArray)};
		if (cuts.size == 1) {
			^list.unbubble
		}{
			cuts = cuts[1..];
			^list.collect {|item| item.sliceWrap(*cuts) }.unbubble
		}
	}
}
//Pbind(\dur, Pseq(".  .. . ".rhy(1)), \degree, Pseries()).play
+ String {
	rhy { |dur = 4|
		var dotIndices = List.new();
		var rests = List.new();
		var res;
		if(this.at(0) == $ ){dotIndices.add(0); rests.add(true)};
		this.do {|char, ix|
			if(char != $ ){dotIndices.add(ix); rests.add(char == $r)};
		};
		dotIndices.add(this.size);
		res = dotIndices[1..].asArray.differentiate.normalizeSum * dur;
		res = [res, rests].flop.collect {|arr| if(arr[1]){Rest(arr[0])}{arr[0]}};
		^res
	}
}