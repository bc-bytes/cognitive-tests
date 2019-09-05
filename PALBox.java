package com.bc.memorytest;

/**
 * Class used to define a single box in the PAL test.
 * 
 * @author Bill Cassidy
 */
public class PALBox
{
	private int boxIndex;
	private int shapeIndex;
	
	/**
	 * Constructor for PALBox class.
	 * 
	 * @param boxIndex
	 *   Index of this box.
	 *   
	 * @param shapeID
	 *   Index number of the shape contained in this box (use 9999 for no shape).
	 */
	public PALBox(int boxIndex, int shapeIndex, boolean boxOpenIndex)
	{
		this.boxIndex = boxIndex;
		this.shapeIndex = shapeIndex;
	}
	
	public int getBoxIndex()
	{
		return boxIndex;
	}
	
	public int getShapeIndex()
	{
		return shapeIndex;
	}
	
	public void setBoxIndex(int boxIndex)
	{
		this.boxIndex = boxIndex;
	}
	
	public void setShapeIndex(int shapeIndex)
	{
		this.shapeIndex = shapeIndex;
	}
}