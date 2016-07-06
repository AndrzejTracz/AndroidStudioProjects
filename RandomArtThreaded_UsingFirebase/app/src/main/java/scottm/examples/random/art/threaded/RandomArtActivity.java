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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class RandomArtActivity extends Activity {

    private static final String TAG = "Random Art Threaded";

    private ImageView artImage;
    private ProgressBar progressBar;
    private Grays grays;
    private Colors colors;

    // the expression used to determine the value at each pixel
    private RandomExpression exp;
    private boolean currentExpressionIsNew;
    private boolean useColors;
    private boolean artInProgress;
    private Random r;
    private ArtTaskInner artTaskInner;
    private DatabaseReference equationListDatabase;
    private DatabaseReference equationCountDatabase;
    private int equationCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setUpFirebase();
        // testFirebase();
    }

    private void setUpFirebase() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        equationListDatabase = database.getReference(getString(R.string.firebase_equation_list_name));
        // Log.d(TAG, "equation list key is: " + equationListDatabase.child("12"));
        equationCountDatabase = database.getReference(getString(R.string.firebase_equation_count_name));

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d(TAG, "onDataChanged call for Value Event Listener. new value: " + dataSnapshot.getValue());
                equationCount = ((Long) dataSnapshot.getValue()).intValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        equationCountDatabase.addValueEventListener(postListener);
    }

//    private void testFirebase() {
//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("test message 1252");
//
//        myRef.setValue("Hello, World!!!!!!!!");
//    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "in onPause");
        if (artTaskInner != null && artInProgress) {
            Log.d(TAG, "calling cancel on artTaskInner");
            artTaskInner.cancel(true);
            artInProgress = false;
        }
    }


    public void newArt(View v) {
        Log.d(TAG, "dimensions of image view: " + artImage.getWidth() + " " + artImage.getHeight());
        if (!artInProgress) {
            artInProgress = true;
            currentExpressionIsNew = true;
            artTaskInner = new ArtTaskInner();
            artTaskInner.execute(artImage.getWidth(), artImage.getHeight());
        }
    }

//    private void onStarClicked(DatabaseReference postRef) {
//        postRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Post p = mutableData.getValue(Post.class);
//                if (p == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (p.stars.containsKey(getUid())) {
//                    // Unstar the post and remove self from stars
//                    p.starCount = p.starCount - 1;
//                    p.stars.remove(getUid());
//                } else {
//                    // Star the post and add self to stars
//                    p.starCount = p.starCount + 1;
//                    p.stars.put(getUid(), true);
//                }
//
//                // Set value and report transaction success
//                mutableData.setValue(p);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
//            }
//        });
//    }

    public void saveEquation(View v) {
        if (exp != null && currentExpressionIsNew) {
            currentExpressionIsNew = false;
            int newCount = equationCount + 1;
            Log.d(TAG, "Setting new count. Old count: " + equationCount + ", new count: " + newCount);
            equationCountDatabase.setValue(newCount);
            String equation = exp.toString();
            // Add current equation to Firebase database
            EquationForStorage newExpression
                    = new EquationForStorage(equation, newCount, 1, 0, System.currentTimeMillis());
            equationListDatabase.child("" + newCount).setValue(newExpression);
        }
    }

    private void testFirebase(int count, String equation) {
        // Add current equation to cloud
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("equationlist");
        EquationForStorage newExpression = new EquationForStorage(equation, count, 1, 0, System.currentTimeMillis());

        myRef.child("" + count).setValue(newExpression);

    }


    public void getRandomGoodArt(View v) {
        if (!artInProgress) {
            currentExpressionIsNew = false;
            int randomID = r.nextInt(equationCount);
            equationListDatabase.child(randomID + "").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            EquationForStorage eq
                                    = dataSnapshot.getValue(EquationForStorage.class);
                            Log.d(TAG, "read expression: " + eq.getEquation());
                            exp = new RandomExpression(eq.getEquation());
                            // now draw it
                            Log.d(TAG, "index / id of expression: " + eq.getId());
                            new ArtTaskInner().execute(artImage.getWidth(), artImage.getHeight());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }


//    private GetCallback<ParseObject> setRandomExpressionFromQuery
//            = new GetCallback<ParseObject>() {
//        public void done(ParseObject object, ParseException e) {
//            if (e == null) {
//                Log.d(TAG, "returned object: " + object);
//                String equation = object.getString("equation");
//                exp = new RandomExpression(equation);
//                // now draw it
//                Log.d(TAG, "equation: " + equation);
//                Log.d(TAG, "index of expression: " + object.getInt("index"));
//                new ArtTaskInner().execute(artImage.getWidth(), artImage.getHeight());
//            } else {
//                Log.d(TAG, "Unable to get the given random expression");
//                exp = null; // so we just pick a new random
//            }
//        }
//    };


    private class ArtTaskInner extends AsyncTask<Integer, Integer, Bitmap> {


        private Bitmap image;

        private ColorShader shader;

        public ArtTaskInner() {
            shader = useColors ? colors : grays;
        }

        @Override
        protected Bitmap doInBackground(Integer... dimensions) {
            if (currentExpressionIsNew || exp == null)
                exp = new RandomExpression();

            Log.d(TAG, "current expression: " + exp.toString());
            createBitmap(dimensions);
            final int MAX_X = dimensions[0];
            final int MAX_Y = dimensions[1];
            final int POINT_SIZE = 1;
            final double X_INC = 2.0 / MAX_X; // * POINT_SIZE; FOR LOWER RESOLUTION IMAGES
            final double Y_INC = 2.0 / MAX_Y; // * POINT_SIZE; FOR LOWER RESOLUTION IMAGES
            final int SHADE = shader.getNumShades();
            double xVal = -1.0;
            for (int x = 0; x < MAX_X; x += POINT_SIZE) {
                double yVal = -1.0;
                for (int y = 0; y < MAX_Y; y += POINT_SIZE) {
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
            progressBar.setProgress((int) ((100.0 * progress[0]) / artImage.getWidth()));
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
            int result = (int) ((val + 1.0) / 2.0 * shades);
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
