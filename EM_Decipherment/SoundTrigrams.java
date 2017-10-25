// Ted Moskovitz
// usage: java SoundTrigrams corpus.txt target.txt
// a trigram language model 
public class SoundTrigrams 
{
	
	private In ipa_source; 
	private LinearProbingHashST<Trigram, Integer> trig_Counts; 
	private LinearProbingHashST<Bigram, Integer> big_Counts; 
	private Bag<Character> sounds; 

	public SoundTrigrams(In ipa_source)
	{
		
		this.ipa_source = ipa_source; 
		trig_Counts = new LinearProbingHashST<Trigram, Integer>(); 
		big_Counts = new LinearProbingHashST<Bigram, Integer>(); 
		sounds = new Bag<Character>(); 
	}

	// return trigrams that have been seen 
	public Bag<Trigram> SoundTrigrams_getTrigs()
	{
		Bag<Trigram> trigs = new Bag<Trigram>(); 
		for (Trigram t : trig_Counts.keys())
		{
			trigs.add(t);
		}
		return trigs; 
	}

	// iV
	public int SoundTrigrams_getV() { return sounds.size(); }

	// find target probability 
	public double SoundTrigrams_prob(In target)
	{
		int iV = sounds.size(); 
		char cCurr = '\0'; 
		char cPrev = '\0'; 
		char cPrevPrev = '\0'; 
		Bigram big; 
		Trigram trig; 
		double prob = 1.0; 
		double newprob = 0.0; 

		// parse target, adjust probability 
		while (target.hasNextChar())
		{
			cCurr = target.readChar(); 

			// wait for 3rd char/sound 
			if ((cPrev != '\0') && (cPrevPrev != '\0'))
			{
				// if seen both trigram and bigram 
				if (((big = seen_Bigram(cPrev, cPrevPrev)) != null) 
					&& ((trig = seen_Trigram(cCurr, cPrev, cPrevPrev)) != null))
				{
					// update probability with add-one smoothing 
					newprob = (trig_Counts.get(trig) + 1.0) / (big_Counts.get(big) + iV);
					prob = prob * newprob; 
					// update trigram prob
					trig.Trigram_setProb(newprob);
				}

				// if seen only bigram
				else if (((big = seen_Bigram(cPrev, cPrevPrev)) != null) 
					&& (!((trig = seen_Trigram(cCurr, cPrev, cPrevPrev)) != null)))
				{
					// update probability with add-one smoothing 
					newprob = 1.0 / (big_Counts.get(big) + iV);
					prob = prob * newprob; 
					trig = new Trigram(cCurr, cPrev, cPrevPrev);
					trig.Trigram_setProb(newprob);

				}

				// if seen neither 
				else 
				{
					// update probability with add-one smoothing
					prob = prob * (1.0 / iV); 
					trig = new Trigram(cCurr, cPrev, cPrevPrev);
					trig.Trigram_setProb(1.0 / (double)iV);
				}
			}

			// update cPrev and cPrevPrev
			cPrevPrev = cPrev; 
			cPrev = cCurr; 
		}

		for (Trigram tri : trig_Counts.keys())
			if (tri.Trigram_getProb() == 0.0) tri.Trigram_setProb(1.0 / (double)iV);

		return prob; 
	}

	// print trigrams
	public void print_Trigs()
	{
		for (Trigram t : trig_Counts.keys())
		{
			t.Trigram_print(); 
			StdOut.println("; count: " + trig_Counts.get(t));
		}
			
	}

	// print bigrams
	public void print_Bigs()
	{
		for (Bigram b : big_Counts.keys())
		{
			b.Bigram_print(); 
			StdOut.println("; count: " + big_Counts.get(b));
		}		
	}

	// print sounds/unigrams
	public void print_Sounds()
	{
		for (Character c : sounds)
			StdOut.println("Sound: " + c);
	}

	// returns matching bigram if found; if not, returns null
	private Bigram seen_Bigram(Bigram big)
	{
		for (Bigram b : big_Counts.keys())
		{
			if (b.Bigram_equals(big))
				return b;

		}
		return null; 
	}

	// returns matching bigram if found; if not, returns null
	private Bigram seen_Bigram(char c1, char c2)
	{
		for (Bigram b : big_Counts.keys())
		{
			if (b.Bigram_equals(c1, c2))
				return b;

		}
		return null; 
	}

	// returns matching trigram if found; if not, returns null
	private Trigram seen_Trigram(Trigram trig)
	{
		for (Trigram t : trig_Counts.keys())
		{
			if (t.Trigram_equals(trig))
				return t;

		}
		return null; 
	}

	// returns matching trigram if found; if not, returns null
	private Trigram seen_Trigram(char c1, char c2, char c3)
	{
		for (Trigram t : trig_Counts.keys())
		{
			if (t.Trigram_equals(c1, c2, c3))
				return t;

		}
		return null; 
	}

	// parse corpus and fill in table of frequencies 
	public void SoundTrigrams_Process_doc()
	{
		char cPrev = '\0'; 
		char cPrevPrev = '\0'; 
		char cCurr = '\0'; 
		Bigram newBig; 
		Trigram newTrig; 
		int old_count; 
		boolean isNewChar = true; 

		// parse corpus and create bigrams and trigrams
		while (ipa_source.hasNextChar())
		{
			isNewChar = true; 
			cCurr = ipa_source.readChar(); 

			if ((cCurr != ' ') && (cCurr != ',') 
					&& (cCurr != '-') && (cCurr != '«') 
					&& (cCurr != '»') && (!Character.isDigit(cCurr))
					&& (cCurr != ')') && (cCurr != '(')
					&& (cCurr != '\'') && (cCurr != '\"')) 
			{

			// unigrams
			for (Character c : sounds)
			{
				if (c == cCurr)
				{
					// if seen sound before, note it and exit loop
					isNewChar = false; 
					break; 
				}
			}
			if (isNewChar)
				sounds.add(cCurr);

			// bigrams
			if (cPrev != '\0')
			{
				// have seen this bigram before? 
				if ((newBig = seen_Bigram(cCurr, cPrev)) != null)
				{
					// if so, increment count
					old_count = big_Counts.get(newBig);
					big_Counts.put(newBig, old_count + 1);
				}
				// else, add the new Bigram 
				else 
				{
					newBig = new Bigram(cCurr, cPrev);
					big_Counts.put(newBig, 1);
				}		
			}

			// trigrams
			if ((cPrev != '\0') && (cPrevPrev != '\0'))
			{
				// have seen this trigram before? 
				if ((newTrig = seen_Trigram(cCurr, cPrev, cPrevPrev)) != null)
				{
					// if so, increment count 
					old_count = trig_Counts.get(newTrig); 
					trig_Counts.put(newTrig, old_count + 1);
				}
				// else, add the new Trigram
				else 
				{
					newTrig = new Trigram(cCurr, cPrev, cPrevPrev); 
					trig_Counts.put(newTrig, 1);
				}
			}

			// set new cPrev and cPrevPrev
			cPrevPrev = cPrev; 
			cPrev = cCurr; 
		}
		}
	}

	// for testing 
	public static void main(String[] args)
	{
		In corpus = new In(args[0]);
		SoundTrigrams st = new SoundTrigrams(corpus);
		st.SoundTrigrams_Process_doc(); 
		st.print_Sounds(); 
		StdOut.println(); 
		st.print_Bigs(); 
		StdOut.println(); 
		//st.print_Trigs(); 

		In targ = new In(args[1]);
		double prob = st.SoundTrigrams_prob(targ);
		StdOut.println(); 
		StdOut.println("The probability of this target is " + prob + ".");

		Bag<Trigram> ts = st.SoundTrigrams_getTrigs(); 
		for (Trigram t : ts)
			StdOut.println(t.Trigram_getProb());

	}


}