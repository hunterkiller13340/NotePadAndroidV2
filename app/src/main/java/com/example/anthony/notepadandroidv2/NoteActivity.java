package com.example.anthony.notepadandroidv2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    protected EditText inputTitre;
    protected EditText inputContenu;
    final private int REQUEST_CODE_ASK_PERMISSION = 123;

    Note oldNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);
        inputTitre = (EditText) findViewById(R.id.zoneTitre);
        inputContenu = (EditText) findViewById(R.id.zoneTexte);
        oldNote = (Note) getIntent().getSerializableExtra(MainActivity.EXTRA_MSG_NOTE);
        if (oldNote != null) {
            inputTitre.setText(oldNote.getTitre());
            inputContenu.setText(oldNote.getContenu());
        }
    }




    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case REQUEST_CODE_ASK_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    Geoloc.getCoordonates(getApplicationContext());
                }else{
                    //permission denied
                    Toast.makeText(this, "ACCES GPS DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void enregistrer(View view) {
        /**
         * verifier que l'on a bien rempli tout les champs
         *
         * ajouter la note dans la bd
         */
        String titre = inputTitre.getText().toString();
        String contenu = inputContenu.getText().toString();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date = format1.format(cal.getTime());

        if (!titre.equals("") && !contenu.equals("")) {
            //TODO recuper coordonnees gps

            String ville = Geoloc.getCoordonates(getApplicationContext()); //recuperer les coordonnées gps

            //fin recup coordonnées gps

            Note note = null;
            NoteDB noteDB = new NoteDB(getApplicationContext());

            //si on creer une note
            if(oldNote == null) {
                note = new Note(titre, contenu, date,ville);
                //on ajoute dans la base la nouvelle note
                noteDB.addNewNote(note);
            }
            //sinon si on edite une note existante
            else if(oldNote.getId() >= 0){
                note = new Note(oldNote.getId(), titre, contenu, date, ville);
                //on met a jour la note
                noteDB.updateNote(note);
            }

            //revenir a MainActivity

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Mauvaise saisie...", Toast.LENGTH_SHORT);
            toast.show();
        }
    }//enregistrer(View view)

    public void annuler(View view){
        /**
         * revenir a l'activite precedente
         */
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }//annuler(View view)
}
