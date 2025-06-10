package com.example.apodicty.utils;

import android.text.TextUtils;
import android.util.Log;
import android.view.View; // Penting: import View
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apodicty.R;
import com.example.apodicty.data.networkApi.respon.DrugResponse;
import com.example.apodicty.data.sqlitedatabase.database.favorite.Favorite;
import com.example.apodicty.databinding.ActivityDetailDrugBinding;

import java.util.List;
import java.util.function.Supplier;

public class DrugDetailHelper {

    private ActivityDetailDrugBinding binding;

    public DrugDetailHelper(ActivityDetailDrugBinding binding) {
        this.binding = binding;
    }

    /**
     * Menetapkan teks ke TextView dan mengatur visibilitas LinearLayout induk.
     * Jika nilai adalah "-", TextView dan LinearLayout induknya akan GONE.
     *
     * @param textView   TextView tempat teks akan disetel.
     * @param parentView LinearLayout induk yang membungkus label dan nilai.
     * @param prefix     Prefiks teks (misal: "Produsen : ").
     * @param value      Nilai data yang akan ditampilkan (bisa String atau List<String>).
     * @param <T>        Tipe data nilai.
     */
    public <T> void setTextSafely(TextView textView, View parentView, String prefix, T value) {
        String textToSet;
        if (value instanceof List) {
            textToSet = listToString((List<String>) value);
        } else if (value instanceof String) {
            textToSet = defaultText((String) value);
        } else {
            textToSet = "-";
        }

        if (textToSet.equals("-")) {
            parentView.setVisibility(View.GONE); // Sembunyikan LinearLayout induk
        } else {
            parentView.setVisibility(View.VISIBLE); // Pastikan LinearLayout induk terlihat
            textView.setText(prefix + textToSet); // Set teks pada TextView nilai
        }
    }

    private String defaultText(String text) {
        return text != null && !text.isEmpty() ? text : "-";
    }

    private String listToString(List<String> list) {
        return list != null && !list.isEmpty() ? TextUtils.join("\n\n", list) : "-";
    }

    public String ensureTextValueForDb(String text) {
        return text != null && !text.trim().isEmpty() ? text.trim() : "-";
    }

    public <T> T safeGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            Log.e("DrugDetailHelper", "SafeGet Exception: " + e.getMessage());
            return null;
        }
    }

    public String getProductTypeFromDrug(DrugResponse.Drug drug) {
        List<String> productTypesList = safeGet(() -> drug.getOpenfda().getProductType());
        if (productTypesList != null && !productTypesList.isEmpty()) {
            return productTypesList.get(0);
        }
        return "UNKNOWN";
    }

    public void setProductTypeImage(String drugType, ImageView imageView) {
        if (drugType != null) {
            if ("HUMAN PRESCRIPTION DRUG".equalsIgnoreCase(drugType)) {
                imageView.setImageResource(R.drawable.pic_human_prescription_drug);
            } else if ("HUMAN OTC DRUG".equalsIgnoreCase(drugType)) {
                imageView.setImageResource(R.drawable.pic_human_otc_drug);
            } else if ("CELLULAR THERAPY".equalsIgnoreCase(drugType)) {
                imageView.setImageResource(R.drawable.pic_cellular_therapy);
            } else {
                imageView.setImageResource(R.drawable.pic_ask);
            }
        } else {
            imageView.setImageResource(R.drawable.pic_ask);
        }
    }

    public Favorite mapDrugResponseToFavorite(DrugResponse.Drug drug, String currentUserEmail) {
        if (drug == null) return null;

        Favorite favorite = new Favorite();
        favorite.setIdObat(ensureTextValueForDb(drug.getId()));
        favorite.setEmailFk(currentUserEmail);
        favorite.setProductType(ensureTextValueForDb(getProductTypeFromDrug(drug)));
        favorite.setGenericName(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getGenericName()))));

        favorite.setBrandName(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getBrandName()))));
        favorite.setEffectiveTime(ensureTextValueForDb(drug.getEffectiveTime()));
        favorite.setVersion(ensureTextValueForDb(drug.getVersion()));
        favorite.setSetId(ensureTextValueForDb(drug.getSetId()));
        favorite.setManufacturerName(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getManufacturerName()))));
        favorite.setRoute(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getRoute()))));
        favorite.setRxcui(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getRxcui()))));
        favorite.setUnii(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getUnii()))));
        favorite.setPharmClassEpc(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getPharmClassEpc()))));
        favorite.setPharmClassMoa(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getPharmClassMoa()))));
        favorite.setProductNdc(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getProductNdc()))));
        favorite.setSplSetId(ensureTextValueForDb(listToString(safeGet(() -> drug.getOpenfda().getSplSetId()))));

        favorite.setActiveIngredient(ensureTextValueForDb(listToString(safeGet(() -> drug.getActiveIngredient()))));
        favorite.setInactiveIngredient(ensureTextValueForDb(listToString(safeGet(() -> drug.getInactiveIngredient()))));
        favorite.setDescription(ensureTextValueForDb(listToString(safeGet(() -> drug.getDescription()))));
        favorite.setIndicationsAndUsage(ensureTextValueForDb(listToString(safeGet(() -> drug.getIndicationsAndUsage()))));
        favorite.setDosageAndAdministration(ensureTextValueForDb(listToString(safeGet(() -> drug.getDosageAndAdministration()))));
        favorite.setContraindications(ensureTextValueForDb(listToString(safeGet(() -> drug.getContraindications()))));
        favorite.setWarnings(ensureTextValueForDb(listToString(safeGet(() -> drug.getWarnings()))));
        favorite.setWarningsAndCautions(ensureTextValueForDb(listToString(safeGet(() -> drug.getWarningsAndCautions()))));
        favorite.setBoxedWarning(ensureTextValueForDb(listToString(safeGet(() -> drug.getBoxedWarning()))));
        favorite.setAdverseReactions(ensureTextValueForDb(listToString(safeGet(() -> drug.getAdverseReactions()))));
        favorite.setDrugInteractions(ensureTextValueForDb(listToString(safeGet(() -> drug.getDrugInteractions()))));
        favorite.setClinicalPharmacology(ensureTextValueForDb(listToString(safeGet(() -> drug.getClinicalPharmacology()))));
        favorite.setPharmacodynamics(ensureTextValueForDb(listToString(safeGet(() -> drug.getPharmacodynamics()))));
        favorite.setPharmacokinetics(ensureTextValueForDb(listToString(safeGet(() -> drug.getPharmacokinetics()))));
        favorite.setMechanismOfAction(ensureTextValueForDb(listToString(safeGet(() -> drug.getMechanismOfAction()))));
        favorite.setUseInSpecificPopulations(ensureTextValueForDb(listToString(safeGet(() -> drug.getUseInSpecificPopulations()))));
        favorite.setPregnancy(ensureTextValueForDb(listToString(safeGet(() -> drug.getPregnancy()))));
        favorite.setNursingMothers(ensureTextValueForDb(listToString(safeGet(() -> drug.getNursingMothers()))));
        favorite.setPediatricUse(ensureTextValueForDb(listToString(safeGet(() -> drug.getPediatricUse()))));
        favorite.setGeriatricUse(ensureTextValueForDb(listToString(safeGet(() -> drug.getGeriatricUse()))));
        favorite.setOverdosage(ensureTextValueForDb(listToString(safeGet(() -> drug.getOverdosage()))));
        favorite.setHowSupplied(ensureTextValueForDb(listToString(safeGet(() -> drug.getHowSupplied()))));
        favorite.setStorageAndHandling(ensureTextValueForDb(listToString(safeGet(() -> drug.getStorageAndHandling()))));
        favorite.setPatientMedicationInformation(ensureTextValueForDb(listToString(safeGet(() -> drug.getPatientMedicationInformation()))));
        favorite.setSplMedguide(ensureTextValueForDb(listToString(safeGet(() -> drug.getSplMedguide()))));
        favorite.setSplPatientPackageInsert(ensureTextValueForDb(listToString(safeGet(() -> drug.getSplPatientPackageInsert()))));
        favorite.setDrugReferences(ensureTextValueForDb(listToString(safeGet(() -> drug.getReferences()))));
        favorite.setQuestions(ensureTextValueForDb(listToString(safeGet(() -> drug.getQuestions()))));

        favorite.setInformationForPatients(ensureTextValueForDb(listToString(safeGet(() -> drug.getInformationForPatients()))));
        favorite.setAskDoctorOrPharmacist(ensureTextValueForDb(listToString(safeGet(() -> drug.getAskDoctorOrPharmacist()))));
        favorite.setSafeHandlingWarning(ensureTextValueForDb(listToString(safeGet(() -> drug.getSafeHandlingWarning()))));
        favorite.setUserSafetyWarnings(ensureTextValueForDb(listToString(safeGet(() -> drug.getUserSafetyWarnings()))));
        favorite.setSplUnclassifiedSection(ensureTextValueForDb(listToString(safeGet(() -> drug.getSplUnclassifiedSection()))));
        favorite.setControlledSubstance(ensureTextValueForDb(listToString(safeGet(() -> drug.getControlledSubstance()))));
        favorite.setDrugAbuseAndDependence(ensureTextValueForDb(listToString(safeGet(() -> drug.getDrugAbuseAndDependence()))));
        favorite.setLaborAndDelivery(ensureTextValueForDb(listToString(safeGet(() -> drug.getLaborAndDelivery()))));
        favorite.setLaboratoryTests(ensureTextValueForDb(listToString(safeGet(() -> drug.getLaboratoryTests()))));
        favorite.setNonclinicalToxicology(ensureTextValueForDb(listToString(safeGet(() -> drug.getNonclinicalToxicology()))));
        favorite.setMicrobiology(ensureTextValueForDb(listToString(safeGet(() -> drug.getMicrobiology()))));
        favorite.setCarcinogenesisAndMutagenesisAndImpairmentOfFertility(ensureTextValueForDb(listToString(safeGet(() -> drug.getCarcinogenesisAndMutagenesisAndImpairmentOfFertility()))));
        favorite.setRecentMajorChanges(ensureTextValueForDb(listToString(safeGet(() -> drug.getRecentMajorChanges()))));
        favorite.setRisks(ensureTextValueForDb(listToString(safeGet(() -> drug.getRisks()))));
        favorite.setStopUse(ensureTextValueForDb(listToString(safeGet(() -> drug.getStopUse()))));

        return favorite;
    }

    public void displayDrugData(DrugResponse.Drug drug) {
        if (drug == null) {
            clearAllDrugTextViews();
            return;
        }
        String drugType = getProductTypeFromDrug(drug);
        setProductTypeImage(drugType, binding.tvPicType);

        // Tidak ada parent LinearLayout untuk ini karena tidak ada label terpisah
        setTextSafely(binding.tvDrugId, binding.tvDrugId, "ID : ", drug.getId());
        setTextSafely(binding.tvGenericName, binding.tvGenericName, "", safeGet(() -> drug.getOpenfda().getGenericName()));
        setTextSafely(binding.tvBrandName, binding.tvBrandName, "", safeGet(() -> drug.getOpenfda().getBrandName()));
        setTextSafely(binding.tvEffectiveTime, binding.tvEffectiveTime, "Effective Time : ", drug.getEffectiveTime());
        setTextSafely(binding.tvVersion, binding.tvVersion, "Versi Dokumen : ", drug.getVersion());
        setTextSafely(binding.tvSetId, binding.tvSetId, "Set Id : ", drug.getSetId());


        // Mulai dari sini, setiap pemanggilan setTextSafely menggunakan LinearLayout induk yang baru
        setTextSafely(binding.tvManufakturName, binding.llManufakturNameContainer, "", safeGet(() -> drug.getOpenfda().getManufacturerName()));
        setTextSafely(binding.tvProductType, binding.llProductTypeContainer, "", safeGet(() -> drug.getOpenfda().getProductType()));
        setTextSafely(binding.tvRoute, binding.llRouteContainer, "", safeGet(() -> drug.getOpenfda().getRoute()));
        setTextSafely(binding.tvRxcui, binding.llRxcuiContainer, "", safeGet(() -> drug.getOpenfda().getRxcui()));
        setTextSafely(binding.tvUnii, binding.llUniiContainer, "", safeGet(() -> drug.getOpenfda().getUnii()));
        setTextSafely(binding.tvPharmClassEpc, binding.llPharmClassEpcContainer, "", safeGet(() -> drug.getOpenfda().getPharmClassEpc()));
        setTextSafely(binding.tvPharmClassMoa, binding.llPharmClassMoaContainer, "", safeGet(() -> drug.getOpenfda().getPharmClassMoa()));
        setTextSafely(binding.tvProductNdc, binding.llProductNdcContainer, "", safeGet(() -> drug.getOpenfda().getProductNdc()));
        setTextSafely(binding.tvSplSetId, binding.llSplSetIdContainer, "", safeGet(() -> drug.getOpenfda().getSplSetId()));

        setTextSafely(binding.tvActiveIngredient, binding.llActiveIngredientContainer, "", drug.getActiveIngredient());
        setTextSafely(binding.tvInactiveIngredient, binding.llInactiveIngredientContainer, "", drug.getInactiveIngredient());
        setTextSafely(binding.tvDescription, binding.llDescriptionContainer, "", drug.getDescription());
        setTextSafely(binding.tvIndicationsAndUsage, binding.llIndicationsAndUsageContainer, "", drug.getIndicationsAndUsage());
        setTextSafely(binding.tvDosageAndAdministration, binding.llDosageAndAdministrationContainer, "", drug.getDosageAndAdministration());
        setTextSafely(binding.tvContraindications, binding.llContraindicationsContainer, "", drug.getContraindications());
        setTextSafely(binding.tvWarnings, binding.llWarningsContainer, "", drug.getWarnings());
        setTextSafely(binding.tvWarningsAndCautions, binding.llWarningsAndCautionsContainer, "", drug.getWarningsAndCautions());
        setTextSafely(binding.tvBoxedWarning, binding.llBoxedWarningContainer, "", drug.getBoxedWarning());
        setTextSafely(binding.tvAdverseReactions, binding.llAdverseReactionsContainer, "", drug.getAdverseReactions());
        setTextSafely(binding.tvDrugInteractions, binding.llDrugInteractionsContainer, "", drug.getDrugInteractions());
        setTextSafely(binding.tvClinicalPharmacology, binding.llClinicalPharmacologyContainer, "", drug.getClinicalPharmacology());
        setTextSafely(binding.tvPharmacodynamics, binding.llPharmacodynamicsContainer, "", drug.getPharmacodynamics());
        setTextSafely(binding.tvPharmacokinetics, binding.llPharmacokineticsContainer, "", drug.getPharmacokinetics());
        setTextSafely(binding.tvMechanismOfAction, binding.llMechanismOfActionContainer, "", drug.getMechanismOfAction());
        setTextSafely(binding.tvUseInSpecificPopulations, binding.llUseInSpecificPopulationsContainer, "", drug.getUseInSpecificPopulations());
        setTextSafely(binding.tvPregnancy, binding.llPregnancyContainer, "", drug.getPregnancy());
        setTextSafely(binding.tvNursingMothers, binding.llNursingMothersContainer, "", drug.getNursingMothers());
        setTextSafely(binding.tvPediatricUse, binding.llPediatricUseContainer, "", drug.getPediatricUse());
        setTextSafely(binding.tvGeriatricUse, binding.llGeriatricUseContainer, "", drug.getGeriatricUse());
        setTextSafely(binding.tvOverdosage, binding.llOverdosageContainer, "", drug.getOverdosage());
        setTextSafely(binding.tvHowSupplied, binding.llHowSuppliedContainer, "", drug.getHowSupplied());
        setTextSafely(binding.tvStorageAndHandling, binding.llStorageAndHandlingContainer, "", drug.getStorageAndHandling());
        setTextSafely(binding.tvPatientMedicationInformation, binding.llPatientMedicationInformationContainer, "", drug.getPatientMedicationInformation());
        setTextSafely(binding.tvSplMedguide, binding.llSplMedguideContainer, "", drug.getSplMedguide());
        setTextSafely(binding.tvSplPatientPackageInsert, binding.llSplPatientPackageInsertContainer, "", drug.getSplPatientPackageInsert());
        setTextSafely(binding.tvReferences, binding.llReferencesContainer, "", drug.getReferences());
        setTextSafely(binding.tvQuestions, binding.llQuestionsContainer, "", drug.getQuestions());

        setTextSafely(binding.tvInformationForPatients, binding.llInformationForPatientsContainer, "", drug.getInformationForPatients());
        setTextSafely(binding.tvAskDoctorOrPharmacist, binding.llAskDoctorOrPharmacistContainer, "", drug.getAskDoctorOrPharmacist());
        setTextSafely(binding.tvSafeHandlingWarning, binding.llSafeHandlingWarningContainer, "", drug.getSafeHandlingWarning());
        setTextSafely(binding.tvUserSafetyWarnings, binding.llUserSafetyWarningsContainer, "", drug.getUserSafetyWarnings());
        setTextSafely(binding.tvSplUnclassifiedSection, binding.llSplUnclassifiedSectionContainer, "", drug.getSplUnclassifiedSection());
        setTextSafely(binding.tvControlledSubstance, binding.llControlledSubstanceContainer, "", drug.getControlledSubstance());
        setTextSafely(binding.tvDrugAbuseAndDependence, binding.llDrugAbuseAndDependenceContainer, "", drug.getDrugAbuseAndDependence());
        setTextSafely(binding.tvLaborAndDelivery, binding.llLaborAndDeliveryContainer, "", drug.getLaborAndDelivery());
        setTextSafely(binding.tvLaboratoryTests, binding.llLaboratoryTestsContainer, "", drug.getLaboratoryTests());
        setTextSafely(binding.tvNonclinicalToxicology, binding.llNonclinicalToxicologyContainer, "", drug.getNonclinicalToxicology());
        setTextSafely(binding.tvMicrobiology, binding.llMicrobiologyContainer, "", drug.getMicrobiology());
        setTextSafely(binding.tvCarcinogenesisAndMutagenesisAndImpairmentOfFertility, binding.llCarcinogenesisAndMutagenesisAndImpairmentOfFertilityContainer, "", drug.getCarcinogenesisAndMutagenesisAndImpairmentOfFertility());
        setTextSafely(binding.tvRecentMajorChanges, binding.llRecentMajorChangesContainer, "", drug.getRecentMajorChanges());
        setTextSafely(binding.tvRisks, binding.llRisksContainer, "", drug.getRisks());
        setTextSafely(binding.tvStopUse, binding.llStopUseContainer, "", drug.getStopUse());
    }

    public void displayFavoriteData(Favorite favorite) {
        if (favorite == null) {
            clearAllDrugTextViews();
            return;
        }
        String drugType = favorite.getProductType();
        setProductTypeImage(drugType, binding.tvPicType);

        // Tidak ada parent LinearLayout untuk ini karena tidak ada label terpisah
        setTextSafely(binding.tvDrugId, binding.tvDrugId, "ID : ", favorite.getIdObat());
        setTextSafely(binding.tvGenericName, binding.tvGenericName, "", favorite.getGenericName());
        setTextSafely(binding.tvBrandName, binding.tvBrandName, "", favorite.getBrandName());
        setTextSafely(binding.tvEffectiveTime, binding.tvEffectiveTime, "Effective Time : ", favorite.getEffectiveTime());
        setTextSafely(binding.tvVersion, binding.tvVersion, "Versi Dokumen : ", favorite.getVersion());
        setTextSafely(binding.tvSetId, binding.tvSetId, "Set Id : ", favorite.getSetId());


        // Mulai dari sini, setiap pemanggilan setTextSafely menggunakan LinearLayout induk yang baru
        setTextSafely(binding.tvManufakturName, binding.llManufakturNameContainer, "", favorite.getManufacturerName());
        setTextSafely(binding.tvProductType, binding.llProductTypeContainer, "", favorite.getProductType());
        setTextSafely(binding.tvRoute, binding.llRouteContainer, "", favorite.getRoute());
        setTextSafely(binding.tvRxcui, binding.llRxcuiContainer, "", favorite.getRxcui());
        setTextSafely(binding.tvUnii, binding.llUniiContainer, "", favorite.getUnii());
        setTextSafely(binding.tvPharmClassEpc, binding.llPharmClassEpcContainer, "", favorite.getPharmClassEpc());
        setTextSafely(binding.tvPharmClassMoa, binding.llPharmClassMoaContainer, "", favorite.getPharmClassMoa());
        setTextSafely(binding.tvProductNdc, binding.llProductNdcContainer, "", favorite.getProductNdc());
        setTextSafely(binding.tvSplSetId, binding.llSplSetIdContainer, "", favorite.getSplSetId());

        setTextSafely(binding.tvActiveIngredient, binding.llActiveIngredientContainer, "", favorite.getActiveIngredient());
        setTextSafely(binding.tvInactiveIngredient, binding.llInactiveIngredientContainer, "", favorite.getInactiveIngredient());
        setTextSafely(binding.tvDescription, binding.llDescriptionContainer, "", favorite.getDescription());
        setTextSafely(binding.tvIndicationsAndUsage, binding.llIndicationsAndUsageContainer, "", favorite.getIndicationsAndUsage());
        setTextSafely(binding.tvDosageAndAdministration, binding.llDosageAndAdministrationContainer, "", favorite.getDosageAndAdministration());
        setTextSafely(binding.tvContraindications, binding.llContraindicationsContainer, "", favorite.getContraindications());
        setTextSafely(binding.tvWarnings, binding.llWarningsContainer, "", favorite.getWarnings());
        setTextSafely(binding.tvWarningsAndCautions, binding.llWarningsAndCautionsContainer, "", favorite.getWarningsAndCautions());
        setTextSafely(binding.tvBoxedWarning, binding.llBoxedWarningContainer, "", favorite.getBoxedWarning());
        setTextSafely(binding.tvAdverseReactions, binding.llAdverseReactionsContainer, "", favorite.getAdverseReactions());
        setTextSafely(binding.tvDrugInteractions, binding.llDrugInteractionsContainer, "", favorite.getDrugInteractions());
        setTextSafely(binding.tvClinicalPharmacology, binding.llClinicalPharmacologyContainer, "", favorite.getClinicalPharmacology());
        setTextSafely(binding.tvPharmacodynamics, binding.llPharmacodynamicsContainer, "", favorite.getPharmacodynamics());
        setTextSafely(binding.tvPharmacokinetics, binding.llPharmacokineticsContainer, "", favorite.getPharmacokinetics());
        setTextSafely(binding.tvMechanismOfAction, binding.llMechanismOfActionContainer, "", favorite.getMechanismOfAction());
        setTextSafely(binding.tvUseInSpecificPopulations, binding.llUseInSpecificPopulationsContainer, "", favorite.getUseInSpecificPopulations());
        setTextSafely(binding.tvPregnancy, binding.llPregnancyContainer, "", favorite.getPregnancy());
        setTextSafely(binding.tvNursingMothers, binding.llNursingMothersContainer, "", favorite.getNursingMothers());
        setTextSafely(binding.tvPediatricUse, binding.llPediatricUseContainer, "", favorite.getPediatricUse());
        setTextSafely(binding.tvGeriatricUse, binding.llGeriatricUseContainer, "", favorite.getGeriatricUse());
        setTextSafely(binding.tvOverdosage, binding.llOverdosageContainer, "", favorite.getOverdosage());
        setTextSafely(binding.tvHowSupplied, binding.llHowSuppliedContainer, "", favorite.getHowSupplied());
        setTextSafely(binding.tvStorageAndHandling, binding.llStorageAndHandlingContainer, "", favorite.getStorageAndHandling());
        setTextSafely(binding.tvPatientMedicationInformation, binding.llPatientMedicationInformationContainer, "", favorite.getPatientMedicationInformation());
        setTextSafely(binding.tvSplMedguide, binding.llSplMedguideContainer, "", favorite.getSplMedguide());
        setTextSafely(binding.tvSplPatientPackageInsert, binding.llSplPatientPackageInsertContainer, "", favorite.getSplPatientPackageInsert());
        setTextSafely(binding.tvReferences, binding.llReferencesContainer, "", favorite.getDrugReferences());
        setTextSafely(binding.tvQuestions, binding.llQuestionsContainer, "", favorite.getQuestions());

        setTextSafely(binding.tvInformationForPatients, binding.llInformationForPatientsContainer, "", favorite.getInformationForPatients());
        setTextSafely(binding.tvAskDoctorOrPharmacist, binding.llAskDoctorOrPharmacistContainer, "", favorite.getAskDoctorOrPharmacist());
        setTextSafely(binding.tvSafeHandlingWarning, binding.llSafeHandlingWarningContainer, "", favorite.getSafeHandlingWarning());
        setTextSafely(binding.tvUserSafetyWarnings, binding.llUserSafetyWarningsContainer, "", favorite.getUserSafetyWarnings());
        setTextSafely(binding.tvSplUnclassifiedSection, binding.llSplUnclassifiedSectionContainer, "", favorite.getSplUnclassifiedSection());
        setTextSafely(binding.tvControlledSubstance, binding.llControlledSubstanceContainer, "", favorite.getControlledSubstance());
        setTextSafely(binding.tvDrugAbuseAndDependence, binding.llDrugAbuseAndDependenceContainer, "", favorite.getDrugAbuseAndDependence());
        setTextSafely(binding.tvLaborAndDelivery, binding.llLaborAndDeliveryContainer, "", favorite.getLaborAndDelivery());
        setTextSafely(binding.tvLaboratoryTests, binding.llLaboratoryTestsContainer, "", favorite.getLaboratoryTests());
        setTextSafely(binding.tvNonclinicalToxicology, binding.llNonclinicalToxicologyContainer, "", favorite.getNonclinicalToxicology());
        setTextSafely(binding.tvMicrobiology, binding.llMicrobiologyContainer, "", favorite.getMicrobiology());
        setTextSafely(binding.tvCarcinogenesisAndMutagenesisAndImpairmentOfFertility, binding.llCarcinogenesisAndMutagenesisAndImpairmentOfFertilityContainer, "", favorite.getCarcinogenesisAndMutagenesisAndImpairmentOfFertility());
        setTextSafely(binding.tvRecentMajorChanges, binding.llRecentMajorChangesContainer, "", favorite.getRecentMajorChanges());
        setTextSafely(binding.tvRisks, binding.llRisksContainer, "", favorite.getRisks());
        setTextSafely(binding.tvStopUse, binding.llStopUseContainer, "", favorite.getStopUse());
    }

    public void clearAllDrugTextViews() {
        // Untuk TextView utama tanpa label terpisah
        resetTextViewAndParent(binding.tvDrugId, binding.tvDrugId);
        resetTextViewAndParent(binding.tvGenericName, binding.tvGenericName);
        resetTextViewAndParent(binding.tvBrandName, binding.tvBrandName);
        resetTextViewAndParent(binding.tvEffectiveTime, binding.tvEffectiveTime);
        resetTextViewAndParent(binding.tvVersion, binding.tvVersion);
        resetTextViewAndParent(binding.tvSetId, binding.tvSetId);


        // Untuk setiap LinearLayout induk yang baru
        resetTextViewAndParent(binding.tvManufakturName, binding.llManufakturNameContainer);
        resetTextViewAndParent(binding.tvProductType, binding.llProductTypeContainer);
        resetTextViewAndParent(binding.tvRoute, binding.llRouteContainer);
        resetTextViewAndParent(binding.tvRxcui, binding.llRxcuiContainer);
        resetTextViewAndParent(binding.tvUnii, binding.llUniiContainer);
        resetTextViewAndParent(binding.tvPharmClassEpc, binding.llPharmClassEpcContainer);
        resetTextViewAndParent(binding.tvPharmClassMoa, binding.llPharmClassMoaContainer);
        resetTextViewAndParent(binding.tvProductNdc, binding.llProductNdcContainer);
        resetTextViewAndParent(binding.tvSplSetId, binding.llSplSetIdContainer);

        resetTextViewAndParent(binding.tvActiveIngredient, binding.llActiveIngredientContainer);
        resetTextViewAndParent(binding.tvInactiveIngredient, binding.llInactiveIngredientContainer);
        resetTextViewAndParent(binding.tvDescription, binding.llDescriptionContainer);
        resetTextViewAndParent(binding.tvIndicationsAndUsage, binding.llIndicationsAndUsageContainer);
        resetTextViewAndParent(binding.tvDosageAndAdministration, binding.llDosageAndAdministrationContainer);
        resetTextViewAndParent(binding.tvContraindications, binding.llContraindicationsContainer);
        resetTextViewAndParent(binding.tvWarnings, binding.llWarningsContainer);
        resetTextViewAndParent(binding.tvWarningsAndCautions, binding.llWarningsAndCautionsContainer);
        resetTextViewAndParent(binding.tvBoxedWarning, binding.llBoxedWarningContainer);
        resetTextViewAndParent(binding.tvAdverseReactions, binding.llAdverseReactionsContainer);
        resetTextViewAndParent(binding.tvDrugInteractions, binding.llDrugInteractionsContainer);
        resetTextViewAndParent(binding.tvClinicalPharmacology, binding.llClinicalPharmacologyContainer);
        resetTextViewAndParent(binding.tvPharmacodynamics, binding.llPharmacodynamicsContainer);
        resetTextViewAndParent(binding.tvPharmacokinetics, binding.llPharmacokineticsContainer);
        resetTextViewAndParent(binding.tvMechanismOfAction, binding.llMechanismOfActionContainer);
        resetTextViewAndParent(binding.tvUseInSpecificPopulations, binding.llUseInSpecificPopulationsContainer);
        resetTextViewAndParent(binding.tvPregnancy, binding.llPregnancyContainer);
        resetTextViewAndParent(binding.tvNursingMothers, binding.llNursingMothersContainer);
        resetTextViewAndParent(binding.tvPediatricUse, binding.llPediatricUseContainer);
        resetTextViewAndParent(binding.tvGeriatricUse, binding.llGeriatricUseContainer);
        resetTextViewAndParent(binding.tvOverdosage, binding.llOverdosageContainer);
        resetTextViewAndParent(binding.tvHowSupplied, binding.llHowSuppliedContainer);
        resetTextViewAndParent(binding.tvStorageAndHandling, binding.llStorageAndHandlingContainer);
        resetTextViewAndParent(binding.tvPatientMedicationInformation, binding.llPatientMedicationInformationContainer);
        resetTextViewAndParent(binding.tvSplMedguide, binding.llSplMedguideContainer);
        resetTextViewAndParent(binding.tvSplPatientPackageInsert, binding.llSplPatientPackageInsertContainer);
        resetTextViewAndParent(binding.tvReferences, binding.llReferencesContainer);
        resetTextViewAndParent(binding.tvQuestions, binding.llQuestionsContainer);

        resetTextViewAndParent(binding.tvInformationForPatients, binding.llInformationForPatientsContainer);
        resetTextViewAndParent(binding.tvAskDoctorOrPharmacist, binding.llAskDoctorOrPharmacistContainer);
        resetTextViewAndParent(binding.tvSafeHandlingWarning, binding.llSafeHandlingWarningContainer);
        resetTextViewAndParent(binding.tvUserSafetyWarnings, binding.llUserSafetyWarningsContainer);
        resetTextViewAndParent(binding.tvSplUnclassifiedSection, binding.llSplUnclassifiedSectionContainer);
        resetTextViewAndParent(binding.tvControlledSubstance, binding.llControlledSubstanceContainer);
        resetTextViewAndParent(binding.tvDrugAbuseAndDependence, binding.llDrugAbuseAndDependenceContainer);
        resetTextViewAndParent(binding.tvLaborAndDelivery, binding.llLaborAndDeliveryContainer);
        resetTextViewAndParent(binding.tvLaboratoryTests, binding.llLaboratoryTestsContainer);
        resetTextViewAndParent(binding.tvNonclinicalToxicology, binding.llNonclinicalToxicologyContainer);
        resetTextViewAndParent(binding.tvMicrobiology, binding.llMicrobiologyContainer);
        resetTextViewAndParent(binding.tvCarcinogenesisAndMutagenesisAndImpairmentOfFertility, binding.llCarcinogenesisAndMutagenesisAndImpairmentOfFertilityContainer); // SEKARANG BENAR
        resetTextViewAndParent(binding.tvCarcinogenesisAndMutagenesisAndImpairmentOfFertility, binding.llCarcinogenesisAndMutagenesisAndImpairmentOfFertilityContainer);

        resetTextViewAndParent(binding.tvRecentMajorChanges, binding.llRecentMajorChangesContainer);
        resetTextViewAndParent(binding.tvRisks, binding.llRisksContainer);
        resetTextViewAndParent(binding.tvStopUse, binding.llStopUseContainer);
    }

    // Metode bantu untuk mereset TextView dan LinearLayout induknya
    private void resetTextViewAndParent(TextView textView, View parentView) {
        textView.setText(""); // Kosongkan teks nilai
        parentView.setVisibility(View.GONE); // Sembunyikan seluruh blok
    }
}