package com.example.apodicty.data.sqlitedatabase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ApodictyDB";
    private static final int DATABASE_VERSION = 5; // <<< NAIKKAN VERSI DATABASE LAGI!

    public static final String TABLE_USER = "users";
    public static final String TABLE_FAVORIT = "favorites";

    // User Table Columns (tidak berubah)
    public static final String COL_EMAIL = "email";
    public static final String COL_USERNAME = "username";
    public static final String COL_NAMA_LENGKAP = "nama_lengkap";
    public static final String COL_TGL_LAHIR = "tgl_lahir";
    public static final String COL_INSTANSI = "instansi";
    public static final String COL_QUOTES = "quotes";

    // Favorite Table Columns (TAMBAHAN BANYAK KOLOM DI SINI)
    public static final String COL_ID_FAVORITE = "id_favorite";
    public static final String COL_ID_OBAT = "id_obat"; // Sama seperti drug.getId()
    public static final String COL_EMAIL_FK = "email_fk";
    public static final String COL_PRODUCTTYPE = "product_type"; // Sama seperti openfda.getProductType()
    public static final String COL_GENERICNAME = "generic_name"; // Sama seperti openfda.getGenericName()

    // Kolom-kolom tambahan dari showDrugData
    public static final String COL_BRANDNAME = "brand_name"; // openfda.getBrandName()
    public static final String COL_EFFECTIVETIME = "effective_time"; // drug.getEffectiveTime()
    public static final String COL_VERSION = "version"; // drug.getVersion()
    public static final String COL_SETID = "set_id"; // drug.getSetId()
    public static final String COL_MANUFACTURERNAME = "manufacturer_name"; // openfda.getManufacturerName()
    public static final String COL_ROUTE = "route"; // openfda.getRoute()
    public static final String COL_RXCUI = "rxcui"; // openfda.getRxcui()
    public static final String COL_UNII = "unii"; // openfda.getUnii()
    public static final String COL_PHARMCLAS_EPC = "pharm_class_epc"; // openfda.getPharmClassEpc()
    public static final String COL_PHARMCLAS_MOA = "pharm_class_moa"; // openfda.getPharmClassMoa()
    public static final String COL_PRODUCTNDC = "product_ndc"; // openfda.getProductNdc()
    public static final String COL_SPLSETID = "spl_set_id"; // openfda.getSplSetId()

    // Informasi Detail Klinis (drug object langsung)
    public static final String COL_ACTIVEINGREDIENT = "active_ingredient";
    public static final String COL_INACTIVEINGREDIENT = "inactive_ingredient";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_INDICATIONSANDUSAGE = "indications_and_usage";
    public static final String COL_DOSAGEANDADMINISTRATION = "dosage_and_administration";
    public static final String COL_CONTRAINDICATIONS = "contraindications";
    public static final String COL_WARNINGS = "warnings";
    public static final String COL_WARNINGSANDCAUTIONS = "warnings_and_cautions";
    public static final String COL_BOXEDWARNING = "boxed_warning";
    public static final String COL_ADVERSEREACTIONS = "adverse_reactions";
    public static final String COL_DRUGINTERACTIONS = "drug_interactions";
    public static final String COL_CLINICALPHARMACOLOGY = "clinical_pharmacology";
    public static final String COL_PHARMACODYNAMICS = "pharmacodynamics";
    public static final String COL_PHARMACOKINETICS = "pharmacokinetics";
    public static final String COL_MECHANISMOFACTION = "mechanism_of_action";
    public static final String COL_USEINSPECIFICPOPULATIONS = "use_in_specific_populations";
    public static final String COL_PREGNANCY = "pregnancy";
    public static final String COL_NURSINGMOTHERS = "nursing_mothers";
    public static final String COL_PEDIATRICUSE = "pediatric_use";
    public static final String COL_GERIATRICUSE = "geriatric_use";
    public static final String COL_OVERDOSAGE = "overdosage";
    public static final String COL_HOWSUPPLIED = "how_supplied";
    public static final String COL_STORAGEANDHANDLING = "storage_and_handling";
    public static final String COL_PATIENTMEDICATIONINFORMATION = "patient_medication_information";
    public static final String COL_SPLMEDGUIDE = "spl_medguide";
    public static final String COL_SPLPATIENTPACKAGEINSERT = "spl_patient_package_insert";
    public static final String COL_DRUG_REFERENCES = "drug_references";
    public static final String COL_QUESTIONS = "questions";

    // Fitur Pendukung
    public static final String COL_INFORMATIONFORPATIENTS = "information_for_patients";
    public static final String COL_ASKDOCTORORPHARMACIST = "ask_doctor_or_pharmacist";
    public static final String COL_SAFEHANDLINGWARNING = "safe_handling_warning";
    public static final String COL_USERSAFETYWARNINGS = "user_safety_warnings";
    public static final String COL_SPLUNCLASSIFIEDSECTION = "spl_unclassified_section";
    public static final String COL_CONTROLLEDSUBSTANCE = "controlled_substance";
    public static final String COL_DRUGABUSEANDDEPENDENCE = "drug_abuse_and_dependence";
    public static final String COL_LABORANDDELIVERY = "labor_and_delivery";
    public static final String COL_LABORATORYTESTS = "laboratory_tests";
    public static final String COL_NONCLINICALTOXICOLOGY = "nonclinical_toxicology";
    public static final String COL_MICROBIOLOGY = "microbiology";
    public static final String COL_CARCINOGENESISANDMUTAGENESISANDIMPAIRMENTOFFERTILITY = "carcinogenesis_and_mutagenesis_and_impairment_of_fertility";
    public static final String COL_RECENTMAJORCHANGES = "recent_major_changes";
    public static final String COL_RISKS = "risks";
    public static final String COL_STOPUSE = "stop_use";
    public static final String COL_CREATED_AT = "created_at";


    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COL_EMAIL + " TEXT PRIMARY KEY, " + // <<< PERBAIKAN DI SINI
                    COL_USERNAME + " TEXT, " +
                    COL_NAMA_LENGKAP + " TEXT, " +
                    COL_TGL_LAHIR + " TEXT, " +
                    COL_INSTANSI + " TEXT, " +
                    COL_QUOTES + " TEXT" +
                    ");";

    private static final String CREATE_TABLE_FAVORITE =
            "CREATE TABLE " + TABLE_FAVORIT + " (" +
                    COL_ID_FAVORITE + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // <<< PERBAIKAN DI SINI
                    COL_ID_OBAT + " TEXT, " +
                    COL_EMAIL_FK + " TEXT, " +
                    COL_PRODUCTTYPE + " TEXT, " +
                    COL_GENERICNAME + " TEXT, " +
                    // Tambahan kolom-kolom baru
                    COL_BRANDNAME + " TEXT, " +
                    COL_EFFECTIVETIME + " TEXT, " +
                    COL_VERSION + " TEXT, " +
                    COL_SETID + " TEXT, " +
                    COL_MANUFACTURERNAME + " TEXT, " +
                    COL_ROUTE + " TEXT, " +
                    COL_RXCUI + " TEXT, " +
                    COL_UNII + " TEXT, " +
                    COL_PHARMCLAS_EPC + " TEXT, " +
                    COL_PHARMCLAS_MOA + " TEXT, " +
                    COL_PRODUCTNDC + " TEXT, " +
                    COL_SPLSETID + " TEXT, " +
                    COL_ACTIVEINGREDIENT + " TEXT, " +
                    COL_INACTIVEINGREDIENT + " TEXT, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_INDICATIONSANDUSAGE + " TEXT, " +
                    COL_DOSAGEANDADMINISTRATION + " TEXT, " +
                    COL_CONTRAINDICATIONS + " TEXT, " +
                    COL_WARNINGS + " TEXT, " +
                    COL_WARNINGSANDCAUTIONS + " TEXT, " +
                    COL_BOXEDWARNING + " TEXT, " +
                    COL_ADVERSEREACTIONS + " TEXT, " +
                    COL_DRUGINTERACTIONS + " TEXT, " +
                    COL_CLINICALPHARMACOLOGY + " TEXT, " +
                    COL_PHARMACODYNAMICS + " TEXT, " +
                    COL_PHARMACOKINETICS + " TEXT, " +
                    COL_MECHANISMOFACTION + " TEXT, " +
                    COL_USEINSPECIFICPOPULATIONS + " TEXT, " +
                    COL_PREGNANCY + " TEXT, " +
                    COL_NURSINGMOTHERS + " TEXT, " +
                    COL_PEDIATRICUSE + " TEXT, " +
                    COL_GERIATRICUSE + " TEXT, " +
                    COL_OVERDOSAGE + " TEXT, " +
                    COL_HOWSUPPLIED + " TEXT, " +
                    COL_STORAGEANDHANDLING + " TEXT, " +
                    COL_PATIENTMEDICATIONINFORMATION + " TEXT, " +
                    COL_SPLMEDGUIDE + " TEXT, " +
                    COL_SPLPATIENTPACKAGEINSERT + " TEXT, " +
                    COL_DRUG_REFERENCES + " TEXT, " +
                    COL_QUESTIONS + " TEXT, " +
                    COL_INFORMATIONFORPATIENTS + " TEXT, " +
                    COL_ASKDOCTORORPHARMACIST + " TEXT, " +
                    COL_SAFEHANDLINGWARNING + " TEXT, " +
                    COL_USERSAFETYWARNINGS + " TEXT, " +
                    COL_SPLUNCLASSIFIEDSECTION + " TEXT, " +
                    COL_CONTROLLEDSUBSTANCE + " TEXT, " +
                    COL_DRUGABUSEANDDEPENDENCE + " TEXT, " +
                    COL_LABORANDDELIVERY + " TEXT, " +
                    COL_LABORATORYTESTS + " TEXT, " +
                    COL_NONCLINICALTOXICOLOGY + " TEXT, " +
                    COL_MICROBIOLOGY + " TEXT, " +
                    COL_CARCINOGENESISANDMUTAGENESISANDIMPAIRMENTOFFERTILITY + " TEXT, " +
                    COL_RECENTMAJORCHANGES + " TEXT, " +
                    COL_RISKS + " TEXT, " +
                    COL_STOPUSE + " TEXT, " +
                    COL_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (" + COL_EMAIL_FK + ") REFERENCES " + TABLE_USER + "(" + COL_EMAIL + ")" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // PERINGATAN: Ini akan menghapus semua data!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}