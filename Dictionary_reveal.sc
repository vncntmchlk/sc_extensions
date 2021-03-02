/*
().reveal will insert the keys and values which are save in the Pbindef into the current line of the document. Might be helpful during live coding to quickly edit Pbindefs
*/
+ Dictionary {
	reveal {
		var myKey = if(this.envirKey.notNil){
			this.envirCompileString
		}{
			var doc, ret;
			doc = Document.current;
			doc.selectRange(doc.selectionStart, doc.selectionStart);
			ret = doc.currentLine;
			ret[..ret.size - 8] // hier .reveal raushauen
		};// check ob es ~bla oder ~bla.blub ist;
		var myTxt = "% = (".format(myKey);
		var replaceText = { |txt selSize|
			Document.current.selectionStart;
			Document.current.string_(
				txt,
				Document.current.selectionStart - selSize - 7, // hier auch .reveal raushauen
				selSize + 8
			)
		};
		this.keysValuesDo { |k, v|
			myTxt = myTxt ++ "\n\t%: %,".format(k.asCompileString, v.asCompileString)
		};
		myTxt = myTxt ++ "\n)";
		replaceText.(myTxt, myKey.size);
	}
}
