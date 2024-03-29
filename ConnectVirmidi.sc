/*
Create a new MIDIOut and connect it to VirMIDI (linux virtual midi)
*/
ConnectVirmidi {
	var mOut;
	*new { |latency = 0.2, legato = true|
		^super.new.init(latency, legato)
	}

	init { |latency, legato|
		if(MIDIClient.initialized.not){
			MIDIClient.init;
		};
		if(legato){
			mOut = MIDIOutLegato(0).latency_(latency);
			CmdPeriod.add({mOut.clear});
			"MIDIOutLegato .. evtl. \cleanupFunc, {~mOut.clear} adden".postln;
		}{
			mOut = MIDIOut(0).latency_(latency);
		};
		if(MIDIClient.destinations.select{|m| m.name.includesStr("VirMIDI", false)}.notEmpty){
			mOut.connect(MIDIClient.destinations.select{|m| m.name.includesStr("VirMIDI", false)}[0]);
		}{
			"couldnt connect to virmidi".warn;
		};
		("Midi latency: " ++ latency).postln;
		^mOut
	}
}