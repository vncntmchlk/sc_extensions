+ Pbindef {
	reveal {
		var myTxt = "";
		var replaceText = { |txt selSize|
			var boiler = 18; // "Pbindef('').reveal" sind 18 zeichen
			selSize = selSize + boiler;
			// var selSize = txt.size.postln + boiler;
			Document.current.selectionStart;
			Document.current.string_(
				txt,
				Document.current.selectionStart - selSize,
				selSize
			)
		};
		this.repositoryArgs[1..].pairsDo { |a b|
			myTxt = myTxt ++ "\nPbindef(%, %, %)".format(key.asCompileString, a.asCompileString, b.asCompileString)
		};
		replaceText.(myTxt, key.asString.size);
		// ^collectList
	}
}
