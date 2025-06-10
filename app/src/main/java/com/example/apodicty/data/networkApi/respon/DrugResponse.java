package com.example.apodicty.data.networkApi.respon;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DrugResponse {
    @SerializedName("meta")
    private Meta meta;

    @SerializedName("results")
    private List<Drug> results;

    public Meta getMeta() {
        return meta;
    }

    public List<Drug> getResults() {
        return results;
    }

    public static class Meta {
        @SerializedName("results")
        private ResultsInfo results; // Inner class results

        public ResultsInfo getResults() {
            return results;
        }

        public static class ResultsInfo {
            @SerializedName("skip")
            private int skip;
            @SerializedName("limit")
            private int limit;
            @SerializedName("total")
            private int total;

            public int getSkip() { return skip; }
            public int getLimit() { return limit; }
            public int getTotal() { return total; }

        }
    }

    public static class Drug {
        @SerializedName("id")
        private String id;
        public String getId() { return id; }

        @SerializedName("effective_time")
        public String effective_time; // Tetap seperti ini (public, snake_case)
        public String getEffectiveTime() { return effective_time; }

        @SerializedName("version")
        private String version;
        public String getVersion() { return version; }

        @SerializedName("set_id")
        private String set_id; // Tetap seperti ini (snake_case)
        public String getSetId(){ return set_id; }

        @SerializedName("openfda")
        private OpenFda openfda;
        public OpenFda getOpenfda() { return openfda; }

        @SerializedName("description")
        private List<String> description;
        public List<String> getDescription() { return description; }

        @SerializedName("purpose")
        private List<String> purpose;
        public List<String> getPurpose() { return purpose; }

        // --- MULAI PENAMBAHAN FITUR DETAIL KLINIS DAN PENDUKUNG (TIDAK DI DALAM OPENFDA) ---
        // Penamaan variabel: camelCase
        // SerializedName: sesuai nama field di JSON (snake_case)

        @SerializedName("active_ingredient")
        private List<String> activeIngredient;
        public List<String> getActiveIngredient() { return activeIngredient; }

        @SerializedName("inactive_ingredient")
        private List<String> inactiveIngredient;
        public List<String> getInactiveIngredient() { return inactiveIngredient; }

        @SerializedName("indications_and_usage")
        private List<String> indicationsAndUsage;
        public List<String> getIndicationsAndUsage() { return indicationsAndUsage; }

        @SerializedName("dosage_and_administration")
        private List<String> dosageAndAdministration;
        public List<String> getDosageAndAdministration() { return dosageAndAdministration; }

        @SerializedName("contraindications")
        private List<String> contraindications;
        public List<String> getContraindications() { return contraindications; }

        @SerializedName("warnings")
        private List<String> warnings;
        public List<String> getWarnings() { return warnings; }

        @SerializedName("warnings_and_cautions")
        private List<String> warningsAndCautions;
        public List<String> getWarningsAndCautions() { return warningsAndCautions; }

        @SerializedName("boxed_warning")
        private List<String> boxedWarning;
        public List<String> getBoxedWarning() { return boxedWarning; }

        @SerializedName("adverse_reactions")
        private List<String> adverseReactions;
        public List<String> getAdverseReactions() { return adverseReactions; }

        @SerializedName("drug_interactions")
        private List<String> drugInteractions;
        public List<String> getDrugInteractions() { return drugInteractions; }

        @SerializedName("clinical_pharmacology")
        private List<String> clinicalPharmacology;
        public List<String> getClinicalPharmacology() { return clinicalPharmacology; }

        @SerializedName("pharmacodynamics")
        private List<String> pharmacodynamics;
        public List<String> getPharmacodynamics() { return pharmacodynamics; }

        @SerializedName("pharmacokinetics")
        private List<String> pharmacokinetics;
        public List<String> getPharmacokinetics() { return pharmacokinetics; }

        @SerializedName("mechanism_of_action")
        private List<String> mechanismOfAction;
        public List<String> getMechanismOfAction() { return mechanismOfAction; }

        @SerializedName("use_in_specific_populations")
        private List<String> useInSpecificPopulations;
        public List<String> getUseInSpecificPopulations() { return useInSpecificPopulations; }

        @SerializedName("pregnancy")
        private List<String> pregnancy;
        public List<String> getPregnancy() { return pregnancy; }

        @SerializedName("nursing_mothers")
        private List<String> nursingMothers;
        public List<String> getNursingMothers() { return nursingMothers; }

        @SerializedName("pediatric_use")
        private List<String> pediatricUse;
        public List<String> getPediatricUse() { return pediatricUse; }

        @SerializedName("geriatric_use")
        private List<String> geriatricUse;
        public List<String> getGeriatricUse() { return geriatricUse; }

        @SerializedName("overdosage")
        private List<String> overdosage;
        public List<String> getOverdosage() { return overdosage; }

        @SerializedName("how_supplied")
        private List<String> howSupplied;
        public List<String> getHowSupplied() { return howSupplied; }

        @SerializedName("storage_and_handling")
        private List<String> storageAndHandling;
        public List<String> getStorageAndHandling() { return storageAndHandling; }

        @SerializedName("patient_medication_information")
        private List<String> patientMedicationInformation;
        public List<String> getPatientMedicationInformation() { return patientMedicationInformation; }

        @SerializedName("spl_medguide")
        private List<String> splMedguide;
        public List<String> getSplMedguide() { return splMedguide; }

        @SerializedName("spl_patient_package_insert")
        private List<String> splPatientPackageInsert;
        public List<String> getSplPatientPackageInsert() { return splPatientPackageInsert; }

        @SerializedName("references")
        private List<String> references;
        public List<String> getReferences() { return references; }

        @SerializedName("questions")
        private List<String> questions;
        public List<String> getQuestions() { return questions; }

        // --- Fitur Pendukung yang Anda sebutkan di daftar terakhir ---
        @SerializedName("information_for_patients")
        private List<String> informationForPatients;
        public List<String> getInformationForPatients() { return informationForPatients; }

        @SerializedName("ask_doctor_or_pharmacist")
        private List<String> askDoctorOrPharmacist;
        public List<String> getAskDoctorOrPharmacist() { return askDoctorOrPharmacist; }

        @SerializedName("safe_handling_warning")
        private List<String> safeHandlingWarning;
        public List<String> getSafeHandlingWarning() { return safeHandlingWarning; }

        @SerializedName("user_safety_warnings")
        private List<String> userSafetyWarnings;
        public List<String> getUserSafetyWarnings() { return userSafetyWarnings; }

        @SerializedName("spl_unclassified_section")
        private List<String> splUnclassifiedSection;
        public List<String> getSplUnclassifiedSection() { return splUnclassifiedSection; }

        @SerializedName("controlled_substance")
        private List<String> controlledSubstance;
        public List<String> getControlledSubstance() { return controlledSubstance; }

        @SerializedName("drug_abuse_and_dependence")
        private List<String> drugAbuseAndDependence;
        public List<String> getDrugAbuseAndDependence() { return drugAbuseAndDependence; }

        @SerializedName("labor_and_delivery")
        private List<String> laborAndDelivery;
        public List<String> getLaborAndDelivery() { return laborAndDelivery; }

        @SerializedName("laboratory_tests")
        private List<String> laboratoryTests;
        public List<String> getLaboratoryTests() { return laboratoryTests; }

        @SerializedName("nonclinical_toxicology")
        private List<String> nonclinicalToxicology;
        public List<String> getNonclinicalToxicology() { return nonclinicalToxicology; }

        @SerializedName("microbiology")
        private List<String> microbiology;
        public List<String> getMicrobiology() { return microbiology; }

        @SerializedName("carcinogenesis_and_mutagenesis_and_impairment_of_fertility")
        private List<String> carcinogenesisAndMutagenesisAndImpairmentOfFertility;
        public List<String> getCarcinogenesisAndMutagenesisAndImpairmentOfFertility() { return carcinogenesisAndMutagenesisAndImpairmentOfFertility; }

        @SerializedName("recent_major_changes")
        private List<String> recentMajorChanges;
        public List<String> getRecentMajorChanges() { return recentMajorChanges; }

        @SerializedName("risks")
        private List<String> risks;
        public List<String> getRisks() { return risks; }

        @SerializedName("stop_use")
        private List<String> stopUse;
        public List<String> getStopUse() { return stopUse; }
        // --- AKHIR PENAMBAHAN FITUR LIST BARU ---
    }

    public static class OpenFda {
        // --- FIELD YANG SUDAH ADA DI OPENFDA, TETAP SEPERTI INI ---
        @SerializedName("brand_name")
        private List<String> brandName;
        public List<String> getBrandName() { return brandName; }

        @SerializedName("generic_name")
        private List<String> genericName;
        public List<String> getGenericName() { return genericName; }

        @SerializedName("manufacturer_name")
        private List<String> manufacturerName;
        public List<String> getManufacturerName() { return manufacturerName; }

        @SerializedName("product_type")
        private List<String> productType;
        public List<String> getProductType() { return productType; }

        @SerializedName("route")
        private List<String> route;
        public List<String> getRoute() { return route; }

        @SerializedName("rxcui")
        private List<String> rxcui;
        public List<String> getRxcui() { return rxcui; }

        @SerializedName("unii")
        private List<String> unii;
        public List<String> getUnii() { return unii; }

        @SerializedName("pharm_class_epc")
        private List<String> pharm_class_epc; // Tetap snake_case
        public List<String> getPharmClassEpc() { return pharm_class_epc; }

        @SerializedName("pharm_class_moa")
        private List<String> pharm_class_moa; // Tetap snake_case
        public List<String> getPharmClassMoa() { return pharm_class_moa; }

        @SerializedName("product_ndc")
        private List<String> product_ndc; // Tetap snake_case
        public List<String> getProductNdc() { return product_ndc; }

        @SerializedName("spl_set_id")
        private List<String> spl_set_id; // Tetap snake_case
        public List<String> getSplSetId() { return spl_set_id; }
    }
}