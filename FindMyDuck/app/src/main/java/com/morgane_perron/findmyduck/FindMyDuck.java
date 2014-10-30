package com.morgane_perron.findmyduck;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class FindMyDuck extends Activity implements View.OnClickListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private ImageView imgDuck;
    //private int xPosition;
    //private int yPosition;
    private Point positionDuck;
    private Button speakButton;
    private Button hautButton;
    private Button basButton;
    private Button droitButton;
    private Button gaucheButton;
    private SoundView soundView;
    private int width;
    private int height;

    private MediaPlayer mPlayer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_duck);
        //changeMainContent(DuckVoice.newInstance());
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        imgDuck = (ImageView) findViewById(R.id.imgDuck);
        //imgDuck.setMinimumHeight(200000);
        //imgDuck.setVisibility(View.INVISIBLE);
        speakButton = (Button) findViewById(R.id.buttonVoice);
        hautButton = (Button) findViewById(R.id.buttonHaut);
        basButton = (Button) findViewById(R.id.buttonBas);
        gaucheButton = (Button) findViewById(R.id.buttonGauche);
        droitButton = (Button) findViewById(R.id.buttonDroit);

        hautButton.setOnClickListener(this);
        basButton.setOnClickListener(this);
        gaucheButton.setOnClickListener(this);
        droitButton.setOnClickListener(this);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        /******************************************************************************* VOICE
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
// pas de reconnaissance, on désactive le déclencheur
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        } */

        soundView = (SoundView) findViewById(R.id.soundView);
        //soundView.setOnClickListener(this);
        soundView.setOnTouchListener(new MyTouchListener());

        positionDuck = new Point();

        /*
        positionDuck = soundView.getRandomPolygon();
        positionDuck.x = (positionDuck.x*55)/soundView.getWRectangle();
        positionDuck.y = (positionDuck.y*85)/soundView.getHRectangle();

        imgDuck.setScaleX((float)(soundView.getWRectangle()/(2*212)));
        imgDuck.setScaleY((float)(soundView.getHRectangle()/(2.5*237)));
        imgDuck.setX(positionDuck.x);//(positionDuck.x/width);
        imgDuck.setY(positionDuck.y);//(positionDuck.y/height);
        //imgDuck.setX(200);//(positionDuck.x/width);
        //imgDuck.setY(200);//(positionDuck.y/height);
        //imgDuck.setX(positionDuck.x-(55+55/2));//(positionDuck.x/width);
        //imgDuck.setY(positionDuck.y-(75));//(positionDuck.y/height); ---- rentre parfaitement PK???
        Log.e("dimension", positionDuck.x/width + " " + positionDuck.y/height);
        Log.e("scale ", soundView.getWRectangle() + " " + imgDuck.getWidth());
        Log.e("largeur", soundView.getWRectangle() + " " + soundView.getHRectangle());
        */
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

    /*private void changeMainContent(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment
        transaction.replace(R.id.gameContent, fragment);

        // Commit the transaction
        transaction.commit();
    }*/


    public void onClick(View v) {
        Log.e("position duck", positionDuck.x + " " + positionDuck.y);
        if(positionDuck.x==-1 && positionDuck.y==-1) { // TESTE et OK !!!
            Log.e("largeur", soundView.getHRectangle() + " " + soundView.getWRectangle());
            positionDuck = soundView.getRandomPolygon();
            imgDuck.setScaleX((float) (soundView.getWRectangle()/212));
            imgDuck.setScaleY((float) (soundView.getHRectangle()/(237*1.1)));
            imgDuck.setX(positionDuck.x-soundView.getWRectangle());//(positionDuck.x/width);
            imgDuck.setY(positionDuck.y-(soundView.getHRectangle()*(float)0.85));//(positionDuck.y/height);
            Log.e("position duck", positionDuck.x + " " + positionDuck.y);
        }

        Point result = null;
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
        } else if(v.getId() == R.id.buttonHaut){
            result = soundView.moveSelection("haut");
        }else if(v.getId() == R.id.buttonBas){
            result = soundView.moveSelection("bas");
        }else if(v.getId() == R.id.buttonDroit){
            result = soundView.moveSelection("droite");
        }else if(v.getId() == R.id.buttonGauche){
            result = soundView.moveSelection("gauche");
        }
        else{
            Log.e("on click", v.getX() + " " + v.getY());
        }
        if (result !=null){
            Log.e("on click", result.x + " " + result.y);
            if((result.x == positionDuck.x) && (result.y == positionDuck.y)) {
                Log.e("WIN","WIN");
                Log.e("ici", "ici");
                imgDuck.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        Log.e("On ActivityResult", "On ActivityResult");
        Log.e("position duck", positionDuck.x + " " + positionDuck.y);
        if(positionDuck.x==-1 && positionDuck.y==-1) { // NON TESTE
            Log.e("largeur", soundView.getHRectangle() + " " + soundView.getWRectangle());
            positionDuck = soundView.getRandomPolygon();
            imgDuck.setScaleX((float) (soundView.getWRectangle()/212));
            imgDuck.setScaleY((float) (soundView.getHRectangle()/(237*1.1)));
            imgDuck.setX(positionDuck.x-soundView.getWRectangle());//(positionDuck.x/width);
            imgDuck.setY(positionDuck.y-(soundView.getHRectangle()*(float)0.85));//(positionDuck.y/height);
            Log.e("position duck", positionDuck.x + " " + positionDuck.y);
        }
        // on vérifie que la réponse est bonne et pour nous
        // RESULT_OK est une constante de la classe Activity
        // ce code extrait était placé dans une Activity
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // on récupère les réponses
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(matches.size()>0) {
                for (int i = 0; i < matches.size(); i++) {
                    Log.e("matches", matches.get(i));
                    Point currentPoint = soundView.moveSelection(matches.get(i).toLowerCase());
                    if((currentPoint.x == positionDuck.x) && (currentPoint.y == positionDuck.y)) {
                        //WIN !!
                        //Code qui montre le canard
                        imgDuck.setVisibility(View.VISIBLE); //Non testé
                        Log.e("WIN","WIN");
                    }
                }
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
    }

    private void playSound(int resId, float volume) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
        mPlayer = MediaPlayer.create(this, resId);
        mPlayer.setVolume(0, volume);
        mPlayer.start();
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.e("position duck", positionDuck.x + " " + positionDuck.y);
            if(positionDuck.x==-1 && positionDuck.y==-1) { // NON TESTE
                Log.e("largeur", soundView.getHRectangle() + " " + soundView.getWRectangle());
                positionDuck = soundView.getRandomPolygon();
                imgDuck.setScaleX((float) (soundView.getWRectangle()/212));
                imgDuck.setScaleY((float) (soundView.getHRectangle()/(237*1.1)));
                imgDuck.setX(positionDuck.x-soundView.getWRectangle());//(positionDuck.x/width);
                imgDuck.setY(positionDuck.y-(soundView.getHRectangle()*(float)0.85));//(positionDuck.y/height);
                Log.e("position duck", positionDuck.x + " " + positionDuck.y);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                float mousePositionX = motionEvent.getX();
                float mousePositionY = motionEvent.getY();
                double distance = Math.sqrt(Math.pow(mousePositionX - positionDuck.x, 2) + Math.pow(mousePositionY - positionDuck.y, 2));
                //using any where`
                Log.e("positionTouch", mousePositionX + " " + mousePositionY);
                float volume1 = (float)(1 - distance/1000);
                Log.e("Volume", distance + " " + volume1);
                /*ToneGenerator toneG = new ToneGenerator(3,volume);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); //200 is duration in ms*/

                playSound(R.raw.bip, volume1);
                if((mousePositionX== positionDuck.x) && (mousePositionY == positionDuck.y)) {
                    Log.e("WIN","WIN");
                    Log.e("ici", "ici");
                    imgDuck.setVisibility(View.VISIBLE); //Non testé

                }
                return true;
            }
            return false;
        }
    }


}
