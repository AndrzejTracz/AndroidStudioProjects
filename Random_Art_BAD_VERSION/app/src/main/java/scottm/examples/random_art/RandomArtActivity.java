package scottm.examples.random_art;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
public class RandomArtActivity extends Activity {

    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        imageView = (ImageView) this.findViewById(R.id.imageView1);
    }

    public void newArt(View v) {
        Log.d("Random Art", "Starting new art activity");
        Intent intent = new Intent(this, ShowNewArtActivity.class);
        this.startActivity(intent);
    }


    //  // unsafe method, access network on UI thread
    public void UNSAFE_loadImage(View v) {
        Bitmap b = loadImageFromNetwork(getString(R.string.sample_art_url));
        if(b != null)
            imageView.setImageBitmap(b);
        else
            imageView.setImageResource(R.drawable.default_art);
    }


    //  // safe method to access network in AsyncTask
    public void SAFE_loadImage(View v) {
        // Examples below to load image from network the right way
        DownloadImageTask d = new DownloadImageTask();
        d.execute(getString(R.string.sample_art_url));

    }

    private Bitmap loadImageFromNetwork(String imageURL) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageURL);
            bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
        }
        catch(IOException e) {
            Log.d("Random Art", "problem reading from url: " + imageURL + ", " + e);
        }
        return bitmap;
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        protected Bitmap doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null)
                imageView.setImageBitmap(result);
            else
                imageView.setImageResource(R.drawable.default_art);
        }

        private Bitmap loadImageFromNetwork(String imageURL) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageURL);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            }
            catch(IOException e) {
                Log.d("Random Art", "problem reading from url: " + imageURL + ", " + e);
            }
            return bitmap;
        }
    }

}
