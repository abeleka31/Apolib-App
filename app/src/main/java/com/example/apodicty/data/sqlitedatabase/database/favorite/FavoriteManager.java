package com.example.apodicty.data.sqlitedatabase.database.favorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException; // Tambahkan ini
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.apodicty.data.sqlitedatabase.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {

    private static final String TAG = "FavoriteManager";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public FavoriteManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException { // Tambahkan 'throws SQLException'
        // Hanya buka database jika belum dibuka atau null
        if (database == null || !database.isOpen()) {
            try {
                database = dbHelper.getWritableDatabase();
                Log.d(TAG, "Database opened.");
            } catch (SQLException e) { // Tangkap SQLException
                Log.e(TAG, "Error opening database: " + e.getMessage());
                database = null; // Set database ke null jika gagal dibuka
                throw e; // Lemparkan kembali exception agar ditangani pemanggil
            }
        }
    }

    public void close() {
        if (database != null && database.isOpen()) {
            dbHelper.close(); // dbHelper.close() akan menutup database
            database = null; // Set database ke null setelah ditutup
            Log.d(TAG, "Database closed.");
        }
    }

    // --- METODE BARU UNTUK MENGHITUNG FAVORIT BERDASARKAN EMAIL PENGGUNA ---
    public int getFavoriteCountByUser(String email) {
        int count = 0;
        Cursor cursor = null;
        try {
            // Karena `open()` dan `close()` dikelola di Fragment/Activity,
            // kita hanya perlu memastikan database terbuka di sini.
            // Tidak perlu panggil open() atau close() lagi di dalam metode ini.
            if (!isDbOpen()) {
                Log.e(TAG, "Database not open for getFavoriteCountByUser. Email: " + email);
                return 0; // Kembalikan 0 jika database tidak terbuka
            }

            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_FAVORIT +
                    " WHERE " + DatabaseHelper.COL_EMAIL_FK + " = ?";
            cursor = database.rawQuery(query, new String[]{email});

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0); // COUNT(*) selalu di kolom pertama (indeks 0)
            }
            Log.d(TAG, "getFavoriteCountByUser: Email=" + email + ", Count=" + count);
        } catch (Exception e) { // Tangkap Exception umum untuk log semua error
            Log.e(TAG, "Error getting favorite count for email " + email + ": " + e.getMessage(), e); // Log stack trace
            count = 0; // Kembalikan 0 jika ada error
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
    // ----------------------------------------------------------------------

    // --- METODE UNTUK MEMASTIKAN STATUS DB (PENTING untuk semua metode CRUD) ---
    public boolean isDbOpen() {
        return database != null && database.isOpen();
    }
    // ---------------------------------------------------------------------------


    public boolean addFavorite(Favorite favorite) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database not open for addFavorite. Email: " + favorite.getEmailFk());
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ID_OBAT, favorite.getIdObat());
        values.put(DatabaseHelper.COL_EMAIL_FK, favorite.getEmailFk());
        values.put(DatabaseHelper.COL_PRODUCTTYPE, favorite.getProductType());
        values.put(DatabaseHelper.COL_GENERICNAME, favorite.getGenericName());

        values.put(DatabaseHelper.COL_BRANDNAME, favorite.getBrandName());
        values.put(DatabaseHelper.COL_EFFECTIVETIME, favorite.getEffectiveTime());
        values.put(DatabaseHelper.COL_VERSION, favorite.getVersion());
        values.put(DatabaseHelper.COL_SETID, favorite.getSetId());
        values.put(DatabaseHelper.COL_MANUFACTURERNAME, favorite.getManufacturerName());
        values.put(DatabaseHelper.COL_ROUTE, favorite.getRoute());
        values.put(DatabaseHelper.COL_RXCUI, favorite.getRxcui());
        values.put(DatabaseHelper.COL_UNII, favorite.getUnii());
        values.put(DatabaseHelper.COL_PHARMCLAS_EPC, favorite.getPharmClassEpc());
        values.put(DatabaseHelper.COL_PHARMCLAS_MOA, favorite.getPharmClassMoa());
        values.put(DatabaseHelper.COL_PRODUCTNDC, favorite.getProductNdc());
        values.put(DatabaseHelper.COL_SPLSETID, favorite.getSplSetId());

        values.put(DatabaseHelper.COL_ACTIVEINGREDIENT, favorite.getActiveIngredient());
        values.put(DatabaseHelper.COL_INACTIVEINGREDIENT, favorite.getInactiveIngredient());
        values.put(DatabaseHelper.COL_DESCRIPTION, favorite.getDescription());
        values.put(DatabaseHelper.COL_INDICATIONSANDUSAGE, favorite.getIndicationsAndUsage());
        values.put(DatabaseHelper.COL_DOSAGEANDADMINISTRATION, favorite.getDosageAndAdministration());
        values.put(DatabaseHelper.COL_CONTRAINDICATIONS, favorite.getContraindications());
        values.put(DatabaseHelper.COL_WARNINGS, favorite.getWarnings());
        values.put(DatabaseHelper.COL_WARNINGSANDCAUTIONS, favorite.getWarningsAndCautions());
        values.put(DatabaseHelper.COL_BOXEDWARNING, favorite.getBoxedWarning());
        values.put(DatabaseHelper.COL_ADVERSEREACTIONS, favorite.getAdverseReactions());
        values.put(DatabaseHelper.COL_DRUGINTERACTIONS, favorite.getDrugInteractions());
        values.put(DatabaseHelper.COL_CLINICALPHARMACOLOGY, favorite.getClinicalPharmacology());
        values.put(DatabaseHelper.COL_PHARMACODYNAMICS, favorite.getPharmacodynamics());
        values.put(DatabaseHelper.COL_PHARMACOKINETICS, favorite.getPharmacokinetics());
        values.put(DatabaseHelper.COL_MECHANISMOFACTION, favorite.getMechanismOfAction());
        values.put(DatabaseHelper.COL_USEINSPECIFICPOPULATIONS, favorite.getUseInSpecificPopulations());
        values.put(DatabaseHelper.COL_PREGNANCY, favorite.getPregnancy());
        values.put(DatabaseHelper.COL_NURSINGMOTHERS, favorite.getNursingMothers());
        values.put(DatabaseHelper.COL_PEDIATRICUSE, favorite.getPediatricUse());
        values.put(DatabaseHelper.COL_GERIATRICUSE, favorite.getGeriatricUse());
        values.put(DatabaseHelper.COL_OVERDOSAGE, favorite.getOverdosage());
        values.put(DatabaseHelper.COL_HOWSUPPLIED, favorite.getHowSupplied());
        values.put(DatabaseHelper.COL_STORAGEANDHANDLING, favorite.getStorageAndHandling());
        values.put(DatabaseHelper.COL_PATIENTMEDICATIONINFORMATION, favorite.getPatientMedicationInformation());
        values.put(DatabaseHelper.COL_SPLMEDGUIDE, favorite.getSplMedguide());
        values.put(DatabaseHelper.COL_SPLPATIENTPACKAGEINSERT, favorite.getSplPatientPackageInsert());
        values.put(DatabaseHelper.COL_DRUG_REFERENCES, favorite.getDrugReferences());
        values.put(DatabaseHelper.COL_QUESTIONS, favorite.getQuestions());

        values.put(DatabaseHelper.COL_INFORMATIONFORPATIENTS, favorite.getInformationForPatients());
        values.put(DatabaseHelper.COL_ASKDOCTORORPHARMACIST, favorite.getAskDoctorOrPharmacist());
        values.put(DatabaseHelper.COL_SAFEHANDLINGWARNING, favorite.getSafeHandlingWarning());
        values.put(DatabaseHelper.COL_USERSAFETYWARNINGS, favorite.getUserSafetyWarnings());
        values.put(DatabaseHelper.COL_SPLUNCLASSIFIEDSECTION, favorite.getSplUnclassifiedSection());
        values.put(DatabaseHelper.COL_CONTROLLEDSUBSTANCE, favorite.getControlledSubstance());
        values.put(DatabaseHelper.COL_DRUGABUSEANDDEPENDENCE, favorite.getDrugAbuseAndDependence());
        values.put(DatabaseHelper.COL_LABORANDDELIVERY, favorite.getLaborAndDelivery());
        values.put(DatabaseHelper.COL_LABORATORYTESTS, favorite.getLaboratoryTests());
        values.put(DatabaseHelper.COL_NONCLINICALTOXICOLOGY, favorite.getNonclinicalToxicology());
        values.put(DatabaseHelper.COL_MICROBIOLOGY, favorite.getMicrobiology());
        values.put(DatabaseHelper.COL_CARCINOGENESISANDMUTAGENESISANDIMPAIRMENTOFFERTILITY, favorite.getCarcinogenesisAndMutagenesisAndImpairmentOfFertility());
        values.put(DatabaseHelper.COL_RECENTMAJORCHANGES, favorite.getRecentMajorChanges());
        values.put(DatabaseHelper.COL_RISKS, favorite.getRisks());
        values.put(DatabaseHelper.COL_STOPUSE, favorite.getStopUse());
        values.put(DatabaseHelper.COL_CREATED_AT, favorite.getCreatedAt());

        long result = database.insert(DatabaseHelper.TABLE_FAVORIT, null, values);
        Log.d(TAG, "addFavorite: result = " + result);
        return result != -1;
    }

    public List<Favorite> getFavoritesByUser(String email, @Nullable String searchQuery, @Nullable String sortBy, @Nullable String sortOrder) {
        List<Favorite> favoriteList = new ArrayList<>();
        if (!isDbOpen()) {
            Log.e(TAG, "Database not open for getFavoritesByUser. Email: " + email);
            return favoriteList;
        }

        Cursor cursor = null;
        StringBuilder selection = new StringBuilder(DatabaseHelper.COL_EMAIL_FK + " = ?");
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(email);

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String likeQuery = "%" + searchQuery.trim() + "%";
            selection.append(" AND (")
                    .append(DatabaseHelper.COL_GENERICNAME).append(" LIKE ? COLLATE NOCASE OR ")
                    .append(DatabaseHelper.COL_BRANDNAME).append(" LIKE ? COLLATE NOCASE)");
            selectionArgs.add(likeQuery);
            selectionArgs.add(likeQuery);
        }

        String orderBy = null;
        if (sortBy != null && !sortBy.isEmpty()) {
            orderBy = sortBy;
            if (sortOrder != null && (sortOrder.equalsIgnoreCase("ASC") || sortOrder.equalsIgnoreCase("DESC"))) {
                orderBy += " " + sortOrder;
            } else {
                orderBy += " DESC";
            }
        } else {
            orderBy = DatabaseHelper.COL_CREATED_AT + " DESC";
        }

        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_FAVORIT,
                    null,
                    selection.toString(),
                    selectionArgs.toArray(new String[0]),
                    null,
                    null,
                    orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    favoriteList.add(parseFavoriteFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "getFavoritesByUser: Email=" + email + ", Found=" + favoriteList.size() + " favorites.");
        } catch (Exception e) {
            Log.e(TAG, "Error getting favorites by user with search/sort: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return favoriteList;
    }

    public int deleteFavorite(int idFavorite) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database not open for deleteFavorite by ID.");
            return 0;
        }
        String whereClause = DatabaseHelper.COL_ID_FAVORITE + " = ?";
        String[] whereArgs = { String.valueOf(idFavorite) };
        int rowsAffected = database.delete(DatabaseHelper.TABLE_FAVORIT, whereClause, whereArgs);
        Log.d(TAG, "deleteFavorite: rows affected = " + rowsAffected);
        return rowsAffected;
    }

    public int deleteFavoriteByIdObatAndEmail(String idObat, String userEmail) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database not open for deleteFavoriteByIdObatAndEmail.");
            return 0;
        }
        String whereClause = DatabaseHelper.COL_ID_OBAT + " = ? AND " + DatabaseHelper.COL_EMAIL_FK + " = ?";
        String[] whereArgs = { idObat, userEmail };
        int rowsAffected = database.delete(DatabaseHelper.TABLE_FAVORIT, whereClause, whereArgs);
        Log.d(TAG, "deleteFavoriteByIdObatAndEmail: rows affected = " + rowsAffected);
        return rowsAffected;
    }

    public boolean isDrugFavorited(String idObat, String userEmail) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database not open for isDrugFavorited.");
            return false;
        }
        Cursor cursor = null;
        boolean isFavorited = false;
        try {
            String selection = DatabaseHelper.COL_ID_OBAT + " = ? AND " + DatabaseHelper.COL_EMAIL_FK + " = ?";
            String[] selectionArgs = { idObat, userEmail };

            cursor = database.query(
                    DatabaseHelper.TABLE_FAVORIT,
                    new String[]{DatabaseHelper.COL_ID_FAVORITE},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            isFavorited = cursor != null && cursor.getCount() > 0;
            Log.d(TAG, "isDrugFavorited: Drug " + idObat + " for " + userEmail + " is favorited: " + isFavorited);
        } catch (Exception e) {
            Log.e(TAG, "Error checking favorite status: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isFavorited;
    }

    public Favorite getFavoriteByIdObatAndEmail(String idObat, String userEmail) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database not open for getFavoriteByIdObatAndEmail.");
            return null;
        }
        Cursor cursor = null;
        Favorite favorite = null;

        String[] columns = {
                DatabaseHelper.COL_ID_FAVORITE, DatabaseHelper.COL_ID_OBAT, DatabaseHelper.COL_EMAIL_FK,
                DatabaseHelper.COL_PRODUCTTYPE, DatabaseHelper.COL_GENERICNAME,
                DatabaseHelper.COL_BRANDNAME, DatabaseHelper.COL_EFFECTIVETIME,
                DatabaseHelper.COL_VERSION, DatabaseHelper.COL_SETID,
                DatabaseHelper.COL_MANUFACTURERNAME, DatabaseHelper.COL_ROUTE,
                DatabaseHelper.COL_RXCUI, DatabaseHelper.COL_UNII,
                DatabaseHelper.COL_PHARMCLAS_EPC, DatabaseHelper.COL_PHARMCLAS_MOA,
                DatabaseHelper.COL_PRODUCTNDC, DatabaseHelper.COL_SPLSETID,
                DatabaseHelper.COL_ACTIVEINGREDIENT, DatabaseHelper.COL_INACTIVEINGREDIENT,
                DatabaseHelper.COL_DESCRIPTION, DatabaseHelper.COL_INDICATIONSANDUSAGE,
                DatabaseHelper.COL_DOSAGEANDADMINISTRATION, DatabaseHelper.COL_CONTRAINDICATIONS,
                DatabaseHelper.COL_WARNINGS, DatabaseHelper.COL_WARNINGSANDCAUTIONS,
                DatabaseHelper.COL_BOXEDWARNING, DatabaseHelper.COL_ADVERSEREACTIONS,
                DatabaseHelper.COL_DRUGINTERACTIONS, DatabaseHelper.COL_CLINICALPHARMACOLOGY,
                DatabaseHelper.COL_PHARMACODYNAMICS, DatabaseHelper.COL_PHARMACOKINETICS,
                DatabaseHelper.COL_MECHANISMOFACTION, DatabaseHelper.COL_USEINSPECIFICPOPULATIONS,
                DatabaseHelper.COL_PREGNANCY, DatabaseHelper.COL_NURSINGMOTHERS,
                DatabaseHelper.COL_PEDIATRICUSE, DatabaseHelper.COL_GERIATRICUSE,
                DatabaseHelper.COL_OVERDOSAGE, DatabaseHelper.COL_HOWSUPPLIED,
                DatabaseHelper.COL_STORAGEANDHANDLING, DatabaseHelper.COL_PATIENTMEDICATIONINFORMATION,
                DatabaseHelper.COL_SPLMEDGUIDE, DatabaseHelper.COL_SPLPATIENTPACKAGEINSERT,
                DatabaseHelper.COL_DRUG_REFERENCES, DatabaseHelper.COL_QUESTIONS,
                DatabaseHelper.COL_INFORMATIONFORPATIENTS, DatabaseHelper.COL_ASKDOCTORORPHARMACIST,
                DatabaseHelper.COL_SAFEHANDLINGWARNING, DatabaseHelper.COL_USERSAFETYWARNINGS,
                DatabaseHelper.COL_SPLUNCLASSIFIEDSECTION, DatabaseHelper.COL_CONTROLLEDSUBSTANCE,
                DatabaseHelper.COL_DRUGABUSEANDDEPENDENCE, DatabaseHelper.COL_LABORANDDELIVERY,
                DatabaseHelper.COL_LABORATORYTESTS, DatabaseHelper.COL_NONCLINICALTOXICOLOGY,
                DatabaseHelper.COL_MICROBIOLOGY, DatabaseHelper.COL_CARCINOGENESISANDMUTAGENESISANDIMPAIRMENTOFFERTILITY,
                DatabaseHelper.COL_RECENTMAJORCHANGES, DatabaseHelper.COL_RISKS,
                DatabaseHelper.COL_STOPUSE, DatabaseHelper.COL_CREATED_AT
        };

        try {
            String selection = DatabaseHelper.COL_ID_OBAT + " = ? AND " + DatabaseHelper.COL_EMAIL_FK + " = ?";
            String[] selectionArgs = { idObat, userEmail };

            cursor = database.query(
                    DatabaseHelper.TABLE_FAVORIT,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null,
                    "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                favorite = parseFavoriteFromCursor(cursor);
            }
            Log.d(TAG, "getFavoriteByIdObatAndEmail: Drug " + idObat + " for " + userEmail + " found: " + (favorite != null));
        } catch (Exception e) {
            Log.e(TAG, "Error getting single favorite by ID and email: " + e.getMessage(), e);
            favorite = null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return favorite;
    }

    private Favorite parseFavoriteFromCursor(Cursor cursor) {
        Favorite favorite = new Favorite();
        try {
            favorite.setIdFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_FAVORITE)));
            favorite.setIdObat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_OBAT)));
            favorite.setEmailFk(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL_FK)));
            favorite.setProductType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCTTYPE)));
            favorite.setGenericName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENERICNAME)));

            favorite.setBrandName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BRANDNAME)));
            favorite.setEffectiveTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EFFECTIVETIME)));
            favorite.setVersion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VERSION)));
            favorite.setSetId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SETID)));
            favorite.setManufacturerName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MANUFACTURERNAME)));
            favorite.setRoute(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTE)));
            favorite.setRxcui(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RXCUI)));
            favorite.setUnii(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNII)));
            favorite.setPharmClassEpc(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHARMCLAS_EPC)));
            favorite.setPharmClassMoa(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHARMCLAS_MOA)));
            favorite.setProductNdc(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCTNDC)));
            favorite.setSplSetId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SPLSETID)));

            favorite.setActiveIngredient(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ACTIVEINGREDIENT)));
            favorite.setInactiveIngredient(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INACTIVEINGREDIENT)));
            favorite.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)));
            favorite.setIndicationsAndUsage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INDICATIONSANDUSAGE)));
            favorite.setDosageAndAdministration(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DOSAGEANDADMINISTRATION)));
            favorite.setContraindications(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTRAINDICATIONS)));
            favorite.setWarnings(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WARNINGS)));
            favorite.setWarningsAndCautions(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WARNINGSANDCAUTIONS)));
            favorite.setBoxedWarning(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOXEDWARNING)));
            favorite.setAdverseReactions(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADVERSEREACTIONS)));
            favorite.setDrugInteractions(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DRUGINTERACTIONS)));
            favorite.setClinicalPharmacology(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLINICALPHARMACOLOGY)));
            favorite.setPharmacodynamics(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHARMACODYNAMICS)));
            favorite.setPharmacokinetics(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHARMACOKINETICS)));
            favorite.setMechanismOfAction(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MECHANISMOFACTION)));
            favorite.setUseInSpecificPopulations(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USEINSPECIFICPOPULATIONS)));
            favorite.setPregnancy(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PREGNANCY)));
            favorite.setNursingMothers(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NURSINGMOTHERS)));
            favorite.setPediatricUse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PEDIATRICUSE)));
            favorite.setGeriatricUse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GERIATRICUSE)));
            favorite.setOverdosage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_OVERDOSAGE)));
            favorite.setHowSupplied(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HOWSUPPLIED)));
            favorite.setStorageAndHandling(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STORAGEANDHANDLING)));
            favorite.setPatientMedicationInformation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PATIENTMEDICATIONINFORMATION)));
            favorite.setSplMedguide(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SPLMEDGUIDE)));
            favorite.setSplPatientPackageInsert(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SPLPATIENTPACKAGEINSERT)));
            favorite.setDrugReferences(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DRUG_REFERENCES)));
            favorite.setQuestions(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUESTIONS)));
            favorite.setInformationForPatients(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INFORMATIONFORPATIENTS)));
            favorite.setAskDoctorOrPharmacist(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASKDOCTORORPHARMACIST)));
            favorite.setSafeHandlingWarning(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SAFEHANDLINGWARNING)));
            favorite.setUserSafetyWarnings(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERSAFETYWARNINGS)));
            favorite.setSplUnclassifiedSection(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SPLUNCLASSIFIEDSECTION)));
            favorite.setControlledSubstance(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTROLLEDSUBSTANCE)));
            favorite.setDrugAbuseAndDependence(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DRUGABUSEANDDEPENDENCE)));
            favorite.setLaborAndDelivery(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LABORANDDELIVERY)));
            favorite.setLaboratoryTests(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LABORATORYTESTS)));
            favorite.setNonclinicalToxicology(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NONCLINICALTOXICOLOGY)));
            favorite.setMicrobiology(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MICROBIOLOGY)));
            favorite.setCarcinogenesisAndMutagenesisAndImpairmentOfFertility(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CARCINOGENESISANDMUTAGENESISANDIMPAIRMENTOFFERTILITY)));
            favorite.setRecentMajorChanges(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECENTMAJORCHANGES)));
            favorite.setRisks(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RISKS)));
            favorite.setStopUse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STOPUSE)));
            favorite.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT)));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Column not found in cursor: " + e.getMessage());
            // Ini bisa terjadi jika ada kolom baru di Favorite.java tetapi belum ada di database
            // atau jika ada kesalahan penulisan nama kolom.
            // Anda mungkin perlu meningkatkan DATABASE_VERSION dan menangani onUpgrade.
        }
        return favorite;
    }
}