package scottm.examples.random.art.threaded;

import android.graphics.Color;

public class Grays implements ColorShader {

	public static final int NUM_GRAYS = 256;
	
	// for grayscale, just create paints ahead of time
	private int[] grays;
	
	public Grays() {
		grays = new int[NUM_GRAYS];
		for(int i = 0; i < NUM_GRAYS; i++)  {
			grays[i] = Color.rgb(i, i, i);
		}
	}
	
	public int getNumShades() {
		return NUM_GRAYS;
	}
	
	public int getShade(int g) {
		return grays[g];
	}

}
