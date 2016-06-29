package scottm.examples.random.art.threaded;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BulkUploadActivity extends Activity {

    private DatabaseReference myRef;
    private BufferedReader reader;
    private TextView status_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_upload);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("equationlist");
        status_tv = (TextView) this.findViewById(R.id.status);
    }



    public void upload(View view) {
        Resources res = getResources();
        InputStream inputStream  = res.openRawResource(R.raw.old);
        reader = new BufferedReader(new InputStreamReader((inputStream)));
        addNext();
    }

    private void addNext() {
        try {
            String line = reader.readLine();
            if (line != null) {
                String[] data = line.split(" ");
                String equation = data[1];
                int id = Integer.parseInt(data[0]);
                int upVotes = 1;
                int downVotes = 0;
                long timestamp = Long.parseLong(data[2]);
                EquationForStorage next = new EquationForStorage(equation, id, upVotes, downVotes, timestamp);
                myRef.child("" + id).setValue(next);
                status_tv.append("\n\nAdded " + next.getEquation() + ". Resting now.");
                rest();
            }
        }
        catch (IOException e) {
            Log.e("Bulk Upload", e.toString());
        }
    }

    private void rest() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                status_tv.append("\n\nDone Resting. Adding next");
                addNext();
            }
        }, 5000);

    }
}
