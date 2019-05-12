package com.example.decipher;

/**
 * Ted Moskovitz - April 2016
 * runs the em algorithm on a corpus using a set of character rules
 * and a parallel text for n iterations; generates P(char|sound)
 * Usage: java EM corpus.txt rules.txt phonetic_corp.txt n
 */
public class EM {
	private final int CHAR_NUM = 34; // including null char 	
	private final int MAX_LEN = 3; // max sentence length--triplet here
	private Bag<Bigram> pairs;
	private LinearProbingHashST<Sentence, Sentence> sents;
	private char[] syms;
	private char[] ipa;
	private int iterations;

	public Bag<Bigram> EM_getPairs() {
		return pairs;
	}

	/**
	 * find sentences in target
	 *
	 * @param target
	 * @param ipa
	 */
	public void EM_findSents(In target, In ipa) {
		char curr_sym;
		char curr_ipa;
		char[] sentence_sym = new char[MAX_LEN];
		char[] sentence_ipa = new char[MAX_LEN];
		int len = 0;
		Sentence s_sym, s_ipa;

		while ((target.hasNextChar()) && (ipa.hasNextChar())) {

			curr_sym = target.readChar();
			curr_ipa = ipa.readChar();
			//(curr_sym != '.') && (curr_sym != '?') && (curr_sym != '!') &&
			if (len < MAX_LEN) {
				// remove spaces, commas, dashes...punctuation
				if ((curr_sym != ' ') && (curr_sym != ',')
						&& (curr_sym != '-') && (curr_sym != '«')
						&& (curr_sym != '»') && (!Character.isDigit(curr_sym))
						&& (curr_sym != ')') && (curr_sym != '(')
						&& (curr_sym != '\'') && (curr_sym != '\"')
						&& (curr_sym != 0) && (curr_sym != 59)
						&& (curr_sym != 46) && (curr_sym != 10)) sentence_sym[len] = curr_sym;

				if ((curr_ipa != ' ') && (curr_ipa != ',')
						&& (curr_ipa != '-') && (curr_ipa != '«')
						&& (curr_ipa != '»') && (!Character.isDigit(curr_ipa))
						&& (curr_ipa != ')') && (curr_ipa != '(')
						&& (curr_ipa != '\'') && (curr_ipa != '\"')
						&& (curr_ipa != 0) && (curr_ipa != 59)
						&& (curr_ipa != 46) && (curr_ipa != 10)) sentence_ipa[len] = curr_ipa;
				len++;
			} else {
				// create current sentences, add to hash table
				s_sym = new Sentence(sentence_sym, len);
				s_ipa = new Sentence(sentence_ipa, len);
				sents.put(s_sym, s_ipa);


				if (target.hasNextChar()) {
					curr_sym = target.readChar(); // skip space
					curr_ipa = ipa.readChar();
					len = 0;
				}
			}
		}
	}

	/**
	 * print sents
	 */
	public void EM_printSents() {
		for (Sentence s : sents.keys()) {
			s.Sentence_print();
			sents.get(s).Sentence_print();
			StdOut.println();
		}
	}

	/**
	 * have seen this pair before?
	 *
	 * @param big Bigram
	 * @return boolean
	 */
	public boolean EM_containsPair(Bigram big) {
		for (Bigram b : pairs) {
			if (big.Bigram_equals(b)) return true;

		}
		return false;
	}

	/**
	 * is this in the SAMPA sym set?
	 *
	 * @param c char
	 * @return boolean
	 */
	public boolean EM_containsSYM(char c) {
		for (int i = 0; i < CHAR_NUM; i++)
			if (syms[i] == c) return true;
		return false;
	}

	/**
	 * is this in the SAMPA ipa set?
	 *
	 * @param c char
	 * @return boolean
	 */
	public boolean EM_containsSAMP(char c) {
		for (int i = 0; i < CHAR_NUM; i++)
			if (ipa[i] == c) return true;
		return false;
	}

	/**
	 * return st of sentences (<as written, phonetic>)
	 *
	 * @return
	 */
	public LinearProbingHashST<Sentence, Sentence> EM_getSents() {
		return sents;
	}

	/**
	 * constructs/initializes EM; steps 1 + 2 of algorithm
	 *
	 * @param corpus
	 * @param rules
	 * @param ipa_corp
	 * @param its
	 */
	public EM(In corpus, In rules, In ipa_corp, int its) {
		//probs = new LinearProbingHashST<Bigram, Double>(); 
		pairs = new Bag<>();
		syms = new char[CHAR_NUM];
		ipa = new char[CHAR_NUM];
		sents = new LinearProbingHashST<>();
		iterations = its;

		String line;
		int count = 0;
		Bigram b;
		double p;

		// fill char arrays
		while (rules.hasNextLine()) {
			line = rules.readLine();
			syms[count] = line.charAt(0);
			ipa[count] = line.charAt(1);
			count++;
		}

		EM_findSents(corpus, ipa_corp);

		// find co-occurring chars and add to pairs 
		char curr_sym;
		char curr_ipa;
		Sentence ipa_sent;
		for (Sentence s : sents.keys()) {
			for (int i = 0; i < s.Sentence_getLen(); i++) {
				curr_sym = s.Sentence_getSent()[i];

				ipa_sent = sents.get(s);
				curr_ipa = ipa_sent.Sentence_getSent()[i];
				//StdOut.println(curr_sym + "..." + curr_ipa);
				if (EM_containsSYM(curr_sym) && EM_containsSAMP(curr_ipa)) {
					b = new Bigram(curr_sym, curr_ipa);
					p = 1.0 / CHAR_NUM;
					b.Bigram_setProb(p);
					if (!EM_containsPair(b)) pairs.add(b);
				}
			}
		}
	}

	/**
	 * step 3 of algorithm: iteratively refine probabilities n times
	 * @param n
	 */
	public void EM_refine(int n) {
		double total = 0.0;
		double tc = 0.0;
		int tot = n;
		char sym_s;
		char ipa_s;
		Sentence curr_ipa;
		double p_is;
		Bigram pair;

		for (int i = 0; i < n; i++) {
			// tc = 0
			//tc = 0.0; 
			for (Bigram b : pairs)
				b.Bigram_setTC(0.0);
			// for each sentence pair
			for (Sentence s : sents.keys()) {

				curr_ipa = sents.get(s);

				for (int j = 0; j < s.Sentence_getLen(); j++) {
					total = 0;

					sym_s = s.Sentence_getSent()[j];

					// for i = 1 to l
					for (int k = 0; k < curr_ipa.Sentence_getLen(); k++) {
						ipa_s = curr_ipa.Sentence_getSent()[k];
						for (Bigram b : pairs) {
							// total += P(ipa|sym)
							if (b.Bigram_equals(sym_s, ipa_s)) {
								p_is = b.Bigram_getProb();

								total += p_is;

								break;
							}
						}
					}

					// for i = 1 to l
					for (int k = 0; k < curr_ipa.Sentence_getLen(); k++) {
						ipa_s = curr_ipa.Sentence_getSent()[k];
						for (Bigram b : pairs) {
							// tc(ipa|sym) += P(ipa|sym)/total; 
							if (b.Bigram_equals(sym_s, ipa_s)) {
								tc = b.Bigram_getTC();


								if (total > 0) {

									b.Bigram_setTC(tc + (b.Bigram_getProb() / total));
								}
							}
						}
					}
				}
			}
			// for each ipa
			for (int l = 0; l < CHAR_NUM; l++) {
				total = 0;
				ipa_s = ipa[l];
				// for each sym
				for (int t = 0; t < CHAR_NUM; t++) {
					sym_s = syms[t];
					for (Bigram b : pairs) {
						tc = b.Bigram_getTC();
						if (b.Bigram_equals(sym_s, ipa_s) && (tc != 0.0)) {
							total += tc;
							total += 1.0 / (tot / 1000.0);
						}
					}
				}
				// for each sym
				for (int t = 0; t < CHAR_NUM; t++) {
					sym_s = syms[t];
					for (Bigram b : pairs) {
						tc = b.Bigram_getTC();

						if (b.Bigram_equals(sym_s, ipa_s) && (tc != 0.0)) {
							b.Bigram_setProb(tc / total);
						}
					}
				}
			}
		}
	}

	/**
	 * for testing
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		In corpus = new In(args[0]);
		In rules = new In(args[1]);
		In ipa = new In(args[2]);
		int iterations = Integer.parseInt(args[3]);

		EM em = new EM(corpus, rules, ipa, iterations);
		Bag<Bigram> bigs = em.EM_getPairs();


		em.EM_refine(iterations);

		// print probabilities 
		for (Bigram b : bigs) {
			b.Bigram_print();
			StdOut.println("; Prob: " + b.Bigram_getProb());
		}
	}
}