Ptempo : Pattern {
	asStream { ^FuncStream({ thisThread.clock.tryPerform(\tempo) ?? { 1 } }) }
}

PbeatAccents : Pattern {
	var	<>beatAccents, <>noAccentVal;
	*new { |beatAccents = (Event.new()), noAccentVal = -15|
		^super.newCopyArgs(beatAccents, noAccentVal)
	}
	embedInStream { |inval|
		var startBeat = thisThread.clock.beats; // should have option to be 0
		var noAccentStream = noAccentVal.asStream;
		var beatAccentStream;
		beatAccents.keys.select(_.isInteger).do {|k|
			beatAccents.put(k.asFloat, beatAccents.at(k)); // keys must be float
			beatAccents.removeAt(k)
		};
		beatAccents.keysValuesChange {|k, v| v.asStream};
		loop {
			var beat = ((thisThread.clock.beats - startBeat) % 1).round(0.00000001);
			inval = beatAccents[beat].next ?? {noAccentStream.next};
			inval.yield
		}
	}
}
