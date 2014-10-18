package com.morgane_perron.findmyduck;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class FindMyDuck extends Activity implements View.OnClickListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private ImageView imgDuck;
    private int xPosition;
    private int yPosition;
    private Button speakButton;
    private SoundView soundView;

    private static final int VOLUME = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_duck);
        //changeMainContent(DuckVoice.newInstance());

        imgDuck = (ImageView) findViewById(R.id.imgDuck);
        //imgDuck.setVisibility(View.INVISIBLE);

        xPosition = (int)(Math.random() * 1000); //Trouver les bons paramètres
        yPosition = (int)(Math.random() * 1000);
        Log.e("position", xPosition + " " + yPosition);
        imgDuck.setX(xPosition);
        imgDuck.setY(yPosition);

        speakButton = (Button) findViewById(R.id.buttonVoice);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
// pas de reconnaissance, on désactive le déclencheur
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }

        soundView = (SoundView) findViewById(R.id.soundView);
        //soundView.setOnClickListener(this);
        soundView.setOnTouchListener(new MyTouchListener());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_my_duck, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeMainContent(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment
        transaction.replace(R.id.gameContent, fragment);

        // Commit the transaction
        transaction.commit();
    }


    public void onClick(View v) {
        Log.e("onclick",":P");
        // si on a cliqué sur le bouton
        if (v.getId() == R.id.buttonVoice) {
            // création d’une nouvelle opération, opération de reconnaissance
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            // paramétrage pour une reconnaissance du langage naturel
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // un texte particulier pour la boite de dialogue
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fisheye - Speech recognition demo");
            // on lance la reconnaissance
            // VOICE_RECOGNITION_REQUEST_CODE est un code à nous pour identifier la réponse
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        } else {
            Log.e("on click", v.getX() + " " + v.getY());
            float mousePositionX = soundView.getX();
            float mousePositionY = soundView.getY();
            double distance = Math.sqrt(Math.pow(mousePositionX - xPosition,2) + Math.pow(mousePositionY - yPosition,2));
            //using any where`

//int streamType, int volume
            int volume = (int)(distance*VOLUME);
            ToneGenerator toneG = new ToneGenerator(3,volume);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); //200 is duration in ms

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        Log.e("On ActivityResult", "On ActivityResult");
        // on vérifie que la réponse est bonne et pour nous
        // RESULT_OK est une constante de la classe Activity
        // ce code extrait était placé dans une Activity
        if (requestCode ==  VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // on récupère les réponses
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            int nb_rep = matches.size();
            /*if (nb_rep > 0)
            {
                // par exemple ici, s’il y en a, on s’intéresse aux
                // 3 premières réponses pour ne pas être trop éloigné
                int nb_test = Math.min(3, nb_rep);
                for(int i = 0; i < nb_test; i++)
                {
                    if (matches.get(0).toLowerCase().equals("zoomer"))
                    {
                        // code à exécuter quand on a dit « zoomer »
                        break;
                    }
                    // etc.
                }
            }*/
            //Code qui montre le canard
        }

    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                float mousePositionX = motionEvent.getX();
                float mousePositionY = motionEvent.getY();
                double distance = Math.sqrt(Math.pow(mousePositionX - xPosition,2) + Math.pow(mousePositionY - yPosition,2));
                //using any where`
                int volume = (int)(distance*distance*VOLUME);
                Log.e("Volume", volume + "");
                ToneGenerator toneG = new ToneGenerator(3,volume);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); //200 is duration in ms
                return true;
            }
            return false;
        }
    }

}
