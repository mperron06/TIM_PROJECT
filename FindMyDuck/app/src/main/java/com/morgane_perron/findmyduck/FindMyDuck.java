package com.morgane_perron.findmyduck;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FindMyDuck extends Activity implements View.OnClickListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private ImageView imgDuck;
    private Point positionDuck;
    private Button speakButton;
    private Button hautButton;
    private Button basButton;
    private Button droitButton;
    private Button gaucheButton;
    private SoundView soundView;
    private int width;
    private int height;
    private Button goButton;
    private MediaPlayer mPlayer = null;
    private int nbCarre;
    private long startTime;
    private Dialog dialog;
    private File mFile;

    private String data;
    private float varSetX, varSetY;

    private ArrayList<DataGamer>dataCollected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        varSetX=1;
        varSetY=1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_duck);

        Log.i(":P", Environment.getExternalStorageDirectory().getPath() + "/Android/data/dataCollected.txt");
        mFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/dataCollected.txt");

        nbCarre = 5;
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.begin_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Nb Carrés : " + nbCarre);

        goButton = (Button) dialog.findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateGame();
                dialog.dismiss();
                startTime = System.currentTimeMillis();;
            }
        });

        final SeekBar sk = (SeekBar) dialog.findViewById(R.id.seekBarNbCarre);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                nbCarre = progress+2;
                dialog.setTitle("Nb Carrés : " + String.valueOf(progress+2));
            }
        });
        dialog.show();

    }

    private void initiateGame(){
        //changeMainContent(DuckVoice.newInstance());
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        imgDuck = (ImageView) findViewById(R.id.imgDuck);
        imgDuck.setVisibility(View.INVISIBLE);
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
        /******************************************************************************* VOICE*/
         if (activities.size() != 0) {
         speakButton.setOnClickListener(this);
         } else {
         // pas de reconnaissance, on désactive le déclencheur
         speakButton.setEnabled(false);
         speakButton.setText("Recognizer not present");
         speakButton.setVisibility(View.GONE);
         }

        soundView = (SoundView) findViewById(R.id.soundView);
        //soundView.setOnClickListener(this);
        soundView.clear();
        soundView.setNb(nbCarre);
        soundView.generatePolygons();
        soundView.setOnTouchListener(new MyTouchListener());

        positionDuck = new Point();
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
        if(positionDuck.x==-1 && positionDuck.y==-1) {
            positionDuck = soundView.getRandomPolygon();
            imgDuck.setScaleX((float) (soundView.getWRectangle()/212));
            imgDuck.setScaleY((float) (soundView.getHRectangle()/(237*1.1)));
            getScaleXY();;
            imgDuck.setX(positionDuck.x-soundView.getWRectangle()*varSetX);//(positionDuck.x/width);
            imgDuck.setY(positionDuck.y-soundView.getHRectangle()*varSetY);//(positionDuck.y/height);
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
                onWin("Bouton");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        Log.e("On ActivityResult", "On ActivityResult");
        Log.e("position duck", positionDuck.x + " " + positionDuck.y);
        if(positionDuck.x==-1 && positionDuck.y==-1) {
            positionDuck = soundView.getRandomPolygon();
            imgDuck.setScaleX((float) (soundView.getWRectangle()/212));
            imgDuck.setScaleY((float) (soundView.getHRectangle()/(237*1.1)));
            getScaleXY();;
            imgDuck.setX(positionDuck.x-soundView.getWRectangle()*varSetX);//(positionDuck.x/width);
            imgDuck.setY(positionDuck.y-soundView.getHRectangle()*varSetY);//(positionDuck.y/height);
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
                        onWin("Voice");
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
        if (mPlayer != null && mPlayer.isPlaying()) {
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
            if(positionDuck.x==-1 && positionDuck.y==-1) {
                positionDuck = soundView.getRandomPolygon();
                imgDuck.setScaleX((float) (soundView.getWRectangle()/212));
                imgDuck.setScaleY((float) (soundView.getHRectangle()/(237*1.1)));
                getScaleXY();;
                imgDuck.setX(positionDuck.x-soundView.getWRectangle()*varSetX);//(positionDuck.x/width);
                imgDuck.setY(positionDuck.y-soundView.getHRectangle()*varSetY);//(positionDuck.y/height);
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                float mousePositionX = motionEvent.getX();
                float mousePositionY = motionEvent.getY();
                double distance = Math.sqrt(Math.pow(mousePositionX - positionDuck.x, 2) + Math.pow(mousePositionY - positionDuck.y, 2));
                //using any where`
                Point currentTouch = soundView.caseContainsPoint(new Point(mousePositionX, mousePositionY));
                Log.e("current touch", currentTouch.x+ " " + currentTouch.y);
                float volume1 = (float)(1 - distance/1000);
                Log.e("Volume", distance + " " + volume1);
                playSound(R.raw.bip, volume1);
                if((currentTouch.x == positionDuck.x) && (currentTouch.y == positionDuck.y)) {
                    onWin("Sound");
                }
                return true;
            }
            return false;
        }
    }

    private void onWin(String interaction) {
        float t = (float)(System.currentTimeMillis() - startTime)/1000;
        writeSettings(new DataGamer(interaction, t, nbCarre).toString());
        imgDuck.setVisibility(View.VISIBLE);
        dialog.setTitle("You win in : " + t + " seconds, try again :");
        dialog.show();
    }
    /* resoudre le positionnement du canard */
    public void writeSettings(String data){
        try {

            // Si le fichier est lisible et qu'on peut écrire dedans
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
                // On crée un nouveau fichier. Si le fichier existe déjà, il ne sera pas créé
                if(!mFile.exists())
                    mFile.createNewFile();
                FileOutputStream output = new FileOutputStream(mFile, true);
                output.write(data.getBytes());
                if(output != null)
                    output.close();
            }
           else{
                Log.e("NOP", "PROBLEME");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getScaleXY(){

        switch (nbCarre){
            case 2 :
                varSetX= (float) -0.20;
                varSetY= (float) -0.25;
                break;
            case 3 :
                varSetX= (float) -0.07;
                varSetY= (float) -0.1;
                break;
            case 4 :
                varSetX= (float) 0.12;
                varSetY= (float) 0.05;
                break;
            case 5 :
                varSetX= (float) 0.30;
                varSetY= (float) 0.17;
                break;
            case 6 :
                varSetX= (float) 0.45;
                varSetY= (float) 0.30;
                break;
            case 7 :
                varSetX= (float) 0.58;
                varSetY= (float) 0.43;
                break;
            case 8 :
                varSetX= (float) 0.72;
                varSetY= (float) 0.60;
                break;
            case 9 :
                varSetX= (float) 0.90;
                varSetY= (float) 0.75;
                break;
            case 10 :
                varSetX=1;
                varSetY= (float) 0.85 ;
                break;
            default:
                varSetX=1;
                varSetY= (float) 0.85;

        }
    }


}
