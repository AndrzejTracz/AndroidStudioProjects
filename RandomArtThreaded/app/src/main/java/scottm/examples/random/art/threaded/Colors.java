package scottm.examples.random.art.threaded;

import android.graphics.Color;
import android.util.Log;

public class Colors implements ColorShader {
	public static final int NUM_COLORS = 1024;
	
	// for grayscale, just create paints ahead of time
	private int[] colors;
	
	public Colors() {
		colors = new int[NUM_COLORS];
		float[] hsv = {0f, 1f, 1f};
//		int prevColor = -1;
		for(int i = 0; i < NUM_COLORS; i++)  {
			float hue = 360f * i / NUM_COLORS;
			hsv[0] = hue;
			colors[i] = Color.HSVToColor(hsv);
//			if(colors[i] == prevColor)
//				Log.d("COLORS", "same color! old: " + prevColor + ", new: " + colors[i]);
		}
	}
	
	public int getNumShades() {
		return NUM_COLORS;
	}
	
	public int getShade(int c) {
		return colors[c];
	}
}
