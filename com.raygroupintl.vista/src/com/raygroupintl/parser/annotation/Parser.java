package com.raygroupintl.parser.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.raygroupintl.charlib.AndPredicate;
import com.raygroupintl.charlib.CharPredicate;
import com.raygroupintl.charlib.CharRangePredicate;
import com.raygroupintl.charlib.CharsPredicate;
import com.raygroupintl.charlib.DigitPredicate;
import com.raygroupintl.charlib.ExcludePredicate;
import com.raygroupintl.charlib.LetterPredicate;
import com.raygroupintl.charlib.OrPredicate;
import com.raygroupintl.charlib.Predicate;
import com.raygroupintl.m.struct.IdentifierStartPredicate;
import com.raygroupintl.parser.CharacterAdapter;
import com.raygroupintl.parser.DelimitedListAdapter;
import com.raygroupintl.parser.ListAdapter;
import com.raygroupintl.parser.SequenceAdapter;
import com.raygroupintl.parser.StringAdapter;
import com.raygroupintl.parser.SyntaxErrorException;
import com.raygroupintl.parser.TFCharacter;
import com.raygroupintl.parser.TFChoiceBasic;
import com.raygroupintl.parser.TFChoiceOnChar0th;
import com.raygroupintl.parser.TFChoiceOnChar1st;
import com.raygroupintl.parser.TFConstant;
import com.raygroupintl.parser.TFDelimitedList;
import com.raygroupintl.parser.TFEnd;
import com.raygroupintl.parser.TFList;
import com.raygroupintl.parser.TFSequenceStatic;
import com.raygroupintl.parser.TFString;
import com.raygroupintl.parser.Text;
import com.raygroupintl.parser.Token;
import com.raygroupintl.parser.TokenFactory;

public class Parser {
	private static final Map<String, Predicate> PREDICATES = new HashMap<String, Predicate>();
	static {
		PREDICATES.put("letter", new LetterPredicate());
		PREDICATES.put("digit", new DigitPredicate());
		PREDICATES.put("idstart", new IdentifierStartPredicate());
	}
		
	private static final class Triple<T extends TokenFactory, A extends Annotation> {
		public T factory;
		public A annotation;
		
		public Triple(String name, T factory, A annotation) {
			this.factory = factory;
			this.annotation = annotation;
		}
	}
	
	private static class ConstructorAsCharacterAdapter implements CharacterAdapter {					
		private Constructor<? extends Token> constructor;
		
		public ConstructorAsCharacterAdapter(Constructor<? extends Token> constructor) {
			this.constructor = constructor;
		}
		
		@Override
		public Token convert(char value) {
			try {
				return (Token) this.constructor.newInstance((Object) value);
			} catch (Exception e) {
				return null;
			}
		}
	};

	private static class ConstructorAsStringAdapter implements StringAdapter {					
		private Constructor<? extends Token> constructor;
		
		public ConstructorAsStringAdapter(Constructor<? extends Token> constructor) {
			this.constructor = constructor;
		}
		
		@Override
		public Token convert(String value) {
			try {
				return (Token) this.constructor.newInstance((Object) value);
			} catch (Exception e) {
				return null;
			}
		}
	};
	
	private static class ConstructorAsSequenceAdapter implements SequenceAdapter {					
		private Constructor<? extends Token> constructor;
		
		public ConstructorAsSequenceAdapter(Constructor<? extends Token> constructor) {
			this.constructor = constructor;
		}
		
		@Override
		public Token convert(java.util.List<Token> tokens) {
			try {
				return (Token) this.constructor.newInstance((Object) tokens);
			} catch (Exception e) {
				return null;
			}
		}
	};
	
	private static class ConstructorAsListAdapter implements ListAdapter {					
		private Constructor<? extends Token> constructor;
		
		public ConstructorAsListAdapter(Constructor<? extends Token> constructor) {
			this.constructor = constructor;
		}
		
		@Override
		public Token convert(java.util.List<Token> tokens) {
			try {
				return (Token) this.constructor.newInstance(tokens);
			} catch (Exception e) {
				return null;
			}
		}
	};
	
	private static class ConstructorAsDelimitedListAdapter implements DelimitedListAdapter {					
		private Constructor<? extends Token> constructor;
		
		public ConstructorAsDelimitedListAdapter(Constructor<? extends Token> constructor) {
			this.constructor = constructor;
		}
		
		@Override
		public Token convert(java.util.List<Token> tokens) {
			try {
				return (Token) this.constructor.newInstance(tokens);
			} catch (Exception e) {
				return null;
			}
		}
	};
	
	private static class Store {
		private static RuleGrammar descriptionSpec;
		
		public Map<String, TokenFactory> symbols = new HashMap<String, TokenFactory>();
		
		private java.util.List<Triple<TFChoiceBasic, Choice>> choices  = new ArrayList<Triple<TFChoiceBasic, Choice>>();
		private java.util.List<Triple<TFSequenceStatic, Sequence>> sequences  = new ArrayList<Triple<TFSequenceStatic, Sequence>>();
		private java.util.List<Triple<TFSequenceStatic, Rule>> descriptions  = new ArrayList<Triple<TFSequenceStatic, Rule>>();
		private java.util.List<Triple<TFList, List>> lists  = new ArrayList<Triple<TFList, List>>();
		private java.util.List<Triple<TFSequenceStatic, List>> enclosedLists  = new ArrayList<Triple<TFSequenceStatic, List>>();
		private java.util.List<Triple<TFDelimitedList, List>> delimitedLists  = new ArrayList<Triple<TFDelimitedList, List>>();
		private java.util.List<Triple<TFSequenceStatic, List>> enclosedDelimitedLists  = new ArrayList<Triple<TFSequenceStatic, List>>();
		private java.util.List<Triple<TFChoiceOnChar0th, CChoice>> choice0ths  = new ArrayList<Triple<TFChoiceOnChar0th, CChoice>>();
		private java.util.List<Triple<TFChoiceOnChar1st, CChoice>> choice1sts  = new ArrayList<Triple<TFChoiceOnChar1st, CChoice>>();
		private Map<String, Field> otherSymbols = new HashMap<String, Field>();
		
		private TokenFactory addChoice(String name, Choice choice) {
			TFChoiceBasic value = new TFChoiceBasic(name);
			this.choices.add(new Triple<TFChoiceBasic, Choice>(name, value, choice));
			return value;			
		}
		
		private CharacterAdapter getCharacterAdapter(Field f) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			Adapter adapter = f.getAnnotation(Adapter.class);
			if (adapter != null) {
				Class<?> cls = adapter.value();			
				CharacterAdapter ta = (CharacterAdapter) cls.newInstance();
				return ta;
			}
			TokenType tokenType = f.getAnnotation(TokenType.class);
			if (tokenType != null) {
				Class<? extends Token> tokenCls = tokenType.value();
				Constructor<? extends Token> constructor = tokenCls.getConstructor(char.class);
				CharacterAdapter ta = new ConstructorAsCharacterAdapter(constructor);
				return ta;
			}
			return null;
		}

		private StringAdapter getStringAdapter(Field f) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			Adapter adapter = f.getAnnotation(Adapter.class);
			if (adapter != null) {
				Class<?> cls = adapter.value();			
				StringAdapter ta = (StringAdapter) cls.newInstance();
				return ta;
			}
			TokenType tokenType = f.getAnnotation(TokenType.class);
			if (tokenType != null) {
				Class<? extends Token> tokenCls = tokenType.value();
				Constructor<? extends Token> constructor = tokenCls.getConstructor(String.class);
				StringAdapter ta = new ConstructorAsStringAdapter(constructor);
				return ta;
			}
			return null;
		}

		private SequenceAdapter getSequenceAdapter(Field f) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			Adapter adapter = f.getAnnotation(Adapter.class);
			if (adapter != null) {
				Class<?> cls = adapter.value();			
				SequenceAdapter la = (SequenceAdapter) cls.newInstance();
				return la;
			}
			TokenType tokenType = f.getAnnotation(TokenType.class);
			if (tokenType != null) {
				Class<? extends Token> tokenCls = tokenType.value();
				Constructor<? extends Token> constructor = tokenCls.getConstructor(java.util.List.class);
				SequenceAdapter ta = new ConstructorAsSequenceAdapter(constructor);
				return ta;
			}
			return null;
		}

		private ListAdapter getListAdapter(Field f) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			Adapter adapter = f.getAnnotation(Adapter.class);
			if (adapter != null) {
				Class<?> cls = adapter.value();			
				ListAdapter la = (ListAdapter) cls.newInstance();
				return la;
			}
			TokenType tokenType = f.getAnnotation(TokenType.class);
			if (tokenType != null) {
				Class<? extends Token> tokenCls = tokenType.value();
				Constructor<? extends Token> constructor = tokenCls.getConstructor(java.util.List.class);
				ListAdapter ta = new ConstructorAsListAdapter(constructor);
				return ta;
			}
			return null;
		}

		private DelimitedListAdapter getDelimitedListAdapter(Field f) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			Adapter adapter = f.getAnnotation(Adapter.class);
			if (adapter != null) {
				Class<?> cls = adapter.value();			
				DelimitedListAdapter la = (DelimitedListAdapter) cls.newInstance();
				return la;
			}
			TokenType tokenType = f.getAnnotation(TokenType.class);
			if (tokenType != null) {
				Class<? extends Token> tokenCls = tokenType.value();
				Constructor<? extends Token> constructor = tokenCls.getConstructor(java.util.List.class);
				DelimitedListAdapter ta = new ConstructorAsDelimitedListAdapter(constructor);
				return ta;
			}
			return null;
		}
		
		private TokenFactory addSequence(String name, Sequence sequence, Field f, AdapterSupply adapterSupply) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			SequenceAdapter adapter = this.getSequenceAdapter(f);
			if ((adapter == null) && (adapterSupply != null)) adapter = adapterSupply.getSequenceAdapter();
			TFSequenceStatic value = new TFSequenceStatic(name, adapter);
			this.sequences.add(new Triple<TFSequenceStatic, Sequence>(name, value, sequence));
			return value;			
		}
		
		private TokenFactory addDescription(String name, Rule description, Field f, AdapterSupply adapterSupply)  throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			SequenceAdapter adapter = this.getSequenceAdapter(f);
			if ((adapter == null) && (adapterSupply != null)) adapter = adapterSupply.getSequenceAdapter();
			TFSequenceStatic value = new TFSequenceStatic(name, adapter);
			this.descriptions.add(new Triple<TFSequenceStatic, Rule>(name, value, description));
			return value;		
		}
		
		private TokenFactory addList(String name, List list, Field f, AdapterSupply adapterSupply)  throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			String delimiter = list.delim();
			String left = list.left();
			String right = list.right();
			if (delimiter.length() == 0) {
				if ((left.length() == 0) || (right.length() == 0)) {
					ListAdapter la = this.getListAdapter(f);
					if ((la == null) && (adapterSupply != null)) la = adapterSupply.getListAdapter();
					TFList value = new TFList(name, la);
					this.lists.add(new Triple<TFList, List>(name, value, list));
					return value;
				} else {
					SequenceAdapter la = this.getSequenceAdapter(f);
					if ((la == null) && (adapterSupply != null)) la = adapterSupply.getSequenceAdapter();
					TFSequenceStatic value = new TFSequenceStatic(name, la);
					this.enclosedLists.add(new Triple<TFSequenceStatic, List>(name, value, list));
					return value;
				}
			} else {			
				if ((left.length() == 0) || (right.length() == 0)) {
					DelimitedListAdapter adapter = this.getDelimitedListAdapter(f);
					if ((adapter == null) && (adapterSupply != null))adapter = adapterSupply.getDelimitedListAdapter();
					TFDelimitedList value = new TFDelimitedList(name, adapter);
					this.delimitedLists.add(new Triple<TFDelimitedList, List>(name, value, list));
					return value;
				} else {
					SequenceAdapter la = this.getSequenceAdapter(f);
					if ((la == null) && (adapterSupply != null)) la = adapterSupply.getSequenceAdapter();
					TFSequenceStatic value = new TFSequenceStatic(name, la);
					this.enclosedDelimitedLists.add(new Triple<TFSequenceStatic, List>(name, value, list));
					return value;					
				}
			}
		}
		
		private TokenFactory addCChoice(String name, CChoice cchoice) {
			String lead = cchoice.lead();
			if (lead.length() == 0) {
				TFChoiceOnChar0th value = new TFChoiceOnChar0th(name);
				this.choice0ths.add(new Triple<TFChoiceOnChar0th, CChoice>(name, value, cchoice));
				return value;
			} else {
				TFChoiceOnChar1st value = new TFChoiceOnChar1st(name);
				this.choice1sts.add(new Triple<TFChoiceOnChar1st, CChoice>(name, value, cchoice));
				return value;
			}
		}
		
		private Predicate orPredicates(Predicate p0, Predicate p1) {
			if (p1 == null) return p0;
			if (p0 == null) return p1;
			return new OrPredicate(p0, p1);
		}

		private Predicate andPredicates(Predicate p0, Predicate p1) {
			if (p1 == null) return p0;
			if (p0 == null) return p1;
			return new AndPredicate(p0, p1);
		}
		
		private TokenFactory addCharacters(String name, CharSpecified characters, Field f, AdapterSupply adapterSupply)  throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			Predicate p0 = getCharPredicate(characters.chars());
			Predicate p1 = getCharRanges(characters.ranges());
			Predicate p2 = getCharPredicate(characters.excludechars());
			if (p2 != null) {
				p2 = new ExcludePredicate(p2);
			}			
			Predicate p3 = getCharRanges(characters.excluderanges());
			if (p3 != null) {
				p3 = new ExcludePredicate(p3);
			}			
			Predicate result = andPredicates(orPredicates(p0, p1), orPredicates(p2, p3));
			if (characters.single()) {
				CharacterAdapter ca = this.getCharacterAdapter(f);
				if ((ca == null) && (adapterSupply != null)) ca = adapterSupply.getCharacterAdapter();
				TFCharacter tf = new TFCharacter(name, result, ca);
				return tf;
			} else {		
				StringAdapter sa = this.getStringAdapter(f);
				if ((sa == null) && (adapterSupply != null)) sa = adapterSupply.getStringAdapter();
				TFString tf = new TFString(name, result, sa);
				return tf;
			}
		}
		
		private TokenFactory addWords(String name, WordSpecified wordSpecied, Field f)  throws IllegalAccessException, InstantiationException, NoSuchMethodException {
			String word = wordSpecied.value();
			StringAdapter sa = this.getStringAdapter(f);
			TFConstant tf = new TFConstant(name, word, sa, wordSpecied.ignorecase());
			return tf;
		}
		
		private TokenFactory add(Field f, AdapterSupply adapterSupply) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
			String name = f.getName();
			
			Choice choice = f.getAnnotation(Choice.class);
			if (choice != null) {
				return this.addChoice(name, choice);
			}			
			Sequence sequence = f.getAnnotation(Sequence.class);
			if (sequence != null) {
				return this.addSequence(name, sequence, f, adapterSupply);
			}			
			Rule description = f.getAnnotation(Rule.class);
			if (description != null) {
				return this.addDescription(name, description, f, adapterSupply);
			}			
			List list = f.getAnnotation(List.class);
			if (list != null) {
				return this.addList(name, list, f, adapterSupply);
			}			
			CChoice cchoice = f.getAnnotation(CChoice.class);
			if (cchoice != null) {
				return this.addCChoice(name, cchoice);
			}		
			CharSpecified characters = f.getAnnotation(CharSpecified.class);
			if (characters != null) {
				return this.addCharacters(name, characters, f, adapterSupply);
			}
			WordSpecified words = f.getAnnotation(WordSpecified.class);
			if (words != null) {
				return this.addWords(name, words, f);
			}
			return null;
		}
		
		public <T> void add(T target, AdapterSupply adapterSupply) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
			Class<?> cls = target.getClass();
			while (! cls.equals(Object.class)) {
				for (Field f : cls.getDeclaredFields()) {
					if (TokenFactory.class.isAssignableFrom(f.getType())) {
						String name = f.getName();
						TokenFactory already = this.symbols.get(name);
						if (already == null) {					
							TokenFactory value = (TokenFactory) f.get(target);
							if (value == null) {
								value = this.add(f, adapterSupply);
								if (value != null) {
									f.set(target, value);
								} else {
									this.otherSymbols.put(name, f);
								}
							}
							if (value != null) {
								this.symbols.put(name, value);
							}
						} else {
							f.set(target, already);						
						}
					}
				}
				cls = cls.getSuperclass();
			}			
		}
		
		public <T> void updateEquivalents(T target) throws IllegalAccessException {
			for (String name : this.otherSymbols.keySet()) {			
				Field f = this.otherSymbols.get(name);
				Equivalent annot = f.getAnnotation(Equivalent.class);
				if (annot != null) {
					String source = annot.value();
					TokenFactory sourceFactory = this.symbols.get(source);
					f.set(target, sourceFactory);
					this.symbols.put(name, sourceFactory);
				}			
			}
		}

		private void updateChoices() {		
			for (Triple<TFChoiceBasic, Choice> p : this.choices) {
				TokenFactory[] fs = getFactories(this.symbols, p.annotation.value());
				p.factory.setFactories(fs);
			}
		}
		
		private void updateChoicesOnChar0th() {
			for (Triple<TFChoiceOnChar0th, CChoice> p : this.choice0ths) {
				TokenFactory[] fs = getFactories(this.symbols, p.annotation.value());
				Predicate[] ps = getPredicates(p.annotation.preds());
				p.factory.setChoices(ps, fs);
				String dcode = p.annotation.def();
				if (dcode.length() > 0) {
					TokenFactory df = this.symbols.get(dcode);
					p.factory.setDefault(df);
				}
			}
		}
		
		private void updateChoicesOnChar1st() {
			for (Triple<TFChoiceOnChar1st, CChoice> p : this.choice1sts) {
				TokenFactory[] fs = getFactories(this.symbols, p.annotation.value());
				Predicate[] ps = getPredicates(p.annotation.preds());
				p.factory.setLeadingChar(p.annotation.lead().charAt(0));
				p.factory.setChoices(ps, fs);
				String dcode = p.annotation.def();
				if (dcode.length() > 0) {
					TokenFactory df = this.symbols.get(dcode);
					p.factory.setDefault(df);
				}
			}
		}
		
		private void updateSequences() {
			for (Triple<TFSequenceStatic, Sequence> p : this.sequences) {
				TokenFactory[] fs = getFactories(this.symbols, p.annotation.value());
				boolean[] required = getRequiredFlags(p.annotation.required(), fs.length);
				p.factory.setFactories(fs, required);
			}
		}
	
		private void updateDescription() throws ParseException {
			try {
				if (descriptionSpec == null) {
					Parser parser = new Parser();
					descriptionSpec = parser.parse(RuleGrammar.class, null, true);
				}
				for (Triple<TFSequenceStatic, Rule> p : this.descriptions) {
					String description = p.annotation.value();
					Text text = new Text(description, 0);
					TRule token = (TRule) descriptionSpec.rule.tokenize(text);
					TFSequenceStatic f = (TFSequenceStatic) token.getTopFactory(p.factory.getName(), this.symbols);
					p.factory.copyFrom(f);
				}
			} catch (SyntaxErrorException se) {
				throw new ParseException(se);
			}
		}

		private void updateLists() {
			for (Triple<TFList, List> p : this.lists) {
				TokenFactory f = this.symbols.get(p.annotation.value());
				p.factory.setElement(f);
			}	
		}
		
		private void updateEnclosedLists() {
			for (Triple<TFSequenceStatic, List> p : this.enclosedLists) {
				TokenFactory e = this.symbols.get(p.annotation.value());
				TokenFactory l = this.symbols.get(p.annotation.left());
				TokenFactory r = this.symbols.get(p.annotation.right());
				TFList f = new TFList(p.factory.getName() + ".list", e);
				p.factory.setFactories(new TokenFactory[]{l, f, r}, new boolean[]{true, ! p.annotation.none(), true});
			}	
		}
		
		private void updateEnclosedDelimitedLists() {
			for (Triple<TFSequenceStatic, List> p : this.enclosedDelimitedLists) {
				TokenFactory e = this.symbols.get(p.annotation.value());
				TokenFactory d = this.symbols.get(p.annotation.delim());
				TokenFactory l = this.symbols.get(p.annotation.left());
				TokenFactory r = this.symbols.get(p.annotation.right());
				
				TFDelimitedList dl = new TFDelimitedList(p.factory.getName() + ".list");
				dl.set(e, d, p.annotation.empty());
				
				p.factory.setFactories(new TokenFactory[]{l, dl, r}, new boolean[]{true, ! p.annotation.none(), true});
			}	
		}
		
		private void updateDelimitedLists() {
			for (Triple<TFDelimitedList, List> p : this.delimitedLists) {
				TokenFactory e = this.symbols.get(p.annotation.value());
				TokenFactory d = this.symbols.get(p.annotation.delim());
				boolean empty = p.annotation.empty();
				p.factory.set(e, d, empty);
			}	
		}
		
		public void update(Class<?> cls, boolean ignore)  throws IllegalAccessException, InstantiationException, ParseException {
			this.updateChoices();
			this.updateChoicesOnChar0th();
			this.updateChoicesOnChar1st();
			this.updateSequences();
			this.updateLists();
			this.updateEnclosedLists();
			this.updateEnclosedDelimitedLists();
			this.updateDelimitedLists();
			if (! ignore) {
				this.updateDescription();
			}
		}		
	}
	
	private static Predicate getCharPredicate(char[] chs) {
		if (chs.length == 1) {
			return new CharPredicate(chs[0]);
		} else if (chs.length > 1) {
			return new CharsPredicate(chs);
		} else {
			return null;
		}		
	}
	
	private static Predicate getCharRanges(char[] chs) {
		Predicate result = null;
		for (int i=1; i<chs.length; i=i+2) {
			char ch0 = chs[i];
			char ch1 = chs[i-1];
			Predicate p =  new CharRangePredicate(ch0, ch1);
			if (result == null) {
				result = p;
			} else {
				result = new OrPredicate(result, p);
			}
		}
		return result;
	}
	
	private static TokenFactory[] getFactories(Map<String, TokenFactory> map, String[] names) {
		int n = names.length;
		TokenFactory[] fs = new TokenFactory[n];
		for (int i=0; i<n; ++i) {
			String name = names[i];
			fs[i] = map.get(name);
		}
		return fs;
	}
	
	private static boolean[] getRequiredFlags(String specification, int n) {
		boolean[] result = new boolean[n];
		if (specification.equals("all")) {
			Arrays.fill(result, true);
			return result;
		}
		if (specification.equals("none")) {
			return result;
		}
		for (int i=0; i<specification.length(); ++i) {
			char ch = specification.charAt(i);
			if (ch == 'r') {
				result[i] = true;
			}
		}
		return result;
	}
	
	private static Predicate[] getPredicates(String[] codes) {
		int n = codes.length;
		Predicate[] result = new Predicate[n];
		for (int i=0; i<n; ++i) {
			String code = codes[i];
			if (code.length() == 1) {
				result[i] = new CharPredicate(code.charAt(0));
			} else {
				Predicate named = PREDICATES.get(code);
				if (named == null) {
					result[i] = new CharsPredicate(code.toCharArray());	
				} else {
					result[i] = PREDICATES.get(code);
				}
			}
		}
		return result;
	}
		
	private <T> T parse(Class<T> cls, AdapterSupply adapterSupply, boolean ignore) throws ParseException {
		try {
			T target = cls.newInstance();
			Store store = new Store();
			store.add(target, adapterSupply);
			store.symbols.put("end", new TFEnd("end"));
			store.updateEquivalents(target);
			store.update(cls, ignore);
			return target;
		} catch (IllegalAccessException iae) {
			throw new ParseException(iae);
		} catch (InstantiationException ine) {
			throw new ParseException(ine);
		} catch (ClassNotFoundException cnf) {
			throw new ParseException(cnf);
		} catch (NoSuchMethodException nsm) {
			throw new ParseException(nsm);			
		}
	}

	public <T> T parse(Class<T> cls, AdapterSupply adapterSupply) throws ParseException {
		return this.parse(cls, adapterSupply, false);
	}

	public <T> T parse(Class<T> cls) throws ParseException {
		return this.parse(cls, null, false);
	}
}