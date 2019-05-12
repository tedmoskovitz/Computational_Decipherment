package com.example.decipher;

/**
 * Ted Moskovitz
 * A bigram/char pair data type
 */
public class Bigram {

	private char b1;
	private char b2;
	private int count;
	private double prob;
	private double tc;

	public Bigram(char big1, char big2) {
		b1 = big1;
		b2 = big2;
		count = 1;
		prob = 0.0;
		tc = 0.0;
	}

	public void Bigram_setProb(double newProb) {
		this.prob = newProb;
	}

	public double Bigram_getProb() {
		return this.prob;
	}

	public void Bigram_setTC(double newTC) {
		this.tc = newTC;
	}

	public double Bigram_getTC() {
		return this.tc;
	}

	public boolean Bigram_equals(Bigram b) {
		return (this.b1 == b.b1) && (this.b2 == b.b2);
	}

	public boolean Bigram_equals(char c1, char c2) {
		return (this.b1 == c1) && (this.b2 == c2);
	}


	public char Bigram_getB1() {
		return this.b1;
	}

	public char Bigram_getB2() {
		return this.b2;

	}

	public void Bigram_incFreq() {
		this.count++;
	}

	public int Bigram_getFreq() {
		return this.count;
	}

	public void Bigram_print() {
		StdOut.print("Bigram: " + this.b1 + " " + this.b2);
	}

}