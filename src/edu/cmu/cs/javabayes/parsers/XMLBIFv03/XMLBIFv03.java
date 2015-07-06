package edu.cmu.cs.javabayes.parsers.XMLBIFv03;

import java.util.Enumeration;
import java.util.Vector;

import edu.cmu.cs.javabayes.interchangeformat.IFBayesNet;
import edu.cmu.cs.javabayes.interchangeformat.IFProbabilityEntry;
import edu.cmu.cs.javabayes.interchangeformat.IFProbabilityFunction;
import edu.cmu.cs.javabayes.interchangeformat.IFProbabilityVariable;
import edu.cmu.cs.javabayes.interchangeformat.InterchangeFormat;

/**
 * Definition of the Interchange Format class and its variables. The IFBayesNet
 * ifbn contains the parsed Bayesian network.
 */

public class XMLBIFv03 extends InterchangeFormat implements XMLBIFv03Constants {
	IFBayesNet ifbn;

	static final int NATURE_DEFINE = 1;
	static final int DECISION_DEFINE = 2;
	static final int UTILITY_DEFINE = 3;

	@Override
	public IFBayesNet get_ifbn() {
		return (this.ifbn);
	}

	/**
	 * 
	 */
	public void invert_probability_tables() {
		this.ifbn.invert_probability_tables();
	}

	String pcdata() throws ParseException {
		StringBuffer p = new StringBuffer("");
		Token t;
		while (true) {
			t = getToken(1);
			if ((t.kind == 0) || (t.kind == SOT) || (t.kind == CT)
					|| (t.kind == EOT))
				break;
			else {
				p.append(t.image);
				getNextToken();
			}
		}
		return (p.toString());
	}

	void glob_heading() throws ParseException {
		Token t;
		while (true) {
			t = getToken(1);
			if (t.kind == 0)
				break;
			else {
				if (t.kind == SOT) {
					getNextToken();
					t = getToken(1);
					if (t.kind == BIF) {
						getNextToken();
						break;
					} else {
						getNextToken();
					}
				} else {
					getNextToken();
				}
			}
			getNextToken();
		}
	}

	/**
	 * THE INTERCHANGE FORMAT GRAMMAR STARTS HERE.
	 */

	/**
	 * Basic parsing function. First looks for a Network Declaration, then looks
	 * for an arbitrary number of VariableDeclaration or ProbabilityDeclaration
	 * non-terminals. The objects are in the vectors ifbn.pvs and ifbn.upfs.
	 */
	@Override
	final public void CompilationUnit() throws ParseException {
		IFProbabilityVariable pv;
		IFProbabilityFunction upf;
		OpenTag();
		glob_heading();
		NetworkDeclaration();
		label_1: while (true) {
			switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
			case SOT:
				;
				break;
			default:
				this.jj_la1[0] = this.jj_gen;
				break label_1;
			}
			jj_consume_token(SOT);
			switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
			case VARIABLE:
				pv = VariableDeclaration();
				this.ifbn.add(pv);
				break;
			case DEFINITION:
				upf = ProbabilityDeclaration();
				this.ifbn.add(upf);
				break;
			default:
				this.jj_la1[1] = this.jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		jj_consume_token(EOT);
		jj_consume_token(NETWORK);
		jj_consume_token(CT);
		switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
		case EOT:
			jj_consume_token(EOT);
			jj_consume_token(BIF);
			jj_consume_token(CT);
			break;
		case 0:
			jj_consume_token(0);
			break;
		default:
			this.jj_la1[2] = this.jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
	}

	final public void OpenTag() throws ParseException {
		jj_consume_token(OPENTAG);
	}

	/**
	 * Detect and initialize the network.
	 * 
	 * @throws ParseException
	 */
	final public void NetworkDeclaration() throws ParseException {
		String s, ss;
		Vector<String> properties = new Vector<String>();
		double version;
		version = VersionDeclaration();
		pcdata();
		jj_consume_token(CT);
		jj_consume_token(SOT);
		jj_consume_token(NETWORK);
		jj_consume_token(CT);
		jj_consume_token(SOT);
		jj_consume_token(NAME);
		s = getIdentifier();
		jj_consume_token(EOT);
		jj_consume_token(NAME);
		jj_consume_token(CT);
		label_2: while (true) {
			if (jj_2_1(2)) {
				;
			} else {
				break label_2;
			}
			ss = Property();
			properties.addElement(ss);
		}
		this.ifbn = new IFBayesNet(s, properties);
	}

	/**
	 * Get the format version.
	 */
	final public double VersionDeclaration() throws ParseException {
		Token t;
		double version = 0;
		jj_consume_token(VERSION);
		jj_consume_token(EQUAL);
		t = jj_consume_token(ATTRIBUTE_STRING);
		version = (Double.valueOf((t.image).substring(1, t.image.length() - 1)))
				.doubleValue();

		return (version);

	}

	/**
	 * Detect a variable declaration.
	 */
	final public IFProbabilityVariable VariableDeclaration()
			throws ParseException {
		String s;
		IFProbabilityVariable pv;
		int type = NATURE_DEFINE;
		jj_consume_token(VARIABLE);
		switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
		case TYPE:
			type = TypeDeclaration();
			break;
		default:
			this.jj_la1[3] = this.jj_gen;
			;
		}
		jj_consume_token(CT);
		s = ProbabilityVariableName();
		pv = VariableContent(s);
		jj_consume_token(EOT);
		jj_consume_token(VARIABLE);
		jj_consume_token(CT);

		return (pv);

	}

	final public String ProbabilityVariableName() throws ParseException {
		String s;
		jj_consume_token(SOT);
		jj_consume_token(NAME);
		s = getIdentifier();
		jj_consume_token(EOT);
		jj_consume_token(NAME);
		jj_consume_token(CT);

		return (s);

	}

	final public int TypeDeclaration() throws ParseException {
		int type;
		jj_consume_token(TYPE);
		jj_consume_token(EQUAL);
		type = ProbabilityVariableType();

		return (type);

	}

	final public int ProbabilityVariableType() throws ParseException {
		switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
		case NATURE:
			jj_consume_token(NATURE);

			return (NATURE_DEFINE);

		case DECISION:
			jj_consume_token(DECISION);

			return (DECISION_DEFINE);

		case UTILITY:
			jj_consume_token(UTILITY);

			return (UTILITY_DEFINE);

		default:
			this.jj_la1[4] = this.jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}

	}

	final public IFProbabilityVariable VariableContent(String name)
			throws ParseException {
		int i;
		String s, v, svalues[];
		Vector<String> properties = new Vector<String>();
		Vector<String> values = new Vector<String>();
		Enumeration<String> e;
		IFProbabilityVariable pv = new IFProbabilityVariable();
		label_3: while (true) {
			switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
			case SOT:
				;
				break;
			default:
				this.jj_la1[5] = this.jj_gen;
				break label_3;
			}
			if (jj_2_2(2)) {
				s = Property();
				properties.addElement(s);
			} else {
				switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
				case SOT:
					v = VariableOutcome();
					values.addElement(v);
					break;
				default:
					this.jj_la1[6] = this.jj_gen;
					jj_consume_token(-1);
					throw new ParseException();
				}
			}
		}
		pv.set_name(name);
		pv.set_properties(properties);
		svalues = new String[values.size()];
		for (e = values.elements(), i = 0; e.hasMoreElements(); i++)
			svalues[i] = (e.nextElement());
		pv.set_values(svalues);

		return (pv);

	}

	/**
	 * @return
	 * @throws ParseException
	 */
	final public String VariableOutcome() throws ParseException {
		String s;
		jj_consume_token(SOT);
		jj_consume_token(OUTCOME);
		s = getIdentifier();
		jj_consume_token(EOT);
		jj_consume_token(OUTCOME);
		jj_consume_token(CT);

		return (s);

	}

	/**
	 * Detect a probability declaration.
	 */
	final public IFProbabilityFunction ProbabilityDeclaration()
			throws ParseException {
		String vs[];
		IFProbabilityFunction upf = new IFProbabilityFunction();
		jj_consume_token(DEFINITION);
		jj_consume_token(CT);
		ProbabilityContent(upf);
		jj_consume_token(EOT);
		jj_consume_token(DEFINITION);
		jj_consume_token(CT);

		return (upf);

	}

	final public void ProbabilityContent(IFProbabilityFunction upf)
			throws ParseException {
		int i, j;
		double def[] = null;
		double tab[] = null;
		String s, vs[];
		IFProbabilityEntry entry = null;
		Enumeration<String> e;

		Vector<String> fors = new Vector<String>();
		Vector<String> givens = new Vector<String>();
		Vector<String> properties = new Vector<String>();
		Vector<IFProbabilityEntry> entries = new Vector<IFProbabilityEntry>();
		Vector<double[]> defaults = new Vector<double[]>();
		Vector<double[]> tables = new Vector<double[]>();
		label_4: while (true) {
			switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
			case SOT:
				;
				break;
			default:
				this.jj_la1[7] = this.jj_gen;
				break label_4;
			}
			jj_consume_token(SOT);
			switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
			case FOR:
				s = ProbabilityFor();
				fors.addElement(s);
				break;
			case GIVEN:
				s = ProbabilityGiven();
				givens.addElement(s);
				break;
			case SOT:
				s = Property();
				properties.addElement(s);
				break;
			case TABLE:
				tab = ProbabilityTable();
				tables.addElement(tab);
				break;
			default:
				this.jj_la1[8] = this.jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		upf.set_properties(properties);
		upf.set_defaults(defaults);
		upf.set_entries(entries);
		upf.set_tables(tables);
		upf.set_conditional_index(fors.size());
		vs = new String[fors.size() + givens.size()];
		for (e = fors.elements(), i = 0; e.hasMoreElements(); i++)
			vs[i] = (e.nextElement());
		for (e = givens.elements(), j = i; e.hasMoreElements(); j++)
			vs[j] = (e.nextElement());
		upf.set_variables(vs);
	}

	final public String ProbabilityFor() throws ParseException {
		String s;
		jj_consume_token(FOR);
		s = getIdentifier();
		jj_consume_token(EOT);
		jj_consume_token(FOR);
		jj_consume_token(CT);

		return (s);

	}

	final public String ProbabilityGiven() throws ParseException {
		String s;
		jj_consume_token(GIVEN);
		s = getIdentifier();
		jj_consume_token(EOT);
		jj_consume_token(GIVEN);
		jj_consume_token(CT);

		return (s);

	}

	final public double[] ProbabilityTable() throws ParseException {
		double d[];
		jj_consume_token(TABLE);
		jj_consume_token(CT);
		d = FloatingPointList();
		jj_consume_token(EOT);
		jj_consume_token(TABLE);
		jj_consume_token(CT);

		return (d);

	}

	/**
	 * Some general purpose non-terminals.
	 */

	/**
	 * Pick a list of non-negative floating numbers.
	 * 
	 * @return
	 * @throws ParseException
	 */
	final public double[] FloatingPointList() throws ParseException {
		int i;
		Double d;
		double ds[];
		Vector<Double> d_list = new Vector<Double>();
		Enumeration<Double> e;
		d = FloatingPointNumber();
		d_list.addElement(d);
		label_5: while (true) {
			switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
			case NON_NEGATIVE_NUMBER:
				;
				break;
			default:
				this.jj_la1[9] = this.jj_gen;
				break label_5;
			}
			d = FloatingPointNumber();
			d_list.addElement(d);
		}
		ds = new double[d_list.size()];
		for (e = d_list.elements(), i = 0; e.hasMoreElements(); i++) {
			d = (e.nextElement());
			ds[i] = d.doubleValue();
		}

		return (ds);

	}

	/**
	 * Pick a non-negative floating number; necessary to allow ignored
	 * characters and comments to exist in the middle of a FloatingPointList().
	 */
	final public Double FloatingPointNumber() throws ParseException {
		Token t;
		t = jj_consume_token(NON_NEGATIVE_NUMBER);

		return (Double.valueOf(t.image));

	}

	/**
	 * Property definition.
	 */
	final public String Property() throws ParseException {
		String s;
		jj_consume_token(SOT);
		jj_consume_token(PROPERTY);
		s = getString();
		jj_consume_token(EOT);
		jj_consume_token(PROPERTY);
		jj_consume_token(CT);

		return (s);

	}

	/**
	 * Identifier.
	 */
	final public String getIdentifier() throws ParseException {
		Token t;
		jj_consume_token(CT);
		t = jj_consume_token(IDENTIFIER);

		return (t.image);

	}

	/**
	 * String.
	 */
	final public String getString() throws ParseException {
		jj_consume_token(CT);

		return (pcdata());

	}

	final private boolean jj_2_1(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;
		boolean retval = !jj_3_1();
		jj_save(0, xla);
		return retval;
	}

	final private boolean jj_2_2(int xla) {
		this.jj_la = xla;
		this.jj_lastpos = this.jj_scanpos = this.token;
		boolean retval = !jj_3_2();
		jj_save(1, xla);
		return retval;
	}

	final private boolean jj_3R_6() {
		if (jj_scan_token(SOT))
			return true;
		if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
			return false;
		if (jj_scan_token(PROPERTY))
			return true;
		if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
			return false;
		return false;
	}

	final private boolean jj_3_1() {
		if (jj_3R_6())
			return true;
		if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
			return false;
		return false;
	}

	final private boolean jj_3_2() {
		if (jj_3R_6())
			return true;
		if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
			return false;
		return false;
	}

	public XMLBIFv03TokenManager token_source;
	ASCII_UCodeESC_CharStream jj_input_stream;
	public Token token, jj_nt;
	private int jj_ntk;
	private Token jj_scanpos, jj_lastpos;
	private int jj_la;
	public boolean lookingAhead = false;
	private boolean jj_semLA;
	private int jj_gen;
	final private int[] jj_la1 = new int[10];
	final private int[] jj_la1_0 = { 0x8, 0x84000, 0x11, 0x20000, 0x700000,
			0x8, 0x8, 0x8, 0x10c08, 0x800000, };
	final private JJXMLBIFv03Calls[] jj_2_rtns = new JJXMLBIFv03Calls[2];
	private boolean jj_rescan = false;
	private int jj_gc = 0;

	public XMLBIFv03(java.io.InputStream stream) {
		this.jj_input_stream = new ASCII_UCodeESC_CharStream(stream, 1, 1);
		this.token_source = new XMLBIFv03TokenManager(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 10; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJXMLBIFv03Calls();
	}

	public void ReInit(java.io.InputStream stream) {
		this.jj_input_stream.ReInit(stream, 1, 1);
		this.token_source.ReInit(this.jj_input_stream);
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 10; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJXMLBIFv03Calls();
	}

	public XMLBIFv03(XMLBIFv03TokenManager tm) {
		this.token_source = tm;
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 10; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJXMLBIFv03Calls();
	}

	public void ReInit(XMLBIFv03TokenManager tm) {
		this.token_source = tm;
		this.token = new Token();
		this.jj_ntk = -1;
		this.jj_gen = 0;
		for (int i = 0; i < 10; i++)
			this.jj_la1[i] = -1;
		for (int i = 0; i < this.jj_2_rtns.length; i++)
			this.jj_2_rtns[i] = new JJXMLBIFv03Calls();
	}

	final private Token jj_consume_token(int kind) throws ParseException {
		Token oldToken;
		if ((oldToken = this.token).next != null)
			this.token = this.token.next;
		else
			this.token = this.token.next = this.token_source.getNextToken();
		this.jj_ntk = -1;
		if (this.token.kind == kind) {
			this.jj_gen++;
			if (++this.jj_gc > 100) {
				this.jj_gc = 0;
				for (int i = 0; i < this.jj_2_rtns.length; i++) {
					JJXMLBIFv03Calls c = this.jj_2_rtns[i];
					while (c != null) {
						if (c.gen < this.jj_gen)
							c.first = null;
						c = c.next;
					}
				}
			}
			return this.token;
		}
		this.token = oldToken;
		this.jj_kind = kind;
		throw generateParseException();
	}

	final private boolean jj_scan_token(int kind) {
		if (this.jj_scanpos == this.jj_lastpos) {
			this.jj_la--;
			if (this.jj_scanpos.next == null) {
				this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source
						.getNextToken();
			} else {
				this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
			}
		} else {
			this.jj_scanpos = this.jj_scanpos.next;
		}
		if (this.jj_rescan) {
			int i = 0;
			Token tok = this.token;
			while (tok != null && tok != this.jj_scanpos) {
				i++;
				tok = tok.next;
			}
			if (tok != null)
				jj_add_error_token(kind, i);
		}
		return (this.jj_scanpos.kind != kind);
	}

	final public Token getNextToken() {
		if (this.token.next != null)
			this.token = this.token.next;
		else
			this.token = this.token.next = this.token_source.getNextToken();
		this.jj_ntk = -1;
		this.jj_gen++;
		return this.token;
	}

	final public Token getToken(int index) {
		Token t = this.lookingAhead ? this.jj_scanpos : this.token;
		for (int i = 0; i < index; i++) {
			if (t.next != null)
				t = t.next;
			else
				t = t.next = this.token_source.getNextToken();
		}
		return t;
	}

	final private int jj_ntk() {
		if ((this.jj_nt = this.token.next) == null)
			return (this.jj_ntk = (this.token.next = this.token_source
					.getNextToken()).kind);
		else
			return (this.jj_ntk = this.jj_nt.kind);
	}

	private java.util.Vector<int[]> jj_expentries = new java.util.Vector<>();
	private int[] jj_expentry;
	private int jj_kind = -1;
	private int[] jj_lasttokens = new int[100];
	private int jj_endpos;

	private void jj_add_error_token(int kind, int pos) {
		if (pos >= 100)
			return;
		if (pos == this.jj_endpos + 1) {
			this.jj_lasttokens[this.jj_endpos++] = kind;
		} else if (this.jj_endpos != 0) {
			this.jj_expentry = new int[this.jj_endpos];
			for (int i = 0; i < this.jj_endpos; i++) {
				this.jj_expentry[i] = this.jj_lasttokens[i];
			}
			boolean exists = false;
			for (int[] oldentry: this.jj_expentries) {
				if (oldentry.length == this.jj_expentry.length) {
					exists = true;
					for (int i = 0; i < this.jj_expentry.length; i++) {
						if (oldentry[i] != this.jj_expentry[i]) {
							exists = false;
							break;
						}
					}
					if (exists)
						break;
				}
			}
			if (!exists)
				this.jj_expentries.addElement(this.jj_expentry);
			if (pos != 0)
				this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
		}
	}

	final public ParseException generateParseException() {
		this.jj_expentries.removeAllElements();
		boolean[] la1tokens = new boolean[30];
		for (int i = 0; i < 30; i++) {
			la1tokens[i] = false;
		}
		if (this.jj_kind >= 0) {
			la1tokens[this.jj_kind] = true;
			this.jj_kind = -1;
		}
		for (int i = 0; i < 10; i++) {
			if (this.jj_la1[i] == this.jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((this.jj_la1_0[i] & (1 << j)) != 0) {
						la1tokens[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 30; i++) {
			if (la1tokens[i]) {
				this.jj_expentry = new int[1];
				this.jj_expentry[0] = i;
				this.jj_expentries.addElement(this.jj_expentry);
			}
		}
		this.jj_endpos = 0;
		jj_rescan_token();
		jj_add_error_token(0, 0);
		int[][] exptokseq = new int[this.jj_expentries.size()][];
		for (int i = 0; i < this.jj_expentries.size(); i++) {
			exptokseq[i] = this.jj_expentries.elementAt(i);
		}
		return new ParseException(this.token, exptokseq, tokenImage);
	}

	final public void enable_tracing() {
	}

	final public void disable_tracing() {
	}

	final private void jj_rescan_token() {
		this.jj_rescan = true;
		for (int i = 0; i < 2; i++) {
			JJXMLBIFv03Calls p = this.jj_2_rtns[i];
			do {
				if (p.gen > this.jj_gen) {
					this.jj_la = p.arg;
					this.jj_lastpos = this.jj_scanpos = p.first;
					switch (i) {
					case 0:
						jj_3_1();
						break;
					case 1:
						jj_3_2();
						break;
					}
				}
				p = p.next;
			} while (p != null);
		}
		this.jj_rescan = false;
	}

	final private void jj_save(int index, int xla) {
		JJXMLBIFv03Calls p = this.jj_2_rtns[index];
		while (p.gen > this.jj_gen) {
			if (p.next == null) {
				p = p.next = new JJXMLBIFv03Calls();
				break;
			}
			p = p.next;
		}
		p.gen = this.jj_gen + xla - this.jj_la;
		p.first = this.token;
		p.arg = xla;
	}

}

final class JJXMLBIFv03Calls {
	int gen;
	Token first;
	int arg;
	JJXMLBIFv03Calls next;
}
