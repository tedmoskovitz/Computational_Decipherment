// Ted Moskovitz
// a trigram/char triplet data type 
public class Trigram 
{
	
	private char  t1; 
	private char  t2; 
	private char  t3; 
	private int count; 
	private double prob; 

	public Trigram(char  trig1, char  trig2, char  trig3)
	{
		t1 = trig1; 
		t2 = trig2; 
		t3 = trig3;
		count = 1; 
		prob = 0.0; 
	}

	public void Trigram_setProb(double newprob)
	{
		this.prob = newprob;
	}

	public double Trigram_getProb()
	{
		return this.prob; 
	}

	public boolean Trigram_equals(Trigram t)
	{
		if ((this.t1 == t.t1) && (this.t2 == t.t2) && (this.t3 == t.t3))
			return true; 	
		else		
			return false; 	  
	}

	public boolean Trigram_equals(char c1, char c2, char c3)
	{
		if ((this.t1 == c1) && (this.t2 == c2) && (this.t3 == c3))
			return true; 
		else 
			return false; 
	}


	public char Trigram_getT1()
	{
		return this.t1; 
	}

	public char Trigram_getT2()
	{
		return this.t2; 
	}

	public char Trigram_getT3()
	{
		return this.t3; 
	}

	public int Trigram_getFreq()
	{
		return this.count; 
	}

	public void Trigram_incFreq()
	{
		this.count++; 
	}

	public void Trigram_print()
	{
		StdOut.print("Trigram: " + this.t1 + " " + this.t2 
			+ " " + this.t3);
	}

}