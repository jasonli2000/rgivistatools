package com.raygroupintl.parser;

import java.lang.reflect.Constructor;
import java.util.List;

import com.raygroupintl.parser.annotation.AdapterSupply;

public class TFSequence extends TFBasic {
	public enum ValidateResult {
		CONTINUE, BREAK, NULL_RESULT
	}

	private final static class RequiredFlags {
		private boolean[] flags;
		private int firstRequired = Integer.MAX_VALUE;
		private int lastRequired = Integer.MIN_VALUE;

		public RequiredFlags() {
			this(0);
		}

		public RequiredFlags(int size) {
			flags = new boolean[size];
		}

		public void set(boolean[] flags) {
			this.flags = flags;
			this.firstRequired = Integer.MAX_VALUE;			
			this.lastRequired = Integer.MIN_VALUE;			
			int index = 0;
			for (boolean b : flags) {
				if (b) {
					if (this.firstRequired == Integer.MAX_VALUE) {
						this.firstRequired = index;
					}
					this.lastRequired = index;
				}
				++index;
			}		
		}
		
		public int getFirstRequiredIndex() {
			return this.firstRequired;
		}
		
		public int getLastRequiredIndex() {
			return this.lastRequired;
		}
		
		public boolean isRequired(int i) {
			return this.flags[i];
		}		
	}
	
	private static final SequenceAdapter DEFAULT_ADAPTER = new SequenceAdapter() {		
		@Override
		public TSequence convert(List<Token> tokens) {
			return new TSequence(tokens);
		}
	}; 
	
	static class TFSequenceCopy extends TFBasic {
		private SequenceAdapter adapter;
		private TFSequence slave;
		
		private TFSequenceCopy(String name, TFSequence slave) {
			super(name);
			this.slave = slave;
		}
		
		@Override
		public final TSequence tokenize(Text text, AdapterSupply adapterSupply) throws SyntaxErrorException {
			TSequence result = this.slave.tokenize(text, adapterSupply);
			if ((result == null) || (this.adapter == null)) {
				return result;
			}
			return this.adapter.convert(result.toList());
		}
		
		public TSequence convert(TSequence result) {
			if ((result == null) || (this.adapter == null)) {
				return result;
			}
			return this.adapter.convert(result.toList());			
		}
		
		@Override
		public void setTargetType(Class<? extends Token> cls) {
			this.adapter = getAdapter(cls);
		}

		@Override
		public void setAdapter(Object adapter) {
			this.adapter = getAdapter(adapter);					
		}	
		
		@Override
		public void copyWoutAdapterFrom(TFBasic rhs) {
			if (rhs instanceof TFSequenceCopy) {
				TFSequenceCopy rhsCasted = (TFSequenceCopy) rhs;
				this.slave = rhsCasted.slave;
			} else {
				throw new IllegalArgumentException("Illegal attemp to copy from " + rhs.getClass().getName() + " to " + TFSequenceCopy.class.getName());
			}
		}

		@Override
		public TFBasic getCopy(String name) {
			return new TFSequenceCopy(name, this.slave);
		}		
		
		@Override
		public boolean isInitialized() {
			return this.slave.isInitialized();
		}	
		
		@Override
		protected TokenFactory getLeadingFactory() {
			return this.slave;
		}		
	}
		
	private TokenFactory[] factories = {};
	private RequiredFlags requiredFlags = new RequiredFlags();
	private SequenceAdapter adapter;
	
	public TFSequence(String name) {		
		this(name, DEFAULT_ADAPTER);
	}
	
	public TFSequence(String name, SequenceAdapter adapter) {		
		super(name);
		this.adapter = adapter == null ? DEFAULT_ADAPTER : adapter;
	}
	
	public TFSequence(String name, SequenceAdapter adapter, TokenFactory... factories) {
		this(name, adapter);
		this.factories = factories;
		this.requiredFlags = new RequiredFlags(factories.length);
	}
		
	public TFSequence(String name, TokenFactory... factories) {
		this(name);
		this.factories = factories;
		this.requiredFlags = new RequiredFlags(factories.length);
	}
	
	@Override
	protected TokenFactory getLeadingFactory() {
		return this.factories[0];
	}
				
	@Override
	public void copyWoutAdapterFrom(TFBasic rhs) {
		if (rhs instanceof TFSequence) {
			TFSequence rhsCasted = (TFSequence) rhs;
			this.factories = rhsCasted.factories;
			this.requiredFlags = rhsCasted.requiredFlags;
		} else {
			throw new IllegalArgumentException("Illegal attemp to copy from " + rhs.getClass().getName() + " to " + TFSequence.class.getName());
		}
	}

	@Override
	public TFBasic getCopy(String name) {
		return new TFSequenceCopy(name, this);
	}

	@Override
	public boolean isInitialized() {
		return this.factories.length > 0;
	}
	
	public void setFactories(TokenFactory[] factories, boolean[] requiredFlags) {
		if (requiredFlags.length != factories.length) throw new IllegalArgumentException();
		this.factories = factories;
		this.requiredFlags.set(requiredFlags);
	}

	public void setRequiredFlags(boolean... requiredFlags) {
		if (requiredFlags.length != this.factories.length) throw new IllegalArgumentException();
		this.requiredFlags.set(requiredFlags);
	}
	
	public void setAdapter(SequenceAdapter adapter) {
		this.adapter = adapter;
	}

	public int getSequenceCount() {
		return this.factories.length;
	}
	
	protected ValidateResult validateNext(int seqIndex, TokenStore foundTokens, Token nextToken, boolean noException) throws SyntaxErrorException {
		return ValidateResult.CONTINUE;
	}

	protected ValidateResult validateNull(int seqIndex, TokenStore foundTokens, boolean noException) throws SyntaxErrorException {
		int firstRequired = this.requiredFlags.getFirstRequiredIndex();
		int lastRequired = this.requiredFlags.getLastRequiredIndex();
		
		if ((seqIndex < firstRequired) || (seqIndex > lastRequired)) {
			return ValidateResult.CONTINUE;
		}		
		if (seqIndex == firstRequired) {
			if (noException) return ValidateResult.NULL_RESULT;
			for (int i=0; i<seqIndex; ++i) {
				if (foundTokens.get(i) != null) {
					throw new SyntaxErrorException();
				}
			}
			return ValidateResult.NULL_RESULT;
		}
		if (this.requiredFlags.isRequired(seqIndex)) {
			if (noException) return ValidateResult.NULL_RESULT;
			throw new SyntaxErrorException();
		} else {
			return ValidateResult.CONTINUE;
		}
	}
	
	protected boolean validateEnd(int seqIndex, TokenStore foundTokens, boolean noException) throws SyntaxErrorException {
		if (seqIndex < this.requiredFlags.getLastRequiredIndex()) {
			if (noException) return false;
			throw new SyntaxErrorException();
		}
		return true;
	}
	
	private ValidateResult validate(int seqIndex,TokenStore foundTokens, Token nextToken, boolean noException) throws SyntaxErrorException {
		if (nextToken == null) {
			return this.validateNull(seqIndex, foundTokens, noException);
		} else {
			return this.validateNext(seqIndex, foundTokens, nextToken, noException);			
		}
	}

	protected TSequence getToken(TokenStore foundTokens) {
		for (int i=0; i<foundTokens.size(); ++i) {
			if (foundTokens.get(i) != null) return this.adapter.convert(foundTokens.toList());
		}		
		return null;
	}

	@Override
	public final TSequence tokenize(Text text, AdapterSupply adapterSupply) throws SyntaxErrorException {
		if (text.onChar()) {
			TokenStore foundTokens = new ArrayAsTokenStore(this.factories.length);
			return this.tokenize(text, adapterSupply, 0, foundTokens, false);
		}		
		return null;
	}
	
	final TSequence tokenize(Text text, AdapterSupply adapterSupply, int firstSeqIndex, TokenStore foundTokens, boolean noException) throws SyntaxErrorException {
		int factoryCount = this.factories.length;
		for (int i=firstSeqIndex; i<factoryCount; ++i) {
			TokenFactory factory = this.factories[i];
			Token token = null;
			try {
				token = factory.tokenize(text, adapterSupply);				
			} catch (SyntaxErrorException e) {
				if (noException) return null;
				throw e;
			}
				
			ValidateResult vr = this.validate(i, foundTokens, token, noException);
			if (vr == ValidateResult.BREAK) break;
			if (vr == ValidateResult.NULL_RESULT) return null;

			foundTokens.addToken(token);
			if (token != null) {				
				if (text.onEndOfText() && (i < factoryCount-1)) {
					if (! this.validateEnd(i, foundTokens, noException)) return null;
					break;
				}
			}
		}
		return this.getToken(foundTokens);	
	}
	
	private static SequenceAdapter getAdapter(Class<? extends Token> cls) {
		final Constructor<? extends Token> constructor = getConstructor(cls, List.class, TSequence.class);
		return new SequenceAdapter() {			
			@Override
			public TSequence convert(List<Token> tokens) {
				try{
					return (TSequence) constructor.newInstance(tokens);
				} catch (Exception e) {	
					return null;
				}
			}
		};
		
	}
		
	@Override
	public void setTargetType(Class<? extends Token> cls) {
		this.adapter = getAdapter(cls);
	}
	
	private static SequenceAdapter getAdapter(Object adapter) {
		if (adapter instanceof SequenceAdapter) {
			return (SequenceAdapter) adapter;					
		} else {
			throw new IllegalArgumentException("Wrong adapter type " + adapter.getClass().getName());
		}
	}	
	
	@Override
	public void setAdapter(Object adapter) {
		this.adapter = getAdapter(adapter);					
	}	
}
