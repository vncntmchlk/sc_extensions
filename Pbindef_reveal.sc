/*
Pbindef('').reveal will insert the keys and values which are save in the Pbindef into the current line of the document. Might be helpful during live coding to quickly edit Pbindefs
*/
+ Pbindef {
	reveal {
		var myTxt = "";
		var replaceText = { |txt selSize|
			var boiler = 18; // "Pbindef('').reveal" sind 18 zeichen
			selSize = selSize + boiler;
			Document.current.selectionStart;
			Document.current.string_(
				txt,
				Document.current.selectionStart - selSize,
				selSize
			)
		};
		this.repositoryArgs[1..].pairsDo { |a b|
			myTxt = myTxt ++ "Pbindef(%, %, %)\n".format(key.asCompileString, a.asCompileString, b.asCompileString)
		};
		replaceText.(myTxt, key.asString.size);
	}
}
