OSCSharedEvent : Event {
	var myNetAddr, myPrefix, keepKeyFuncPairs, debugSendMsg, oscAddrDict;
	*new { | netAddr, prefix = "", postSendMsg = (false) |
		^super.new.initNetAddrAndPrefix(netAddr, prefix, postSendMsg)
	}

	initNetAddrAndPrefix { |netAddr, prefix, postSendMsg|
		myNetAddr = netAddr;
		myPrefix = prefix;
		debugSendMsg = postSendMsg;
		keepKeyFuncPairs = (Event.new());
		oscAddrDict = Dictionary.new();
	}

	oscAction { |k, v|
		if(debugSendMsg){
			if(oscAddrDict.keys.includes(k)){
				"OSCSharedEvent sending: ".post;
				myNetAddr.sendMsg(oscAddrDict[k].postln, *v)
			}{
				("OSCSharedEvent NOT sending " ++ myPrefix +/+ k).postln;
			}
		}{
			if(oscAddrDict.keys.includes(k)){
				myNetAddr.sendMsg(oscAddrDict[k], *v)
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
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			OSCdef(addrStr.asSymbol, { |msg|
				super.put(k, msg[1..].unbubble)
			}, addrStr.postln).fix
		};
		//keys with functions to execute when new value is received
		keyFuncPairs.keysValuesDo { |k, func|
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			OSCdef(addrStr.asSymbol, { |msg|
				var val = msg[1..].unbubble;
				// func will be evaluated first, so it is possible to use sharedEv.val as oldVal
				// and the argument as newVal
				func.(val);
				super.put(k, val);
			}, addrStr.postln).fix
		};
		"remember to open correct udp port!".warn
	}

	// for all keys
	makeOSCdefsForKeys { |keyFuncPairs = (Event.new())|
		keepKeyFuncPairs = keyFuncPairs;
		"making OSCdefs for Addresses: ".postln;
		//keys without function supplied
		(this.keys.as(Set) - keyFuncPairs.keys).do { |k|
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			OSCdef(addrStr.asSymbol, { |msg|
				super.put(k, msg[1..].unbubble)
			}, addrStr.postln).fix
		};
		//keys with functions to execute when new value is received
		keyFuncPairs.keysValuesDo { |k, func|
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			OSCdef(addrStr.asSymbol, { |msg|
				var val = msg[1..].unbubble;
				func.(val);
				super.put(k, val);
			}, addrStr.postln).fix
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