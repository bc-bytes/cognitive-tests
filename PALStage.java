package com.bc.memorytest;

/**
 * Class to ecapsulate a PAL test definition for a single stage.
 * 
 * @author Bill Cassidy
 */
public class PALStage
{
	private int numberOfSets;
	private int numberOfShapes;
	
	public PALStage(int numberOfSets, int numberOfShapes)
	{
		this.numberOfSets = numberOfSets;
		this.numberOfShapes = numberOfShapes;
	}
	
	public int getNumberOfSets()
	{
		return numberOfSets;
	}
	
	public int getNumberOfShapes()
	{
		return numberOfShapes;
	}
}