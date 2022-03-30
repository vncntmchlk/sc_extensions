/*
().reveal will insert the keys and values which are save in the Pbindef into the current line of the document. Might be helpful during live coding to quickly edit Pbindefs
*/
+ Dictionary {
	reveal {
		var doc = Document.current;
		var lineStartEnd = doc.getCurrentLineStartEnd;
		var myKey = if(this.envirKey.notNil){
			this.envirCompileString
		}{
			var ret;
			//doc.selectRange(lineStartEnd[0], lineStartEnd[1]);
			ret = doc.currentLine;
			if(ret.isEmpty){
				doc.selectRange(lineStartEnd[1] - 1, 0);
				ret = doc.currentLine;
			};
			ret[..ret.size - 8] // hier .reveal raushauen
		}; // check ob es ~bla oder ~bla.blub ist;
		var myTxt = "";
		this.asSortedArray.do {|arr|
			myTxt = myTxt ++ "%% = %;\n".format(myKey, arr[0].asString, arr[1].asCompileString)
		};
		doc.string_(
			myTxt,
			lineStartEnd[0],
			lineStartEnd[1] - lineStartEnd[0]
		)
	}
}
/*+ Dictionary {
reveal {
var doc = Document.current;
var initialLine = doc.selectionStart;
var myKey = if(this.envirKey.notNil){
this.envirCompileString
}{
var ret;
doc.selectRange(initialLine, initialLine);
ret = doc.currentLine;
ret[..ret.size - 8] // hier .reveal raushauen
};// check ob es ~bla oder ~bla.blub ist;
var myTxt = "%.putAll((".format(myKey);
var replaceText = { |txt selSize|
// doc.selectionStart;
doc.string_(
txt,
initialLine - selSize - 7, // hier auch .reveal raushauen
selSize + 8
)
};
/*this.keysValuesDo { |k, v|
myTxt = myTxt ++ "\n\t%: %,".format(k.asCompileString, v.asCompileString)
};*/
this.asSortedArray.do {|arr|
myTxt = myTxt ++ "\n\t%: %,".format(arr[0].asCompileString, arr[1].asCompileString)
};
myTxt = myTxt ++ "\n))\n";
replaceText.(myTxt, myKey.size);
doc.selectRange(initialLine - myKey.size, 1);
}
}*/
