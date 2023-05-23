PSloopQuant : Pattern {
	var <>psPat, <>lookBack, <>doSkip, <>loopFunc, <>loopFuncPerItem, <>loopFuncAsGoFunc, <>goFunc, <>indices, <>quantAddition;
	*new { |srcPat, length = inf, bufSize = 1, lookBack = 0, doSkip = 0,
		loopFunc, loopFuncPerItem = 0, loopFuncAsGoFunc = 0, goFunc, indices, copyItems = 0, copySets = 1, quantAddition = 0|
		^super.new.psPat_(PStream(srcPat, length, bufSize, copyItems, copySets))
		.lookBack_(lookBack).doSkip_(doSkip).loopFunc_(loopFunc)
		.loopFuncPerItem_(loopFuncPerItem).loopFuncAsGoFunc_(loopFuncAsGoFunc)
		.goFunc_(goFunc).indices_(indices).quantAddition_(quantAddition);
	}
	embedInStream { |inval|
		var psStream = psPat.asStream;
		var lookBackStream, lastVals, lookBackVal, oldLookBackVal, lookBackValClipped, loopLength,
		doSkipVal, count, mappedIndex, indicesStream, rawVal, mappedVal,
		currentIndices, currentIndexOrder, getCurrentIndexOrder, loopFuncStream, currentLoopFunc,
		loopFuncPerItemVal, goFuncStream, currentGoFunc, loopFuncAsGoFuncVal,
		quantAdditionVal, quantAdditionStream;

		getCurrentIndexOrder = { |nextIndices, indexbase|
			nextIndices.isKindOf(Function).if { nextIndices.(indexbase) }{ nextIndices }
		};

		lookBackStream = case
		{ lookBack.isKindOf(Function) }{ Pfunc(lookBack) }
		{ lookBack.isKindOf(Pattern) }{ lookBack }.asStream;

		indicesStream = case
		{ indices.isKindOf(Function) || indices.isKindOf(SequenceableCollection) }{ Pn(indices) }
		{ indices.isKindOf(Pattern) }{ indices }.asStream;

		loopFuncStream = case
		{ loopFunc.isKindOf(Function) }{ Pn(loopFunc) }
		{ loopFunc.isKindOf(Pattern) }{ loopFunc }.asStream;

		goFuncStream = case
		{ goFunc.isKindOf(Function) }{ Pn(goFunc) }
		{ goFunc.isKindOf(Pattern) }{ goFunc }.asStream;

		quantAdditionStream = case
		{ quantAddition.isKindOf(Function) }{ Pfunc(quantAddition) }
		{ quantAddition.isKindOf(Pattern) }{ quantAddition }.asStream;

		lookBackVal = lookBackStream.next;
		quantAdditionVal = quantAdditionStream.next;

		while {
			psPat.memoRoutine.notNil.if { lastVals = psPat.lastValues };
			lookBackVal.notNil
		}{
			(lookBackVal > 0).if {
				count = 0;
				lookBackValClipped = min(lookBackVal, psPat.bufSize - 1);
				currentIndices = indicesStream.next;
				currentIndexOrder = getCurrentIndexOrder.(currentIndices, lookBackValClipped);
				currentLoopFunc = loopFuncStream.next;

				while { lookBackVal > 0 }{
					lookBackValClipped = min(lookBackVal, psPat.bufSize - 1);
					currentIndices.notNil.if {
						mappedIndex = currentIndexOrder[count].clip(0, lookBackValClipped - 1);
						loopLength = currentIndexOrder.size;
					}{
						mappedIndex = count.mod(lookBackValClipped);
						loopLength = lookBackValClipped;
					};
					// index has to be reversed as PS buffers last at first
					rawVal = lastVals.at(lookBackValClipped - 1 - mappedIndex);
					mappedVal = currentLoopFunc.notNil.if { currentLoopFunc.(rawVal) }{ rawVal };

					if(count >= (loopLength - 1)){
						if(quantAdditionVal.notNil){
							mappedVal[\delta] = thisThread.clock.timeToNextBeat(quant: quantAdditionVal).postln;
						}
					};

					inval = mappedVal.embedInStream(inval);
					count = count + 1;

					loopFuncPerItemVal = loopFuncPerItem.();
					(loopFuncPerItemVal.miSC_check || (count >= loopLength)).if {
						currentLoopFunc = loopFuncStream.next
					};
					doSkipVal = doSkip.();
					(doSkipVal.miSC_check || (count >= loopLength)).if {
						oldLookBackVal = lookBackValClipped;
						lookBackVal = lookBackStream.next;
						lookBackValClipped = min(lookBackVal, psPat.bufSize - 1);

						quantAdditionVal = quantAdditionStream.next;

						((oldLookBackVal != lookBackValClipped) || (count >= loopLength)).if {
							count = 0;
							(lookBackVal != 0).if {
								currentIndices = indicesStream.next;
								currentIndexOrder = getCurrentIndexOrder.(currentIndices, lookBackValClipped);
							}
						}
					}
				}
			}{
				loopFuncAsGoFuncVal = loopFuncAsGoFunc.();
				currentGoFunc = loopFuncAsGoFuncVal.miSC_check.if { loopFuncStream }{ goFuncStream }.next;

				rawVal = psStream.next(inval);
				mappedVal = currentGoFunc.notNil.if { currentGoFunc.(rawVal) }{ rawVal };

				inval = mappedVal.embedInStream(inval);
				lookBackVal = lookBackStream.next;
				quantAdditionVal = quantAdditionStream.next;
			}
		};
		^inval;
	}
}