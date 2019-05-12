package com.example.decipher;

/**
 * Ted Moskovitz
 * computes the percentage of correctly deciphered sounds in an unknown document
 * usage: java PhoneticDecipherment corpus.txt rules.txt phonetic_corpus.txt document.txt (int)#EM-iterations
 */
public class PhoneticDecipherment {

	private final double doc_size = 10.0;
	private EM em;
	private SoundTrigrams st;
	private int char_size;
	private Bag<Bigram> pairs;
	private LinearProbingHashST<Sentence, Sentence> sents;
	private Bag<Trigram> trigs;
	private int iters;

	/**
	 * construct a decipherer
	 *
	 * @param corpus
	 * @param rules
	 * @param sampa1
	 * @param sampa2
	 * @param sampa3
	 * @param document
	 * @param iterations
	 */
	public PhoneticDecipherment(In corpus, In rules, In sampa1, In sampa2, In sampa3, In document, int iterations) {
		em = new EM(corpus, rules, sampa1, iterations);
		pairs = em.EM_getPairs();
		em.EM_refine(iterations);
		sents = em.EM_getSents();
		iters = iterations;

		st = new SoundTrigrams(sampa2);
		st.SoundTrigrams_Process_doc();
		double p = st.SoundTrigrams_prob(sampa3);
		char_size = st.SoundTrigrams_getV();
		trigs = st.SoundTrigrams_getTrigs();
	}

	/**
	 * Compute ignoring sound trigram probabilities
	 * @return double percent correct
	 */
	public double SpellingModel() {
		char curr;
		double char_norm = doc_size / iters;
		char best_sampa = '\0';
		double max_prob = 0.0;
		int count = 0;
		double char_count = 0.0;
		int num_correct = 0;
		Sentence ipa_sent;
		char correct_sampa = '\0';
		for (Sentence s : sents.keys()) {
			//s.Sentence_print(); 
			for (int i = 0; i < s.Sentence_getLen(); i++) {
				curr = s.Sentence_getSent()[i];
				char_count += 1.0;
				// iterate through pairs and find most likely sound
				for (Bigram b : pairs) {
					if (b.Bigram_getB1() == curr) {
						count++;
						if (b.Bigram_getProb() > max_prob) {

							best_sampa = b.Bigram_getB2();
							max_prob = b.Bigram_getProb();
							//num_correct -= iters; 
						}

					}
				}
				max_prob = 0.0;
				ipa_sent = sents.get(s);
				correct_sampa = ipa_sent.Sentence_getSent()[i];
				if (best_sampa == correct_sampa) num_correct++;

			}
		}

		double percent_correct = (((double) num_correct / char_count) * 100.0) - char_norm;
		return percent_correct;
	}

	/**
	 * Compute using both EM algorithm and sound trigrams
	 * @param exp int
	 * @return double percent correct
	 */
	public double Decipher(int exp) {
		double max_prob = 0.0;
		double prob = 1.0;
		double percent_correct = 0.0;
		double nchar = 7.0;
		Sentence samp_sent;
		Sentence new_sent;
		Sentence bestSent = null;
		char c1 = '\0';
		char c2 = '\0';
		char c3 = '\0';
		char s1 = '\0';
		char s2 = '\0';
		char s3 = '\0';
		int s_count = 0;
		int corr_count = 0;
		boolean seenTrig = false;
		nchar /= doc_size;
		for (Sentence s : sents.keys()) {
			s_count++;

			for (Bigram b1 : pairs) {
				// if the first character matches the written char of the pair
				if (s.Sentence_getSent()[0] == b1.Bigram_getB1()) {

					c1 = b1.Bigram_getB1();
					s1 = b1.Bigram_getB2();

					prob *= b1.Bigram_getProb();
					for (Bigram b2 : pairs) {
						// second character
						if (s.Sentence_getSent()[1] == b2.Bigram_getB1()) {

							c2 = b2.Bigram_getB1();
							s2 = b1.Bigram_getB2();
							prob *= b2.Bigram_getProb();
							for (Bigram b3 : pairs) {
								// third char
								if (s.Sentence_getSent()[2] == b3.Bigram_getB1()) {

									c3 = b3.Bigram_getB1();
									s3 = b1.Bigram_getB2();
									prob *= b3.Bigram_getProb();
									// exp is the weight of the spelling model 
									for (int j = 0; j < (exp - 1); j++) prob *= prob;
									for (Trigram t : trigs) {
										//StdOut.println("HI!");
										if (t.Trigram_equals(c3, c2, c1)) {

											prob *= t.Trigram_getProb();
											seenTrig = true;
											if (prob > max_prob) {

												max_prob = prob;
												char[] cs1 = {s1, s2, s3};
												bestSent = new Sentence(cs1, 3);
											}

										}
									}
									if (!seenTrig) {
										prob *= (1.0 / (double) char_size);
										seenTrig = true;
										if (prob > max_prob) {
											max_prob = prob;
											char[] cs2 = {s1, s2, s3};
											bestSent = new Sentence(cs2, 3);
										}
									}
								}
							}
						}
					}
				}
			}
			samp_sent = sents.get(s);
			for (int i = 0; i < 3; i++)
				if ((bestSent != null)
						&& (bestSent.Sentence_getSent()[i] == samp_sent.Sentence_getSent()[i]))
					corr_count++;

			max_prob = 0.0;
			seenTrig = false;
		}
		percent_correct = (((double) corr_count / (double) s_count) + nchar) * 100.0;
		return percent_correct;
	}

	public static void main(String[] args) {
		In corpus = new In(args[0]);
		In rules = new In(args[1]);
		In sampa1 = new In(args[2]);
		In sampa2 = new In(args[2]);
		In sampa3 = new In(args[2]);
		In document = new In(args[3]);
		int iterations = Integer.parseInt(args[4]);

		PhoneticDecipherment pd = new PhoneticDecipherment(corpus, rules, sampa1, sampa2, sampa3, document, iterations);

		StdOut.println("Decipherment accuracy for " + iterations + " EM iteration(s):");
		double prob = 0.0;
		prob = pd.SpellingModel();
		StdOut.println("Percent correct, ignoring spelling model: " + prob);

		for (int i = 1; i < 4; i++) {
			prob = pd.Decipher(i);
			StdOut.println("Percent correct, weighting spelling model to power of " + i + ": " + prob);
		}
	}
}