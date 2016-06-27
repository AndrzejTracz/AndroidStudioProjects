package scottm.examples.random_art;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ArtView extends View {

	private static final String TAG = "Random_Art_View";

	public static final int NUM_GRAYS = 256;

	// the expression used to determine the value at
	// each pixel
	private RandomExpression exp;

	// size of points
	private static final int POINT_SIZE = 1;

	// canvas dimensions
	private int canvasWidth;
	private int canvasHeight;

	// for grayscale, just create paints ahead of time
	private Paint[] grays;

	private Paint[] createGrays() {
		Paint[] result = new Paint[NUM_GRAYS];
		for(int i = 0; i < NUM_GRAYS; i++)  {
			result[i] = new Paint();
			result[i].setARGB(255, i, i, i);
			result[i].setStrokeWidth(POINT_SIZE);
		}
		return result;
	}

	public ArtView(Context context) {
		super(context);

		grays = createGrays();
		// exp = new RandomExpression("yCCSxxMSSAS");
		exp = new RandomExpression("yCxSM");
		// exp = new RandomExpression("xxACSSxCAyCyxASASCAyCCAyyyAAxMSxCxCAxSySMMCMCSMSCS");
		// exp = new RandomExpression();
		setClickListener();
		
//		// uncomment this line to observe how quickly the Activity stops if 
//		// trying to access the network from a GUI thread
		// setImage(); // display image obtained from network based on URL
	}


	public void newArt() {
	    pickNewExpression();
        invalidate();
        Log.d(TAG, "Clicked. New expression: " + exp.toString());
	}
	

	private void setClickListener() {
		this.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pickNewExpression();
				invalidate();
				Log.d(TAG, "Clicked. New expression: " + exp.toString());
			}
			
		    // need to move to Activity!
//			@Override
//			public void onClick(View v) {
//			    new DownloadImageTask().execute("http://www.utexas.edu/sites/default/files/images/longhorn_mark.gif_1.gif");
//			}
		});
	}

	private void pickNewExpression() {
		exp = new RandomExpression();
	}

	//    @Override protected void onDraw(Canvas canvas) {
	//        canvas.drawColor(Color.BLACK);
	//
	//        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	//        circlePaint.setColor(Color.RED);
	//        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/3, circlePaint);
	//        
	//    }

	@Override
	protected void onDraw(Canvas canvas) {
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();
		drawPicture(canvas, 1);
	}

	private void drawPicture(Canvas c, int POINT_SIZE) {
		Log.d(TAG, "in drawPicture method!");
		Log.d(TAG, "in drawPicture method 2!");
		final int MAX_X = canvasWidth;
		Log.d(TAG, "in drawPicture method still. MAX_X " + MAX_X);
		final int MAX_Y = canvasHeight;
		Log.d(TAG, "in drawPicture method still. MAX_Y " + MAX_Y);
		final double X_INC = 2.0 / MAX_X * POINT_SIZE;
		Log.d(TAG, "in drawPicture method still. X_INC " + X_INC);
		final double Y_INC = 2.0 / MAX_Y * POINT_SIZE;
		Log.d(TAG, "in drawPicture method still. Y_INC " + Y_INC);
		double xVal = -1.0;   
		Log.d(TAG, "in drawPicture method still. x_val " + xVal);
		for(int x = 0; x < MAX_X; x += POINT_SIZE) {
			double yVal = -1.0;
			// Log.d(TAG, "in ArtThread drawPicture method still. x " + x);
			for(int y = 0; y < MAX_Y; y += POINT_SIZE) {
				int shade = getGrayShade(xVal, yVal);
				grays[shade].setStrokeWidth(POINT_SIZE);
				c.drawPoint(x, y, grays[shade]);
				yVal += Y_INC;
			}
			xVal += X_INC;
		}
		Log.d(TAG, "in ArtThread drawPicture method! Done drawing");
	}

	private int getGrayShade(double x, double y) {
		double val = exp.getResult(x, y);
		int result = (int)((val + 1.0) / 2.0 * NUM_GRAYS);
		result = (result == NUM_GRAYS) ? 255 : result;
		assert 0 <= result && result < NUM_GRAYS : result + " " + val;
		return result;
	}
}
