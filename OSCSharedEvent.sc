// diese "/" +/+ myPrefix +/+ k dinger waere besser in nem dict zu speichern bei kreation fuer performance? wahrscheinlich wurscht, bench sagt time to run: 4.3013999999175e-05 seconds.

OSCSharedEvent : Event {
	var myNetAddr, myPrefix, keepKeyFuncPairs, debugSendMsg;
	*new { | netAddr, prefix = "", postSendMsg = (false) |
		^super.new.initNetAddrAndPrefix(netAddr, prefix, postSendMsg)
	}

	initNetAddrAndPrefix { |netAddr, prefix, postSendMsg|
		myNetAddr = netAddr;
		myPrefix = prefix;
		debugSendMsg = postSendMsg;
		keepKeyFuncPairs = (Event.new())
	}

	oscAction { |k, v|
		if(debugSendMsg){
			if(OSCdef.all.keys.includes(("/" +/+ myPrefix +/+ k).asSymbol)){
				"OSCSharedEvent sending: ".post;
				myNetAddr.sendMsg(("/" +/+ myPrefix +/+ k).postln, *v)
			}{
				("OSCSharedEvent NOT sending " ++ myPrefix +/+ k).postln;
			}
		}{
			if(OSCdef.all.keys.includes(("/" +/+ myPrefix +/+ k).asSymbol)){
				myNetAddr.sendMsg(("/" +/+ myPrefix +/+ k), *v)
			}
		}
	}

	//getPrefix { ^myPrefix }

	// for specified keys
	makeOSCdefs { |keys, keyFuncPairs = (Event.new())|
		keepKeyFuncPairs = keyFuncPairs;
		"making OSCdefs for Addresses: ".postln;
		//keys without function supplied
		(keys.as(Set) - keyFuncPairs.keys).do { |k|
			OSCdef(("/" +/+ myPrefix +/+ k).asSymbol, { |msg|
				super.put(k, msg[1..].unbubble)
			}, ("/" +/+ myPrefix +/+ k).postln).fix
		};
		//keys with functions to execute when new value is received
		keyFuncPairs.keysValuesDo { |k, func|
			OSCdef(("/" +/+ myPrefix +/+ k).asSymbol, { |msg|
				var val = msg[1..].unbubble;
				// func will be evaluated first, so it is possible to use sharedEv.val as oldVal
				// and the argument as newVal
				func.(val);
				super.put(k, val);
			}, ("/" +/+ myPrefix +/+ k).postln).fix
		};
		"remember to open correct udp port!".warn
	}

	// for all keys
	makeOSCdefsForKeys { |keyFuncPairs = (Event.new())|
		keepKeyFuncPairs = keyFuncPairs;
		"making OSCdefs for Addresses: ".postln;
		//keys without function supplied
		(this.keys.as(Set) - keyFuncPairs.keys).do { |k|
			OSCdef(("/" +/+ myPrefix +/+ k).asSymbol, { |msg|
				super.put(k, msg[1..].unbubble)
			}, ("/" +/+ myPrefix +/+ k).postln).fix
		};
		//keys with functions to execute when new value is received
		keyFuncPairs.keysValuesDo { |k, func|
			OSCdef(("/" +/+ myPrefix +/+ k).asSymbol, { |msg|
				var val = msg[1..].unbubble;
				func.(val);
				super.put(k, val);
			}, ("/" +/+ myPrefix +/+ k).postln).fix
		};
		"remember to open correct udp port!".warn
	}

	sendAll {
		this.keysValuesDo {|k, v| this.oscAction(k, v)}
	}

	put { |key, value|
		//check if there is a func
		if(keepKeyFuncPairs[key].notNil){
			keepKeyFuncPairs[key].(value)
		};
		this.oscAction(key, value);
		//};
		super.put(key, value);
	}
}