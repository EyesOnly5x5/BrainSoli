package io.github.eyesonly5x5;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import de.eyesonly5x5.brainsoli.R;

public class Globals  extends ListActivity {
    @SuppressLint("StaticFieldLeak")
    private static final Globals instance = new Globals();
    private static MyDisplay metrics;

    // Beispiele für Daten...
    private byte[][] Tast = new byte[100][9];
    private int maxFelder = 0;
    private final boolean[] Flg = new boolean[100];
    private final int[][] Colors = new int[11][5];
    List<Integer> Color = new ArrayList<>();
    int[] BUTTON_IDS;
    int[] TEXT_IDS;
    private TextView Ausgabe;
    List<Button> buttons = new ArrayList<>();
    List<TextView> TextV = new ArrayList<>();
    private int Zuege = 0;
    private int Anzahl = 0;
    private int Activity=-1;
    private Context myContext;
    private Resources myRes;
    private boolean gewonnen = true;
    private SoundBib SoundW;
    private SoundBib SoundF;
    private final int Buty = 90;
    private int[][] NonoG;
    private boolean geloest;
    private boolean dashEnde;
    private int istGedrueckt = 0;
    private String woMischen = "Zauber";
    private boolean geMischt = false;

    // private Globals() { }

    public static Globals getInstance() {
        return instance;
    }

    public static void setMetrics( Resources hier ){
        metrics = new MyDisplay( hier );
    }
    public static MyDisplay getMetrics( ){
        return( metrics );
    }

    public int getMaxFelder() {
        return this.maxFelder;
    }

    public int getZuege() {
        return Zuege;
    }
    public int incZuege() { return ++Zuege; }

    public void setAusgabe(TextView wert) {
        Ausgabe = wert;
    }

    public SoundBib getSoundBib(boolean s) {
        return( (s)?SoundW:SoundF );
    }
    public void setSoundBib(boolean s, SoundBib wert) {
        if( s ) SoundW = wert;
        else SoundF = wert;
    }

    public boolean getGewonnen() {
        return gewonnen;
    }
    public void setGewonnen( boolean wert) {
        gewonnen = wert;
    }

    public boolean getDashEnde() {
        return dashEnde;
    }
    public void setActivity(int act){
        Activity = act;
    }
    public void setMyContext( MainActivity c) {
        myContext = c;
        myRes = myContext.getResources();
    }

    public void addButton(Button button) {
        buttons.add(button);
    }
    public void addText(TextView Text) { TextV.add(Text); }

    public void setNonoG( int vec, int pos, int wert ){ NonoG[vec][pos] = wert; }
    public int getNonoG( int vec, int pos ){ return( NonoG[vec][pos] ); }

    public void setWoMischen( String wert ){
        woMischen = wert;
        metrics.setWoMischen(wert);
    }
    public String getWoMischen( ){ return( woMischen ); }
    public List<Integer> getColor(){ return( Color ); }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    public void Mischer() {
        int id, id1, id2, tmp;
        Random r = new Random();
        Zuege = 0;
        gewonnen = false;
        Ausgabe.setText("Züge: " + Zuege);
        NonoG = new int[2][Anzahl*Anzahl];
        geMischt = true;
        for (id = 0; id < maxFelder; id++) {
            Button button = buttons.get(id);
            Flg[id] = true;
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.DarkGreen));
            button.setTextColor(button.getContext().getResources().getColor(R.color.white));
        }
        for (int i = 0; i < 25; i++) {
            id = r.nextInt(maxFelder);
            for (int idS : Tast[id]) if (idS > 0) changeFlg(idS - 1);
        }
    }

     public void DasIstEsSoli(){
        int anz = 0;
        geloest = false;
        for( int i = 0; i < NonoG[0].length; i++ ){
            if( NonoG[0][i] == 0 ) anz++;
            if( NonoG[0][i] >= 1 ) {
                int[] p = new int[]{ i+1, i-1, i+7, i-7 };
                for( int j = 0; j < p.length; j++ ) {
                    if (p[j] < 0) p[j] = 0;
                    if (p[j] > NonoG[0].length) p[j] = 0;
                    if ( NonoG[0][p[j]] == 1 ) {
                        geloest = true;
                    }
                }
            }
        }
        gewonnen = ( (NonoG[0][24] == 1) && (anz == 32) );

        dashEnde = (!gewonnen && !geloest) || gewonnen;
    }

    @SuppressLint("ResourceType")
    public void sortButtons(){
        // List<Button> but = new ArrayList<>();
        Button tmp;
        boolean tausch = true;
        while( tausch ){
            tausch = false;
            for( int i = 0; i<(buttons.size()-1); i++ ) {
                if( buttons.get(i).getId() > buttons.get((i+1)).getId() ) {
                    tmp = buttons.get(i);
                    buttons.set( i, buttons.get((i+1)) );
                    buttons.set( (i+1), tmp );
                    tausch = true;
                }
            }
        }
    }

    public void saveSolitar( ){
        String data = "";
        for( int i = 0; i<NonoG[0].length; i++ ){
            data += ""+NonoG[0][i]+",";
            data += ""+NonoG[1][i]+"\n";
        }
        speichern( "Solitar.txt", data );
    }

    public void loadSolitar(){
        String[] data;
        int zahl = 0;
        int[][] tmp = new int[2][Anzahl*Anzahl];

        data = laden( "Solitar.txt", "0,0" );
        for( int i = 0; i<data.length; i++ ) {
            String[] x = data[i].split(",");
            tmp[0][i] = Integer.parseInt( x[0] );
            if( tmp[0][i] == 0 ) zahl++;
            tmp[1][i] = Integer.parseInt( x[1] );
        }
        if( zahl < 32 ){
            for( int i = 0; i<(NonoG[0].length); i++ ){
                NonoG[0][i] = tmp[0][i];
                NonoG[1][i] = tmp[1][i];
            }
            Zuege = zahl-1;
        }
    }

    public void deleSolitar(){
        loeschen( "Solitar.txt" );
    }

// /data/user/0/de.eyesonly5x5.brainsoli/files/Solitar.txt
    private void speichern( String filename, String data ){
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = myContext.openFileOutput(filename, myContext.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loeschen( String filename ){
        File file = new File( myContext.getFilesDir(), filename );
        file.delete();
    }

    private String[] laden( String filename, String vorlage ){
        String[] ret = new String[Anzahl*Anzahl];
        int i;
        for( i = 0; i < ret.length; i++ ) ret[i] = vorlage;
        i = 0;
        try {
            File in = new File( myContext.getFilesDir(), filename );
            Scanner scanner = new Scanner(in);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                ret[i++] = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return( ret );
    }

    public void SolitarInit(){
        for( int i = 0; i<(Anzahl*Anzahl); i++ ){
            NonoG[0][i] = 1;
            switch( i ){
                case 0: case 1: case 5: case 6: case 7: case 8: case 12: case 13:
                case 35: case 36: case 40: case 41: case 42: case 43: case 47: case 48:
                    NonoG[0][i] = -1;
                    break;
                case 24:
                    NonoG[0][i] = 0;
            }
            NonoG[1][i] = 0;
        }
        Zuege = 0;
        istGedrueckt = 0;
    }

    public int istGedrueckt(){ return( istGedrueckt ); }
    public void istGedrueckt( int wert ){ istGedrueckt = wert; }

 /*
 0 1 2
 3 4 5
 6 7 8
 */

    @SuppressLint("WrongConstant")
    private int anzahlButtons(){
        int AnzBut = (((metrics.getMaxPixels()) / (int)(this.Buty*metrics.getFaktor()))-3);
        // int dimenX = (int) metrics.getMinPixels() / (column+1);
        if( AnzBut > 11 ) AnzBut = 11;
        AnzBut *= Anzahl;
        return( AnzBut );
    }

    public int[] getButtonIDs() {
        int wer = getWerWoWas();
        int Buttys = (wer==0)?9:(wer==1)?16:(wer==2)?25:(wer==3)?anzahlButtons():(wer==4)?100:(wer>=5)?Anzahl*Anzahl:0;
        int[] ret = new int[Buttys];

        if( wer < 3 ){
            for (int i = 0; i < ret.length; i++) {
                ret[i] = myContext.getResources().getIdentifier("b"+(i+1), "id", myContext.getPackageName());
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = (i + 1);
            }
            if( wer == 5 ) {
                NonoG = new int[2][Anzahl * Anzahl];
            }
        }
        BUTTON_IDS = ret;
        maxFelder = BUTTON_IDS.length;
        return (BUTTON_IDS);
    }

    public int[] getTextIDs() {
        int[] ret = new int[Anzahl*2];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = (300 + i);
        }
        TEXT_IDS = ret;
        return (TEXT_IDS);
    }

    @SuppressLint("NonConstantResourceId")
    private int getWerWoWas(){
        int ret = -1;
        switch( Activity ){
            case R.layout.activity_solitar:
                ret = 5;
                break;
        }
        return( ret );
    }

    public void setGameData( int anzahl ) {
        Zuege = 0;
        gewonnen = true;
        buttons = null;
        buttons = new ArrayList<>();
        TextV = null;
        TextV = new ArrayList<>();
        Anzahl = anzahl;
        istGedrueckt = 0;
        dashEnde = false;
    }

    @SuppressLint("ResourceAsColor")
    public void changeFlg(int id) {
        Button button = buttons.get(id);
        if (Flg[id]) {
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.DarkRed));
            button.setTextColor(button.getContext().getResources().getColor(R.color.Gelb));
        } else {
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.DarkGreen));
            button.setTextColor(button.getContext().getResources().getColor(R.color.white));
        }
        Flg[id] = !Flg[id];
    }

    static class SoundBib extends AppCompatActivity {
        private SoundPool soundPool;
        List<Integer> sound = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // setContentView(R.layout.activity_main);
        }

        public SoundBib(boolean s, Context context) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();

            if( s ) {
                sound.add(soundPool.load(context, R.raw.won1, 1));
                sound.add(soundPool.load(context, R.raw.won2, 1));
                sound.add(soundPool.load(context, R.raw.won3, 1));
                sound.add(soundPool.load(context, R.raw.won4, 1));
                sound.add(soundPool.load(context, R.raw.won5, 1));
            } else {
                sound.add(soundPool.load(context, R.raw.fail1, 1));
                sound.add(soundPool.load(context, R.raw.fail2, 1));
                sound.add(soundPool.load(context, R.raw.fail3, 1));
                sound.add(soundPool.load(context, R.raw.fail4, 1));
            }
        }

        // When users click on the button "Gun"
        public void playSound() {
            soundPool.autoPause();
            Random r = new Random();
            int id = r.nextInt(sound.size());
            soundPool.play(sound.get(id), 0.25F, 0.25F, 0, 0, 1);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            soundPool.release();
            soundPool = null;
        }
    }

    public void Anleitung( Context dasDA, int Wat ) {
        Dialog customDialog = new Dialog( dasDA );
        customDialog.setContentView(R.layout.anleitung);
        TextView oView = customDialog.findViewById( R.id.Anleitung );
        String str = myRes.getString( Wat, myRes.getString( R.string.Wunschliste ) );
        oView.setText( str );
        Button bView = customDialog.findViewById( R.id.Warte );
        bView.setOnClickListener(view -> customDialog.dismiss());
        customDialog.setCancelable(false);
        customDialog.show();
    }
}