# Ted Moskovitz
# Final Project: Computational Decipherment
# RunDecipherment.sh

#!/bin/bash

# compile princeton programs (adapted from http://algs4.cs.princeton.edu/code/): 
javac StdIn.java
javac StdOut.java
javac In.java
javac Out.java
javac LinearProbingHashST.java
javac Bag.java

# compile my programs:
javac Bigram.java
javac Trigram.java
javac SAMPA_Converter.java
javac Sentence.java
javac SoundTrigrams.java
javac EM.java
javac PhoneticDecipherment.java

# run: 
java SAMPA_Converter corp.txt span_rules.txt
# 1, 5, 10, 15 EM iterations (results are standard output):
java PhoneticDecipherment corp.txt span_rules.txt phonetic_corp.txt donquixote1.txt 1
java PhoneticDecipherment corp.txt span_rules.txt phonetic_corp.txt donquixote1.txt 5
java PhoneticDecipherment corp.txt span_rules.txt phonetic_corp.txt donquixote1.txt 10
java PhoneticDecipherment corp.txt span_rules.txt phonetic_corp.txt donquixote1.txt 15
