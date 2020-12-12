// taken from H.J.Harkins

MIDIOutLegato : MIDIOut {
	var noteCount;

	*new { arg port, uid;
		if(thisProcess.platform.name != \linux) {
			^super.new(port, uid ?? { MIDIClient.destinations[port].uid }).init;
		} {
			^super.new(port, uid ?? 0).init;
		}
	}

	init {
		// channel --> note array
		// Unlikely to have 128 simultaneous note-ons for the same note number
		// so, save some space by using bytes
		noteCount = Array.fill(16, { Int8Array.fill(128, 0) });
	}

	noteOn { arg chan, note = 60, veloc = 64;
		noteCount[chan][note] = noteCount[chan][note] + 1;
		super.noteOn(chan, note, veloc);
	}

	noteOff { arg chan, note = 60, veloc = 64;
		noteCount[chan][note] = max(0, noteCount[chan][note] - 1);
		if(noteCount[chan][note] == 0) {
			super.noteOff(chan, note, veloc);
		};
	}

	clear {
		this.init;
	}
	allNotesOff { arg chan;
		super.allNotesOff(chan);
		this.init;
	}
}