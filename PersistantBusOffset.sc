PersistantBusOffset : PersistentMainFX{
	*synthFunc{
		^{|busOffset = 12|
			var numChn = 8;
			var sig = In.ar(0, numChn);
			ReplaceOut.ar(0, DC.ar(0) ! numChn);
			Out.ar(bus: busOffset,  channelsArray: sig);
		};
	}
}