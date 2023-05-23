/*
sort dictionary keys
*/
+ JSON {
	*stringifySort { arg obj;
		var out, outKeys, outValues, order;

		if(obj.isString, {
			^obj.asCompileString.reject(_.isControl).replace("\n", JSON.nl).replace("\t", JSON.tab);
		});
		if(obj.class === Symbol, {
			^JSON.stringify(obj.asString)
		});

		if(obj.isKindOf(Dictionary), {
			out = List.new;	outKeys = List.new; outValues = List.new;
			obj.keysValuesDo({ arg key, value;
				outKeys.add(key.asString.asCompileString);
				outValues.add(JSON.stringifySort(value));
				//out.add( key.asString.asCompileString ++ ":" + JSON.stringify(value) );
			});
			order = outKeys.order;
			order.do {|ix|
				out.add( outKeys[ix] ++ ":" + outValues[ix] );
			};
			^("{" ++ (out.join(", ")) ++ "}");
		});

		if(obj.isNil, {
			^"null"
		});
		if(obj === true, {
			^"true"
		});
		if(obj === false, {
			^"false"
		});
		if(obj.isNumber, {
			if(obj.isNaN, {
				^"null"
			});
			if(obj === inf, {
				^"null"
			});
			if(obj === (-inf), {
				^"null"
			});
			^obj.asString
		});
		if(obj.isKindOf(SequenceableCollection), {
			^"[" ++ obj.collect({ arg sub;
				JSON.stringify(sub)
			}).join(", ")
			++ "]";
		});

		// obj.asDictionary -> key value all of its members

		// datetime
		// "2010-04-20T20:08:21.634121"
		// http://en.wikipedia.org/wiki/ISO_8601

		("No JSON conversion for object" + obj).warn;
		^JSON.stringify(obj.asCompileString)
	}
}
