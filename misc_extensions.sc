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