package com.raygroupintl.vista.mtoken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.raygroupintl.bnf.ChoiceSupply;
import com.raygroupintl.bnf.TBasic;
import com.raygroupintl.bnf.TEmpty;
import com.raygroupintl.bnf.TFBasic;
import com.raygroupintl.bnf.TFChar;
import com.raygroupintl.bnf.TFChoice;
import com.raygroupintl.bnf.TFConstChar;
import com.raygroupintl.bnf.TFConstChars;
import com.raygroupintl.bnf.TFEmptyVerified;
import com.raygroupintl.bnf.TFSeq;
import com.raygroupintl.bnf.TFSeqORO;
import com.raygroupintl.bnf.TFSeqRO;
import com.raygroupintl.bnf.TFSeqROO;
import com.raygroupintl.bnf.TFSeqRequired;
import com.raygroupintl.bnf.TSyntaxError;
import com.raygroupintl.fnds.IToken;
import com.raygroupintl.fnds.ITokenFactory;
import com.raygroupintl.fnds.ITokenFactorySupply;
import com.raygroupintl.vista.struct.MError;
import com.raygroupintl.vista.struct.MNameWithMnemonic;

public class TFCommand extends TFSeq {
	private MVersion version;
	
	private TFCommand(MVersion version) {
		this.version = version;
	}
		
	private static MNameWithMnemonic.Map COMMAND_NAMES = new MNameWithMnemonic.Map();
	static {
		COMMAND_NAMES.update("B", "BREAK"); 	
		COMMAND_NAMES.update("H", "HALT"); 	
		COMMAND_NAMES.update("H", "HANG"); 	
		COMMAND_NAMES.update("V", "VIEW"); 	
		COMMAND_NAMES.update("E", "ELSE"); 	
		COMMAND_NAMES.update("TC", "TCOMMIT"); 	
		COMMAND_NAMES.update("TR", "TRESTART"); 	
		COMMAND_NAMES.update("TRO", "TROLLBACK"); 	
		COMMAND_NAMES.update("TS", "TSTART"); 	
		COMMAND_NAMES.update("C", "CLOSE"); 	
		COMMAND_NAMES.update("D", "DO"); 	
		COMMAND_NAMES.update("E", "XECUTE"); 	
		COMMAND_NAMES.update("F", "FOR"); 	
		COMMAND_NAMES.update("G", "GOTO"); 	
		COMMAND_NAMES.update("I", "IF"); 	
		COMMAND_NAMES.update("J", "JOB"); 	
		COMMAND_NAMES.update("K", "KILL"); 	
		COMMAND_NAMES.update("L", "LOCK"); 	
		COMMAND_NAMES.update("M", "MERGE"); 	
		COMMAND_NAMES.update("N", "NEW");		
		COMMAND_NAMES.update("O", "OPEN"); 	
		COMMAND_NAMES.update("Q", "QUIT"); 	
		COMMAND_NAMES.update("R", "READ"); 	
		COMMAND_NAMES.update("S", "SET"); 	
		COMMAND_NAMES.update("U", "USE");
		COMMAND_NAMES.update("W", "WRITE");		
		COMMAND_NAMES.update("X", "XECUTE");		
	}
	
	public static ITokenFactory getTFPostCondition(IToken[] previousTokens, MVersion version) {
		ITokenFactory tfColon = TFConstChar.getInstance(':');
		ITokenFactory tfExpr = TFExpr.getInstance(version);
		return TFSeqRequired.getInstance(tfColon, tfExpr);
	}

	private static ITokenFactory getXArgumentFactory(MVersion version) {
		ITokenFactory tf = ChoiceSupply.get(TFExpr.getInstance(version), '@', TFIndirection.getInstance(version));
		ITokenFactory pc = getTFPostCondition(null, version);
		return TFDelimitedList.getInstance(TFSeqRO.getInstance(tf, pc), ',');
	}
	
	private static ITokenFactory getFArgumentFactory(MVersion version) {
		TFExpr tfExpr = TFExpr.getInstance(version);
		TFSeqRequired tfFromTo = TFSeqRequired.getInstance(TFConstChar.getInstance(':'), tfExpr);
		ITokenFactory RHS = TFSeqROO.getInstance(tfExpr, tfFromTo, tfFromTo);
		ITokenFactory RHSs = TFCommaDelimitedList.getInstance(RHS);
		return TFSeqRequired.getInstance(TFLvn.getInstance(version), TFConstChar.getInstance('='), RHSs); 
	}

	private static ITokenFactory getLArgumentFactory(MVersion version) {
		ITokenFactory tfNRef = ChoiceSupply.get(TFLvn.getInstance(version), "^@", TFGvn.getInstance(version), TFIndirection.getInstance(version));		
		ITokenFactory tfNRefOrList = ChoiceSupply.get(tfNRef, '(', TFDelimitedList.getInstance(tfNRef, ',', true));
		ITokenFactory e = TFSeqORO.getInstance(TFConstChars.getInstance("+-"), tfNRefOrList, TFTimeout.getInstance(version));
		return TFCommaDelimitedList.getInstance(e);
	}


	
	public static void addCommand(String name) {
		COMMAND_NAMES.update(name, name);
	}
	
	private static class TCommandName extends TKeyword {
		private TCommandName(String value) {
			super(value);
		}
	
		@Override
		public List<MError> getErrors() {
			return null;
		}
		
		@Override
		protected MNameWithMnemonic getNameWithMnemonic() {
			String value = this.getStringValue().toUpperCase();
			MNameWithMnemonic name = COMMAND_NAMES.get(value);
			return name;
		}
	}
	
	private static class TFGeneric implements ITokenFactory {
		@Override
		public IToken tokenize(String line, int fromIndex) {
			int endIndex = line.length();
			int index = fromIndex;
			boolean inQuotes = false;
			while (index < endIndex) {
				char ch = line.charAt(index);
								
				if (ch == '"') {
					inQuotes = ! inQuotes;
				} else if (ch == ' ') {
					if (! inQuotes) break;
				} else if ((ch == '\r') || (ch == '\n')) {
					break;
				}
				++index;
			}
			if (index > fromIndex) {
				return new TBasic(line.substring(fromIndex, index));
			} else {
				return new TEmpty();
			}
		}
	}
		
	private static class TFCommandName extends TFIdent {
		@Override
		public IToken tokenize(String line, int fromIndex) {
			IToken result = super.tokenize(line, fromIndex);
			String cmdName = result.getStringValue().toUpperCase();
			if (COMMAND_NAMES.containsKey(cmdName)) {
				return new TCommandName(result.getStringValue());
			} else {
				return new TSyntaxError(MError.ERR_UNDEFINED_COMMAND , line, fromIndex);
			}			
		}
				
		public static TFCommandName getInstance() {
			return new TFCommandName();
		}
	}
		
	private static class TFSCommand implements ITokenFactorySupply {
		private static Map<String, ITokenFactory> ARGUMENT_FACTORIES;
		private static void initialize(final MVersion version) {
			ARGUMENT_FACTORIES = new HashMap<String, ITokenFactory>();
			TFEmptyVerified empty = TFEmptyVerified.getInstance(' ');
			ARGUMENT_FACTORIES.put("E", empty); 	
			ARGUMENT_FACTORIES.put("TC", empty); 	
			ARGUMENT_FACTORIES.put("TR", empty); 	
			ARGUMENT_FACTORIES.put("TRO", empty); 	
			ARGUMENT_FACTORIES.put("TS", empty); 	
			
			ITokenFactory c = TFCommaDelimitedList.getInstance(new TFChoice() {			
				@Override
				protected ITokenFactory getFactory(char ch) {
					if (ch == '@') {
						return TFIndirection.getInstance(version);
					} else {
						return TFSeqRO.getInstance(TFExpr.getInstance(version), TFSeqRequired.getInstance(TFConstChar.getInstance(':'), new TFDeviceParams(version)));
					}
				}
			});
			ARGUMENT_FACTORIES.put("C", c); 	
			
			
			ARGUMENT_FACTORIES.put("D", TFCommaDelimitedList.getInstance(TFDoArgument.getInstance(version)));		
			ARGUMENT_FACTORIES.put("X", getXArgumentFactory(version)); 	
			ARGUMENT_FACTORIES.put("F", getFArgumentFactory(version)); 	
			ARGUMENT_FACTORIES.put("G", TFCommaDelimitedList.getInstance(TFGotoArgument.getInstance(version))); 	
			ARGUMENT_FACTORIES.put("I", TFCommaDelimitedList.getInstance(TFExpr.getInstance(version))); 	
			ARGUMENT_FACTORIES.put("J", TFCommaDelimitedList.getInstance(TFJobArgument.getInstance(version))); 	
			ARGUMENT_FACTORIES.put("K", TFCommaDelimitedList.getInstance(TFKillArgument.getInstance(version))); 	
			ARGUMENT_FACTORIES.put("L", getLArgumentFactory(version)); 	
			ARGUMENT_FACTORIES.put("M", TFCommaDelimitedList.getInstance(TFMergeArgument.getInstance(version))); 	
			
			ITokenFactory n = new TFChoice() {
				@Override
				protected ITokenFactory getFactory(char ch) {
					switch(ch) {
					case '(': 
						return TFCommaDelimitedList.getInstance(TFLvn.getInstance(version));
					case '@':
						return TFIndirection.getInstance(version);
					case '$':
						return TFIntrinsic.getInstance(version);
					default:
						return TFName.getInstance();
					}
				}
			};		
			ARGUMENT_FACTORIES.put("N", TFCommaDelimitedList.getInstance(n));		
			
			ARGUMENT_FACTORIES.put("O", TFCommaDelimitedList.getInstance(TFOpenArgument.getInstance(version))); 	
			ARGUMENT_FACTORIES.put("Q", TFExpr.getInstance(version)); 	
			ARGUMENT_FACTORIES.put("R", TFCommaDelimitedList.getInstance(TFReadArgument.getInstance(version))); 	
			ARGUMENT_FACTORIES.put("S", TFCommaDelimitedList.getInstance(TFSetArgument.getInstance(version))); 	
			ARGUMENT_FACTORIES.put("U", TFCommaDelimitedList.getInstance(TFUseArgument.getInstance(version)));
			
			ITokenFactory w = new TFChoice() {
				@Override
				protected ITokenFactory getFactory(char ch) {
					switch(ch) {
						case '!':
						case '#':
						case '?':
							return TFFormat.getInstance(version);
						case '/':
							return TFSeqRequired.getInstance(TFChar.SLASH, TFName.getInstance(), TFActualList.getInstance(version));
						case '*':
							return TFSeqRequired.getInstance(TFConstChar.getInstance('*'), TFExpr.getInstance(version));
						case '@':
							return TFIndirection.getInstance(version);
						default:
							return TFExpr.getInstance(version);
					}
				}
			};		
			ARGUMENT_FACTORIES.put("W", TFCommaDelimitedList.getInstance(w));		
		}
		
		private MVersion version;
		
		private TFSCommand(MVersion version) {
			this.version = version;
		}
			
		public ITokenFactory get(int seqIndex, IToken[] previousTokens) {
			switch (seqIndex) {
				case 0:
					return TFCommandName.getInstance();
				case 1:
					return TFSeqRequired.getInstance(TFConstChar.getInstance(':'), TFExpr.getInstance(this.version));
				case 2:
					return TFConstChar.getInstance(' ');
				case 3: {
					TCommandName cmd = (TCommandName) previousTokens[0];
					String key = cmd.getNameWithMnemonic().getMnemonic();
					if (ARGUMENT_FACTORIES == null) {
						initialize(this.version);
					}
					ITokenFactory f = ARGUMENT_FACTORIES.get(key);
					if (f == null) {
						return new TFGeneric();
					} else {
						return f;
					}
				}					
				case 4:
					return TFBasic.getInstance(' ');
				default:
					assert(seqIndex == 5);
					return null;
			}
		}
		
		public int getCount() {
			return 5;
		}
	}
	
	@Override
	protected ITokenFactorySupply getFactorySupply() {
		return new TFSCommand(this.version);
	}

	@Override
	protected int validateNull(int seqIndex, IToken[] foundTokens) {
		if (seqIndex == 0) {
			return RETURN_NULL;
		}
		//if (seqIndex == 2) {
		//	return this.getErrorCode();
		//}
		return CONTINUE;				
	}

	@Override
	protected int validateEnd(int seqIndex, IToken[] foundTokens) {
		return 0;
	}

	public static TFCommand getInstance(MVersion version) {
		return new TFCommand(version);
	}
}
