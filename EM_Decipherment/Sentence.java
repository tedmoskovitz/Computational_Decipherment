// Ted Moskovitz
// a sentence data type
public class Sentence 
{
	private char[] sent; 
	private int length; 
	private final int SIZE = 3; 

	public Sentence(char[] sentence, int len)
	{
		
		this.sent = new char[SIZE];
		for (int i = 0; i < len; i++)
			this.sent[i] = sentence[i];
 
		this.length = len; 
	}

	public char[] Sentence_getSent()
	{ return this.sent; }

	public int Sentence_getLen()
	{ return this.length; }

	public void Sentence_print()
	{
		for (int i = 0; i < this.length; i++)
			StdOut.print(this.sent[i]);
		StdOut.println();  
	}

}