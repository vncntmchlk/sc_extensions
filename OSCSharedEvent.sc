OSCSharedEvent : Event {
	var myNetAddr, myPrefix, keepKeyFuncPairs, debugSendMsg, oscAddrDict;
	classvar <>all;

	*initClass {
		all = IdentityDictionary.new;
	}

	*new { | netAddr, prefix = "", postSendMsg = (false) |
		^super.new.initNetAddrAndPrefix(netAddr, prefix, postSendMsg)
	}

	*globalSendAll {
		all.do {|sharedEv| sharedEv.sendAll}
	}

	initNetAddrAndPrefix { |netAddr, prefix, postSendMsg|
		if(prefix.isEmpty.not){all[prefix.asSymbol] = this};
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
		if(debugSendMsg){
			"making OSCdefs for Addresses: ".postln;
		};
		//keys without function supplied
		(keys.as(Set) - keyFuncPairs.keys).do { |k|
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			if(debugSendMsg){addrStr.postln};
			OSCdef(addrStr.asSymbol, { |msg|
				super.put(k, msg[1..].unbubble)
			}, addrStr).fix
		};
		//keys with functions to execute when new value is received
		keyFuncPairs.keysValuesDo { |k, func|
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			if(debugSendMsg){addrStr.postln};
			OSCdef(addrStr.asSymbol, { |msg|
				var val = msg[1..].unbubble;
				// func will be evaluated first, so it is possible to use sharedEv.val as oldVal
				// and the argument as newVal
				func.(val);
				super.put(k, val);
			}, addrStr).fix
		};
		if(debugSendMsg){
			"remember to open correct udp port!".warn
		}
	}

	// for all keys
	makeOSCdefsForKeys { |keyFuncPairs = (Event.new())|
		keepKeyFuncPairs = keyFuncPairs;
		if(debugSendMsg){
			"making OSCdefs for Addresses: ".postln;
		};
		//keys without function supplied
		(this.keys.as(Set) - keyFuncPairs.keys).do { |k|
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			if(debugSendMsg){addrStr.postln};
			OSCdef(addrStr.asSymbol, { |msg|
				super.put(k, msg[1..].unbubble)
			}, addrStr).fix
		};
		//keys with functions to execute when new value is received
		keyFuncPairs.keysValuesDo { |k, func|
			var addrStr = "/" +/+ myPrefix +/+ k;
			oscAddrDict[k] = addrStr;
			if(debugSendMsg){addrStr.postln};
			OSCdef(addrStr.asSymbol, { |msg|
				var val = msg[1..].unbubble;
				func.(val);
				super.put(k, val);
			}, addrStr).fix
		};
		if(debugSendMsg){
			"remember to open correct udp port!".warn
		}
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