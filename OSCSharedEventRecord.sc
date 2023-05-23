OSCSharedEventRecord : Event {
	var myNetAddr, myPrefix, keepKeyFuncPairs, debugSendMsg, oscAddrDict, recordStarted, startTime, logList;
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
		recordStarted = false;
	}

	startRecord {
		recordStarted = true;
		startTime = Main.elapsedTime;
		logList = List.new;
		this.keysValuesDo {|k, v|
			this.logKeyValue(k, v);
		}
	}

	logKeyValue {|key, value|
		if(recordStarted){
			var timeNow = Main.elapsedTime - startTime;
			logList.add([timeNow, key, value]);
		}
	}

	stopRecord { |savePath|
		recordStarted = false;
		if(savePath.notNil){
			logList.writeArchive(savePath)
		};
	}

	playBackRoutine { |loadPath|
		if(recordStarted){
			"stop record first".warn;
		}{
			var playBackList = Object.readArchive(loadPath);
			^Routine {
				var lastTime = 0;
				playBackList.do {|arr|
					var time = arr[0];
					var key = arr[1];
					var value = arr[2];
					((time - lastTime) * thisThread.clock.tempo).wait;
					lastTime = time;
					// [key, value].postln;
					this.put(key, value);
				};
			}
		}
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
				var val = msg[1..].unbubble;
				this.logKeyValue(k, val);
				super.put(k, val)
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
				this.logKeyValue(k, val);
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
				var val = msg[1..].unbubble;
				this.logKeyValue(k, val);
				super.put(k, val)
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
				this.logKeyValue(k, val);
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
		this.logKeyValue(key, value);
		super.put(key, value);
	}
}