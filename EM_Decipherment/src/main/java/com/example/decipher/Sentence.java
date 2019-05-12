package com.example.decipher;


/**
 * Ted Moskovitz
 * a sentence data type
 */
public class Sentence {
	private char[] sent;
	private int length;
	private final int SIZE = 3;

	public Sentence(char[] sentence, int len) {

		this.sent = new char[SIZE];
		if (len >= 0) System.arraycopy(sentence, 0, this.sent, 0, len);

		this.length = len;
	}

	public char[] Sentence_getSent() {
		return this.sent;
	}

	public int Sentence_getLen() {
		return this.length;
	}

	public void Sentence_print() {
		for (int i = 0; i < this.length; i++)
			StdOut.print(this.sent[i]);
		StdOut.println();
	}
}