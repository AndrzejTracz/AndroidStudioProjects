package scottm.examples.random_art;

import android.os.Bundle;
import android.app.Activity;
public class RandomArtActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ArtView(this));
    }

}
