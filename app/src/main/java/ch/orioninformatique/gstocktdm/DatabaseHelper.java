package ch.orioninformatique.gstocktdm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class  DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ScannerDatabase.db";
    private static final String TABLE_INVENTAIRE = "inventaire";
    private static final String TABLE_PARAMETRES = "parametres";
    private static final String KEY_ID = "id";
    private static final String KEY_EAN = "ean";
    private static final String KEY_NUMERO = "numero";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_QT = "qt";
    private static final String KEY_QTSTOCK = "qtstock";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INVENTAIRE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_INVENTAIRE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EAN + " TEXT," +KEY_NUMERO + " TEXT," + KEY_DESIGNATION + " TEXT," + KEY_QT + " INTEGER," + KEY_QTSTOCK + " INTEGER"+" )";
        db.execSQL(CREATE_INVENTAIRE_TABLE);

        String CREATE_PARAMETRES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PARAMETRES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + "adresse TEXT," + "port INTEGER"+" )";
        db.execSQL(CREATE_PARAMETRES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addInventaire(Inventaire inventaire){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EAN,inventaire.getEan());
        values.put(KEY_DESIGNATION,inventaire.getDesignation());
        values.put(KEY_NUMERO,inventaire.getNumero());
        values.put(KEY_QT,inventaire.getQt());
        values.put(KEY_QTSTOCK,inventaire.getQtstock());

        db.insert(TABLE_INVENTAIRE,null,values);
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
    public int GetInventaireCount(){
        String countQuery = "SELECT id FROM "+TABLE_INVENTAIRE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        Integer qt = cursor.getCount();
        cursor.close();
        return qt;
    }
}
