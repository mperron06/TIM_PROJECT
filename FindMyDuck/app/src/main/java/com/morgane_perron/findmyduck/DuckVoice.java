package com.morgane_perron.findmyduck;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DuckVoice extends Fragment {
    private ImageView imgDuck;
    private int xPosition;
    private int yPosition;

    public DuckVoice() {
        // Required empty public constructor
    }

    public static DuckVoice newInstance() {
        return new DuckVoice();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_duck_voice, container, false);
        imgDuck = (ImageView) v.findViewById(R.id.imgDuck);
        //imgDuck.setVisibility(View.INVISIBLE);

        xPosition = (int)(Math.random() * 1000); //Trouver les bons paramètres
        yPosition = (int)(Math.random() * 1000);
        Log.e("position", xPosition + " " + yPosition);
        imgDuck.setX(xPosition);
        imgDuck.setY(yPosition);
        // Inflate the layout for this fragment
        return v;
    }


}
