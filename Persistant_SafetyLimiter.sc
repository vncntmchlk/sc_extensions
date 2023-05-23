// In a class file, eg MainHPF.sc
PersistantSafetyLimiter : PersistentMainFX{
	*synthFunc{
		^{|bus=0|
			var input = In.ar(bus, 2);
			var sig = SafetyLimiter.ar(input);
			ReplaceOut.ar(bus: bus,  channelsArray: sig);
		};
	}
}