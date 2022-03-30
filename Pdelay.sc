// jamshark70

Pdelay : FilterPattern {
	var	<>delay, <>maxDelay, <>default;
	*new { |pattern, delay = 1, maxDelay = 1, default|
		^super.new(pattern).delay_(delay).maxDelay_(maxDelay).default_(default)
	}

	embedInStream { |inval|
		var	dly = delay.value(inval),
			bsize = max(maxDelay, dly) + 1,
			buffer = Array.fill(bsize, { default }),
			stream = pattern.asStream,
			writeI = 0, readI = dly.neg,
			item;
		while { (item = stream.next(inval)).notNil } {
			buffer.wrapPut(writeI, item);
			inval = (buffer.wrapAt(readI) ?? { buffer[0] }).yield;
			writeI = writeI + 1;
			readI = readI + 1;
		};
			// input stream ended but the last 'dly' items haven't been yielded yet
		dly.do { |x|
			inval = buffer.wrapAt(max(readI + x, 0)).yield;
		};
		^inval
	}
}