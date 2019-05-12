package com.example.decipher;


/**
 * Ted Moskovitz
 * usage: java SAMPA_Converter corpus.txt rules.txt
 * converts corpus according to rules
 */
public class SAMPA_Converter {

	public static void main(String[] args) {
		In corpus = new In(args[0]);
		In norm_rules = new In(args[1]);
		Out sampa = new Out("phonetic_corp.txt");
		char c1, c2;
		String line;
		LinearProbingHashST<Character, Character> rule_st = new LinearProbingHashST<>();

		// read and set normal (single char) rules
		while (norm_rules.hasNextLine()) {

			line = norm_rules.readLine();
			c1 = line.charAt(0);
			c2 = line.charAt(1);

			rule_st.put(c1, c2);

		}

		// create 'bilingual' aka phonetic corpus
		while (corpus.hasNextChar()) {
			c1 = corpus.readChar();

			// two special two-character rules (for Spanish only)
			if ((c1 == 'c') && (corpus.hasNextChar())) {
				c2 = corpus.readChar();
				if (c2 == 'h')
					sampa.print("ts");
				else
					sampa.print(c1 + "" + c2);
			} else if (rule_st.contains(c1))
				sampa.print(rule_st.get(c1));

			else
				sampa.print(c1);
		}
	}
}