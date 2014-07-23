package uk.co.jtutcher.trainupdater;

import java.util.ArrayList;

public class Track {
	private int min;
	private int max;

	
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	
	public Track(int min, int max)
	{
		this.min = min;
		this.max = max;

	}
	
}
