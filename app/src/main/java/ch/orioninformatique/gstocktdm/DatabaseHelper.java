package ch.orioninformatique.gstocktdm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class  DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ScannerDatabase5.db";
    private static final String TABLE_INVENTAIRE = "inventaire";
    private static final String TABLE_COMMANDECLIENT = "commandecl";
    private static final String TABLE_COMMANDEFOURN = "commandefour";
    private static final String TABLE_PARAMETRES = "parametres";
    private static final String KEY_ID = "id";
    private static final String KEY_EAN = "ean";
    private static final String KEY_NUMERO = "numero";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_COMMANDE = "numcommande";
    private static final String KEY_QT = "qt";
    private static final String KEY_QTSTOCK = "qtstock";
    private static final String KEY_LIVRE = "qtlivre";
    private static final String KEY_NUMLIGNE = "numligne";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INVENTAIRE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_INVENTAIRE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EAN + " TEXT," + KEY_NUMERO + " TEXT," + KEY_DESIGNATION + " TEXT," + KEY_QT + " INTEGER," + KEY_QTSTOCK + " INTEGER" + " )";
        db.execSQL(CREATE_INVENTAIRE_TABLE);

        String CREATE_PARAMETRES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PARAMETRES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + "adresse TEXT," + "port INTEGER" + " )";
        db.execSQL(CREATE_PARAMETRES_TABLE);

        String CREATE_COMMANDECLIENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COMMANDECLIENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EAN + " TEXT," + KEY_NUMERO + " TEXT," + KEY_DESIGNATION + " TEXT," + KEY_QT + " INTEGER," + KEY_NUMLIGNE + " INTEGER," + KEY_LIVRE + " INTEGER," + KEY_COMMANDE + " TEXT" + " )";
        db.execSQL(CREATE_COMMANDECLIENT_TABLE);

        String CREATE_COMMANDEFOURN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COMMANDEFOURN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EAN + " TEXT," + KEY_NUMERO + " TEXT," + KEY_DESIGNATION + " TEXT," + KEY_QT + " INTEGER," + KEY_NUMLIGNE + " INTEGER," + KEY_LIVRE + " INTEGER," + KEY_COMMANDE + " TEXT" + " )";
        db.execSQL(CREATE_COMMANDEFOURN_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addInventaire(Inventaire inventaire) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EAN, inventaire.getEan());
        values.put(KEY_DESIGNATION, inventaire.getDesignation());
        values.put(KEY_NUMERO, inventaire.getNumero());
        values.put(KEY_QT, inventaire.getQt());
        values.put(KEY_QTSTOCK, inventaire.getQtstock());

        db.insert(TABLE_INVENTAIRE, null, values);
        db.close();

    }

    public boolean enregistreCommandesclientdetail(String numero, String ean) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db2 = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_COMMANDECLIENT, new String[]{KEY_ID, KEY_EAN, KEY_NUMERO, KEY_QT, KEY_LIVRE, KEY_DESIGNATION, KEY_NUMLIGNE, KEY_COMMANDE}, KEY_COMMANDE + " =? AND " + KEY_EAN + " =?",
                new String[]{numero, ean}, null, null, KEY_NUMLIGNE, null);

        if (cursor.getCount() == 0) {
            return false;
        } else {
            if (cursor.moveToFirst()) {
                do {

                    Commandes commandes = new Commandes(0, "", "", 0, 0, "", 0, "");
                    commandes.setId(cursor.getInt(0));
                    commandes.setEan(cursor.getString(1));
                    commandes.setNumero(cursor.getString(2));
                    commandes.setQt(cursor.getInt(3));
                    commandes.setLivre(cursor.getInt(4));
                    commandes.setDesignation(cursor.getString(5));
                    commandes.setNumligne(cursor.getInt(6));
                    commandes.setNumcommande(cursor.getString(7));
                    if (commandes.getQt() > commandes.getLivre()){
                        // enregsistre la livraison
                        ContentValues values = new ContentValues();
                        Integer livre = cursor.getInt(4);
                        livre = livre + 1;
                        values.put(KEY_LIVRE,livre);
                        db2.update(TABLE_COMMANDECLIENT,values,KEY_ID + "=?",
                                new String[]{String.valueOf(cursor.getInt(0))});

                        cursor.moveToLast();
                        return true;
                    }
                } while (cursor.moveToNext());

            }
            return false;
        }

    }


    public boolean enregistreCommandesfourndetail(String numero,String ean) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db2 = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_COMMANDEFOURN, new String[]{KEY_ID, KEY_EAN, KEY_NUMERO, KEY_QT, KEY_LIVRE, KEY_DESIGNATION, KEY_NUMLIGNE, KEY_COMMANDE}, KEY_COMMANDE + " =? AND "+KEY_EAN + " =?",
                new String[]{numero,ean}, null, null, KEY_NUMLIGNE, null);

        if (cursor.getCount() == 0) {
            return false;
        } else {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {

                        Commandes commandes = new Commandes(0, "", "", 0, 0, "", 0, "");
                        commandes.setId(cursor.getInt(0));
                        commandes.setEan(cursor.getString(1));
                        commandes.setNumero(cursor.getString(2));
                        commandes.setQt(cursor.getInt(3));
                        commandes.setLivre(cursor.getInt(4));
                        commandes.setDesignation(cursor.getString(5));
                        commandes.setNumligne(cursor.getInt(6));
                        commandes.setNumcommande(cursor.getString(7));
                        if (commandes.getQt() > commandes.getLivre()){
                            // enregsistre la livraison
                            ContentValues values = new ContentValues();
                            Integer livre = cursor.getInt(4);
                            livre = livre + 1;
                            values.put(KEY_LIVRE,livre);
                            db2.update(TABLE_COMMANDEFOURN,values,KEY_ID + "=?",
                                    new String[]{String.valueOf(cursor.getInt(0))});

                            cursor.moveToLast();
                            return true;
                        }
                    } while (cursor.moveToNext());
                    return false;
                }
            }
            return false;

        }
    }

    public List<Commandes> getCommandesclientdetail(String numero){
        List<Commandes> commandeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMANDECLIENT, new String[]{KEY_ID,KEY_EAN,KEY_NUMERO,KEY_QT,KEY_LIVRE,KEY_DESIGNATION,KEY_NUMLIGNE,KEY_COMMANDE},KEY_COMMANDE + " =?",
                new String[]{numero},null,null,KEY_NUMLIGNE,null);

        if (cursor.getCount() == 0) {

            return null;

        }
        else {
            if (cursor.moveToFirst()) {
                do {
                    Commandes commandes = new Commandes(0, "", "", 0, 0, "", 0, "");
                    commandes.setId(cursor.getInt(0));
                    commandes.setEan(cursor.getString(1));
                    commandes.setNumero(cursor.getString(2));
                    commandes.setQt(cursor.getInt(3));
                    commandes.setLivre(cursor.getInt(4));
                    commandes.setDesignation(cursor.getString(5));
                    commandes.setNumligne(cursor.getInt(6));
                    commandes.setNumcommande(cursor.getString(7));
                    commandeList.add(commandes);
                } while (cursor.moveToNext());
            };
            return commandeList;
        }
    }

    public List<Commandes> getCommandesfourndetail(String numero){
        List<Commandes> commandeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMANDEFOURN, new String[]{KEY_ID,KEY_EAN,KEY_NUMERO,KEY_QT,KEY_LIVRE,KEY_DESIGNATION,KEY_NUMLIGNE,KEY_COMMANDE},KEY_COMMANDE + " =?",
                new String[]{numero},null,null,KEY_NUMLIGNE,null);

        if (cursor.getCount() == 0) {

            return null;

        }
        else {
            if (cursor.moveToFirst()) {
                do {
                    Commandes commandes = new Commandes(0, "", "", 0, 0, "", 0, "");
                    commandes.setId(cursor.getInt(0));
                    commandes.setEan(cursor.getString(1));
                    commandes.setNumero(cursor.getString(2));
                    commandes.setQt(cursor.getInt(3));
                    commandes.setLivre(cursor.getInt(4));
                    commandes.setDesignation(cursor.getString(5));
                    commandes.setNumligne(cursor.getInt(6));
                    commandes.setNumcommande(cursor.getString(7));
                    commandeList.add(commandes);
                } while (cursor.moveToNext());
            };
            return commandeList;
        }
    }

    public Commandes getCommandesClient(String numligne, String numero){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMANDECLIENT, new String[]{KEY_ID,KEY_EAN,KEY_NUMERO,KEY_QT,KEY_LIVRE,KEY_DESIGNATION,KEY_NUMLIGNE,KEY_COMMANDE},KEY_COMMANDE + " =? AND "+KEY_NUMLIGNE + " =?",
                new String[]{numero,numligne},null,null,null,null);

        if (cursor.getCount() == 0) {

            return null;

        }
        else {
            cursor.moveToFirst();
            Commandes commandes = new Commandes(0,"","",0,0,"",0,"");
            commandes.setId(cursor.getInt(0));
            commandes.setEan(cursor.getString(1));
            commandes.setNumero(cursor.getString(2));
            commandes.setQt(cursor.getInt(3));
            commandes.setLivre(cursor.getInt(4));
            commandes.setDesignation(cursor.getString(5));
            commandes.setNumligne(cursor.getInt(6));
            commandes.setNumcommande(cursor.getString(7));

            return commandes;
        }
    }

    public Commandes getCommandesFourn(String numligne, String numero){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMANDEFOURN, new String[]{KEY_ID,KEY_EAN,KEY_NUMERO,KEY_QT,KEY_LIVRE,KEY_DESIGNATION,KEY_NUMLIGNE,KEY_COMMANDE},KEY_COMMANDE + " =? AND "+KEY_NUMLIGNE + " =?",
                new String[]{numero,numligne},null,null,null,null);

        if (cursor.getCount() == 0) {

            return null;

        }
        else {
            cursor.moveToFirst();
            Commandes commandes = new Commandes(0,"","",0,0,"",0,"");
            commandes.setId(cursor.getInt(0));
            commandes.setEan(cursor.getString(1));
            commandes.setNumero(cursor.getString(2));
            commandes.setQt(cursor.getInt(3));
            commandes.setLivre(cursor.getInt(4));
            commandes.setDesignation(cursor.getString(5));
            commandes.setNumligne(cursor.getInt(6));
            commandes.setNumcommande(cursor.getString(7));
            return commandes;
        }
    }

    public void addCommandeClient(Commandes commandes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EAN,commandes.getEan());
        values.put(KEY_NUMERO,commandes.getNumero());
        values.put(KEY_QT,commandes.getQt());
        values.put(KEY_LIVRE,commandes.getLivre());
        values.put(KEY_NUMLIGNE,commandes.getNumligne());
        values.put(KEY_DESIGNATION,commandes.getDesignation());
        values.put(KEY_COMMANDE,commandes.getNumcommande());

        db.insert(TABLE_COMMANDECLIENT,null,values);
        db.close();

    }

    public void addCommandeFourn(Commandes commandes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EAN,commandes.getEan());
        values.put(KEY_NUMERO,commandes.getNumero());
        values.put(KEY_QT,commandes.getQt());
        values.put(KEY_LIVRE,commandes.getLivre());
        values.put(KEY_NUMLIGNE,commandes.getNumligne());
        values.put(KEY_DESIGNATION,commandes.getDesignation());
        values.put(KEY_COMMANDE,commandes.getNumcommande());

        db.insert(TABLE_COMMANDEFOURN,null,values);
        db.close();

    }

    public void addParametre(Parametres parametre){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("adresse",parametre.getAdresse());
        values.put("port",parametre.getPort());

        db.insert(TABLE_PARAMETRES,null,values);
        db.close();

    }


    public Parametres getParametre(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PARAMETRES, new String[]{"id", "adresse", "port"}, "id =?",
                new String[]{id.toString()}, null, null, null, null);

        if (cursor.getCount() == 0) {

            return null;

        } else {

            cursor.moveToFirst();
            Parametres parametre = new Parametres(0,"",0);
            parametre.setId(cursor.getInt(0));
            parametre.setAdresse(cursor.getString(1));
            parametre.setPort(cursor.getInt(2));
            return    parametre;
        }

    }

     public Inventaire getInventaire(String ean){
        SQLiteDatabase db = this.getReadableDatabase();

//        String selectQuery = "SELECT * FROM " + TABLE_INVENTAIRE;
//        Cursor cursor = db.rawQuery(selectQuery,null,null);

        Cursor cursor = db.query(TABLE_INVENTAIRE, new String[]{KEY_ID,KEY_EAN,KEY_NUMERO,KEY_DESIGNATION,KEY_QT,KEY_QTSTOCK},KEY_EAN + " =?",
                new String[]{ean},null,null,null,null);

        if (cursor.getCount() == 0) {

            return null;

        }
        else {
            cursor.moveToFirst();
            Inventaire inventaire = new Inventaire(0,"","","",0,0);
            inventaire.setId(cursor.getInt(0));
            inventaire.setEan(cursor.getString(1));
            inventaire.setNumero(cursor.getString(2));
            inventaire.setDesignation(cursor.getString(3));
            inventaire.setQt(cursor.getInt(4));
            inventaire.setQtstock(cursor.getInt(5));
            return inventaire;
        }
    }
    public List<Inventaire> getAllInventaire(){
        List<Inventaire> inventaireList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_INVENTAIRE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null,null);
        if (cursor.moveToFirst()){
            do  {
                Inventaire inventaire = new Inventaire(0,"","","",0,0);
                inventaire.setId(cursor.getInt(0));
                inventaire.setEan(cursor.getString(1));
                inventaire.setNumero(cursor.getString(2));
                inventaire.setDesignation(cursor.getString(3));
                inventaire.setQt(cursor.getInt(4));
                inventaire.setQtstock(cursor.getInt(5));
                inventaireList.add(inventaire);
            } while (cursor.moveToNext());
        }
        return inventaireList;
    }
    public int updateeancommandeclient(Commandes commande){
        SQLiteDatabase db = this.getWritableDatabase();
        if (commande.getEan() != null){
            ContentValues values = new ContentValues();
            values.put(KEY_EAN, commande.getEan());
            return db.update(TABLE_COMMANDECLIENT, values, KEY_ID + "=?",
                    new String[]{String.valueOf(commande.getId())});
        }
        return 0;

    }
    public int updateeancommandefourn(Commandes commande){
        SQLiteDatabase db = this.getWritableDatabase();
        if (commande.getEan() != null){
            ContentValues values = new ContentValues();
            values.put(KEY_EAN, commande.getEan());
            return db.update(TABLE_COMMANDEFOURN, values, KEY_ID + "=?",
                    new String[]{String.valueOf(commande.getId())});
        }
        return 0;

    }
    public int updateInventaire(Inventaire inventaire){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QT,inventaire.getQt());
        return db.update(TABLE_INVENTAIRE,values,KEY_ID + "=?",
                new String[]{String.valueOf(inventaire.getId())});

    }
    public int updateParametre(Parametres parametre){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("adresse",parametre.getAdresse());
        values.put("port",parametre.getPort());
        return db.update(TABLE_PARAMETRES,values,  "id =?",
                new String[]{String.valueOf(parametre.getId())});

    }
    public void deleteInventaire(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTAIRE,null,null);
        db.close();
    }
    public void deleteCommandeClient(String numcommande){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMMANDECLIENT, String.valueOf(KEY_COMMANDE == numcommande),null);
        db.close();
    }
    public void deleteCommandeFourn(String numcommande){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMMANDEFOURN, String.valueOf(KEY_COMMANDE == numcommande),null);
        db.close();
    }

    public int GetInventaireCount(){
        String countQuery = "SELECT id FROM "+TABLE_INVENTAIRE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        Integer qt = cursor.getCount();
        cursor.close();
        return qt;
    }
}
