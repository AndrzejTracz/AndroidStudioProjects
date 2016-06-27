package scottm.examples.random_art;

import android.app.Activity;
import android.os.Bundle;

public class ShowNewArtActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(new ArtView(this));
    }
}
