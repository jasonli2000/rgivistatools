package com.raygroupintl.vista.mtoken;

import com.raygroupintl.fnds.IToken;
import com.raygroupintl.fnds.ITokenFactory;

public class TFComment implements ITokenFactory {
	@Override
	public IToken tokenize(String line, int fromIndex) {
		int endIndex = line.length();
		if (fromIndex < endIndex) {
			char ch = line.charAt(fromIndex);
			if (ch == ';') {
				//int index = fromIndex + 1;
				//while (index < endIndex) {
				//	char chLoop = line.charAt(index);
				//	if ((chLoop == '\n') || (chLoop == '\r')) break;
				//	++index;
				//}
				int index = endIndex;
				String commentContent = line.substring(fromIndex+1, index);
				return new TComment(commentContent);
			}
		}
		return null;
	}
}
