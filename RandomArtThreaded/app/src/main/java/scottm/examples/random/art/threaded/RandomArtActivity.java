package scottm.examples.random.art.threaded;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Random;

public class RandomArtActivity extends Activity {

	private static final String TAG = "Random Art Threaded";
	
	private ImageView artImage;
	private ProgressBar progressBar;
	private Grays grays;
	private Colors colors;

    // the expression used to determine the value at each pixel
	private RandomExpression exp;
	private boolean pickRandomExpression;
	private boolean useColors;
    private boolean artInProgress;
    private Random r;
    private ArtTaskInner artTaskInner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(this, "GACBq6Jwvf2PL7EIl3IRpvav7GEUZdki8gcSojgK", "l2PuQKbbnbXimsYrsgk0P7W2uOFxk89nfQrdy97r");

        r = new Random(3162000);
        this.setContentView(R.layout.main);
        artImage = (ImageView) this.findViewById(R.id.imageView1);
        artImage.setImageResource(R.drawable.default_art);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
        grays = new Grays();
        colors = new Colors();
        Log.d(TAG, "dimensions of image view: " + artImage.getWidth() + " " + artImage.getHeight());
        
        // start with blank and white
        useColors = false;
        // testParse();
        // setSequence();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "in onPause");
        if (artTaskInner != null && artInProgress) {
            Log.d(TAG, "calling cancel on artTaskInner");
            artTaskInner.cancel(true);
        }
    }
//    private void setSequence() {
//    	ParseObject testObject = new ParseObject("Sequence");
//    	testObject.put("index", 65);
//    	testObject.saveInBackground();
//    }

    // for testing only
    private void testParse() {
    	ParseObject testObject = new ParseObject("TestObject");
    	testObject.put("foo", "bar");
    	testObject.saveInBackground();
	}


	public void newArt(View v) {
        Log.d(TAG, "dimensions of image view: " + artImage.getWidth() + " " + artImage.getHeight());
    	if(!artInProgress) {
			artInProgress = true;
			pickRandomExpression = true;
            artTaskInner = new ArtTaskInner();
            artTaskInner.execute(artImage.getWidth(), artImage.getHeight());
		}
    }
    
	public void saveEquation(View v) {
		if(exp != null && pickRandomExpression) {

            final int[] count = {0};

            ParseQuery<ParseObject> countQuery
                    = ParseQuery.getQuery("ArtExpressionCount");

            countQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject masterCount, ParseException e) {
                    if(e == null) {
                        count[0] = masterCount.getInt("TheCount");
                        Log.d(TAG, "The Count via the master count object: " + count[0]);
                        masterCount.increment("TheCount");
                        masterCount.saveInBackground();

                        ParseObject currentExpression
                                    = new ParseObject("ArtExpression");

                        currentExpression.put("equation", exp.toString());
                        currentExpression.put("votes", 1);
                        currentExpression.put("index", count[0]);
                        currentExpression.saveInBackground();
                    } else {
                        Log.d(TAG, "Unable to get count, not saving expression. Exception: " + e);
                    }
                }
            });

		}
	}
	

        // SHOULD CHECK THAT IF CURRENT DISPLAY IS SAVED VERSION, WE DON"T PICK THE SAME
        // EQUATION. MUST BE NEW!!!!
    public void getRandomGoodArt(View v) {
		pickRandomExpression = false;

        ParseQuery<ParseObject> countQuery
                = ParseQuery.getQuery("ArtExpressionCount");

        countQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject masterCount, ParseException e) {
                if (e == null) {
                    int count = masterCount.getInt("TheCount");
                    int randomIndex = r.nextInt(count);
                    Log.d(TAG, "The Count via the master count object: " + count);

                    ParseQuery<ParseObject> query
                                = ParseQuery.getQuery("ArtExpression");
                    query.whereGreaterThanOrEqualTo("index", randomIndex);
                    query.getFirstInBackground(setRandomExpressionFromQuery);
                } else {
                    Log.d(TAG, "Unable to get count to get random expression");
                }
            }
        });
	}
	
	
	private GetCallback<ParseObject> setRandomExpressionFromQuery  
											= new GetCallback<ParseObject>() {
		public void done(ParseObject object, ParseException e) {
			if (e == null) {
                Log.d(TAG, "returned object: " + object);
				String equation = object.getString("equation");
				exp = new RandomExpression(equation);
				// now draw it
				Log.d(TAG, "equation: " + equation);
				Log.d(TAG, "index of expression: " + object.getInt("index"));
				new ArtTaskInner().execute(artImage.getWidth(), artImage.getHeight());
			} else {
				Log.d(TAG, "Unable to get the given random expression");
				exp = null; // so we just pick a new random
			}
		}
	};
	
	
    private class ArtTaskInner extends AsyncTask<Integer, Integer, Bitmap>{

    	
    	private Bitmap image;
  
    	private ColorShader shader;
    	
    	public ArtTaskInner() {
    		shader = useColors ? colors : grays;
    	}
    	
    	@Override
    	protected Bitmap doInBackground(Integer... dimensions) {
    		if(pickRandomExpression || exp == null)
    			exp = new RandomExpression();
    		
    		Log.d(TAG, "current expression: " + exp.toString());
    		createBitmap(dimensions);
    		final int MAX_X = dimensions[0];
    		final int MAX_Y = dimensions[1];
            final int POINT_SIZE = 1;
    		final double X_INC = 2.0 / MAX_X; // * POINT_SIZE; FOR LOWER RESOLUTION IMAGES
    		final double Y_INC = 2.0 / MAX_Y ; // * POINT_SIZE; FOR LOWER RESOLUTION IMAGES
    		final int SHADE = shader.getNumShades();
    		double xVal = -1.0;
            for(int x = 0; x < MAX_X; x += POINT_SIZE) {
    			double yVal = -1.0;
    			for(int y = 0; y < MAX_Y; y += POINT_SIZE) {
    				int shade = getGrayShade(xVal, yVal, SHADE);
    				image.setPixel(x, y, shader.getShade(shade));
    				yVal += Y_INC;
    			}
    			publishProgress(x);
                if (isCancelled()) {
                    break; // GACK!!!!!!!!!!!
                }
    			xVal += X_INC;
    		}
    		return image;
    	}

    	protected void onProgressUpdate(Integer... progress) {
    		progressBar.setProgress( (int) ((100.0 * progress[0]) / artImage.getWidth()) );
    	}
    	
    	protected void onPostExecute(Bitmap result) {
            artInProgress = false;
    		artImage.setImageBitmap(result);
    	}

    	private void createBitmap(Integer[] dimensions) {
			image = Bitmap.createBitmap(dimensions[0], dimensions[1], Bitmap.Config.ARGB_8888);
			Log.d(TAG, "BITMAP size: " + image.getWidth() + image.getHeight());
		}


    	private int getGrayShade(double x, double y, int shades) {
    		double val = exp.getResult(x, y);
    		int result = (int)((val + 1.0) / 2.0 * shades);
    		result = (result == shades) ? shades - 1 : result;
    		// assert 0 <= result && result < Grays.NUM_GRAYS : result + " " + val;
    		return result;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.black_and_white:
                useColors = false;
                return true;
            case R.id.colors:
                useColors = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
