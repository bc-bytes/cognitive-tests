package com.bc.memorytest;

public class CRTDetail 
{
	public int runNumber;
	public int correctResponse; // correct box index (zero based)
	public int subjectResponse; // index of box chosen by user (zero based)
	public long responseTime;
	public float interStimulusInterval;
}