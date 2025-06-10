package com.example.apodicty.data.sqlitedatabase.database.favorite;

public class Favorite {
    private int idFavorite;
    private String idObat;
    private String emailFk;
    private String productType;
    private String genericName;

    // Tambahan properti baru
    private String brandName;
    private String effectiveTime;
    private String version;
    private String setId;
    private String manufacturerName;
    private String route;
    private String rxcui;
    private String unii;
    private String pharmClassEpc;
    private String pharmClassMoa;
    private String productNdc;
    private String splSetId;

    private String activeIngredient;
    private String inactiveIngredient;
    private String description;
    private String indicationsAndUsage;
    private String dosageAndAdministration;
    private String contraindications;
    private String warnings;
    private String warningsAndCautions;
    private String boxedWarning;
    private String adverseReactions;
    private String drugInteractions;
    private String clinicalPharmacology;
    private String pharmacodynamics;
    private String pharmacokinetics;
    private String mechanismOfAction;
    private String useInSpecificPopulations;
    private String pregnancy;
    private String nursingMothers;
    private String pediatricUse;
    private String geriatricUse;
    private String overdosage;
    private String howSupplied;
    private String storageAndHandling;
    private String patientMedicationInformation;
    private String splMedguide;
    private String splPatientPackageInsert;
    private String drugReferences;
    private String questions;

    private String informationForPatients;
    private String askDoctorOrPharmacist;
    private String safeHandlingWarning;
    private String userSafetyWarnings;
    private String splUnclassifiedSection;
    private String controlledSubstance;
    private String drugAbuseAndDependence;
    private String laborAndDelivery;
    private String laboratoryTests;
    private String nonclinicalToxicology;
    private String microbiology;
    private String carcinogenesisAndMutagenesisAndImpairmentOfFertility;
    private String recentMajorChanges;
    private String risks;
    private String stopUse;
    private String createdAt;


    public Favorite() {}

    public Favorite(int idFavorite, String idObat, String emailFk, String productType, String genericName) {
        this.idFavorite = idFavorite;
        this.idObat = idObat;
        this.emailFk = emailFk;
        this.productType = productType;
        this.genericName = genericName;
    }

    // --- GETTERS DAN SETTERS UNTUK SEMUA PROPERTI ---

    public int getIdFavorite() { return idFavorite; }
    public void setIdFavorite(int idFavorite) { this.idFavorite = idFavorite; }
    public String getIdObat() { return idObat; }
    public void setIdObat(String idObat) { this.idObat = idObat; }
    public String getEmailFk() { return emailFk; }
    public void setEmailFk(String emailFk) { this.emailFk = emailFk; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; } // <--- Sudah diperbaiki!
    public String getEffectiveTime() { return effectiveTime; }
    public void setEffectiveTime(String effectiveTime) { this.effectiveTime = effectiveTime; } // <--- Sudah diperbaiki!
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; } // <--- Sudah diperbaiki!
    public String getSetId() { return setId; }
    public void setSetId(String setId) { this.setId = setId; }
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public String getRxcui() { return rxcui; }
    public void setRxcui(String rxcui) { this.rxcui = rxcui; }
    public String getUnii() { return unii; }
    public void setUnii(String unii) { this.unii = unii; }
    public String getPharmClassEpc() { return pharmClassEpc; }
    public void setPharmClassEpc(String pharmClassEpc) { this.pharmClassEpc = pharmClassEpc; }
    public String getPharmClassMoa() { return pharmClassMoa; }
    public void setPharmClassMoa(String pharmClassMoa) { this.pharmClassMoa = pharmClassMoa; }
    public String getProductNdc() { return productNdc; }
    public void setProductNdc(String productNdc) { this.productNdc = productNdc; }
    public String getSplSetId() { return splSetId; }
    public void setSplSetId(String splSetId) { this.splSetId = splSetId; }
    public String getActiveIngredient() { return activeIngredient; }
    public void setActiveIngredient(String activeIngredient) { this.activeIngredient = activeIngredient; }
    public String getInactiveIngredient() { return inactiveIngredient; }
    public void setInactiveIngredient(String inactiveIngredient) { this.inactiveIngredient = inactiveIngredient; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIndicationsAndUsage() { return indicationsAndUsage; }
    public void setIndicationsAndUsage(String indicationsAndUsage) { this.indicationsAndUsage = indicationsAndUsage; }
    public String getDosageAndAdministration() { return dosageAndAdministration; }
    public void setDosageAndAdministration(String dosageAndAdministration) { this.dosageAndAdministration = dosageAndAdministration; }
    public String getContraindications() { return contraindications; }
    public void setContraindications(String contraindications) { this.contraindications = contraindications; }
    public String getWarnings() { return warnings; }
    public void setWarnings(String warnings) { this.warnings = warnings; }
    public String getWarningsAndCautions() { return warningsAndCautions; }
    public void setWarningsAndCautions(String warningsAndCautions) { this.warningsAndCautions = warningsAndCautions; }
    public String getBoxedWarning() { return boxedWarning; }
    public void setBoxedWarning(String boxedWarning) { this.boxedWarning = boxedWarning; }
    public String getAdverseReactions() { return adverseReactions; }
    public void setAdverseReactions(String adverseReactions) { this.adverseReactions = adverseReactions; }
    public String getDrugInteractions() { return drugInteractions; }
    public void setDrugInteractions(String drugInteractions) { this.drugInteractions = drugInteractions; }
    public String getClinicalPharmacology() { return clinicalPharmacology; }
    public void setClinicalPharmacology(String clinicalPharmacology) { this.clinicalPharmacology = clinicalPharmacology; }
    public String getPharmacodynamics() { return pharmacodynamics; }
    public void setPharmacodynamics(String pharmacodynamics) { this.pharmacodynamics = pharmacodynamics; }
    public String getPharmacokinetics() { return pharmacokinetics; }
    public void setPharmacokinetics(String pharmacokinetics) { this.pharmacokinetics = pharmacokinetics; }
    public String getMechanismOfAction() { return mechanismOfAction; }
    public void setMechanismOfAction(String mechanismOfAction) { this.mechanismOfAction = mechanismOfAction; }
    public String getUseInSpecificPopulations() { return useInSpecificPopulations; }
    public void setUseInSpecificPopulations(String useInSpecificPopulations) { this.useInSpecificPopulations = useInSpecificPopulations; }
    public String getPregnancy() { return pregnancy; }
    public void setPregnancy(String pregnancy) { this.pregnancy = pregnancy; }
    public String getNursingMothers() { return nursingMothers; }
    public void setNursingMothers(String nursingMothers) { this.nursingMothers = nursingMothers; }
    public String getPediatricUse() { return pediatricUse; }
    public void setPediatricUse(String pediatricUse) { this.pediatricUse = pediatricUse; }
    public String getGeriatricUse() { return geriatricUse; }
    public void setGeriatricUse(String geriatricUse) { this.geriatricUse = geriatricUse; }
    public String getOverdosage() { return overdosage; }
    public void setOverdosage(String overdosage) { this.overdosage = overdosage; }
    public String getHowSupplied() { return howSupplied; }
    public void setHowSupplied(String howSupplied) { this.howSupplied = howSupplied; }
    public String getStorageAndHandling() { return storageAndHandling; }
    public void setStorageAndHandling(String storageAndHandling) { this.storageAndHandling = storageAndHandling; }
    public String getPatientMedicationInformation() { return patientMedicationInformation; }
    public void setPatientMedicationInformation(String patientMedicationInformation) { this.patientMedicationInformation = patientMedicationInformation; }
    public String getSplMedguide() { return splMedguide; }
    public void setSplMedguide(String splMedguide) { this.splMedguide = splMedguide; }
    public String getSplPatientPackageInsert() { return splPatientPackageInsert; }
    public void setSplPatientPackageInsert(String splPatientPackageInsert) { this.splPatientPackageInsert = splPatientPackageInsert; }
    public String getDrugReferences() { return drugReferences; }
    public void setDrugReferences(String drugReferences) { this.drugReferences = drugReferences; }
    public String getQuestions() { return questions; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setQuestions(String questions) { this.questions = questions; }
    public String getInformationForPatients() { return informationForPatients; }
    public void setInformationForPatients(String informationForPatients) { this.informationForPatients = informationForPatients; }
    public String getAskDoctorOrPharmacist() { return askDoctorOrPharmacist; }
    public void setAskDoctorOrPharmacist(String askDoctorOrPharmacist) { this.askDoctorOrPharmacist = askDoctorOrPharmacist; }
    public String getSafeHandlingWarning() { return safeHandlingWarning; }
    public void setSafeHandlingWarning(String safeHandlingWarning) { this.safeHandlingWarning = safeHandlingWarning; }
    public String getUserSafetyWarnings() { return userSafetyWarnings; }
    public void setUserSafetyWarnings(String userSafetyWarnings) { this.userSafetyWarnings = userSafetyWarnings; }
    public String getSplUnclassifiedSection() { return splUnclassifiedSection; }
    public void setSplUnclassifiedSection(String splUnclassifiedSection) { this.splUnclassifiedSection = splUnclassifiedSection; }
    public String getControlledSubstance() { return controlledSubstance; }
    public void setControlledSubstance(String controlledSubstance) { this.controlledSubstance = controlledSubstance; }
    public String getDrugAbuseAndDependence() { return drugAbuseAndDependence; }
    public void setDrugAbuseAndDependence(String drugAbuseAndDependence) { this.drugAbuseAndDependence = drugAbuseAndDependence; }
    public String getLaborAndDelivery() { return laborAndDelivery; }
    public void setLaborAndDelivery(String laborAndDelivery) { this.laborAndDelivery = laborAndDelivery; }
    public String getLaboratoryTests() { return laboratoryTests; }
    public void setLaboratoryTests(String laboratoryTests) { this.laboratoryTests = laboratoryTests; }
    public String getNonclinicalToxicology() { return nonclinicalToxicology; }
    public void setNonclinicalToxicology(String nonclinicalToxicology) { this.nonclinicalToxicology = nonclinicalToxicology; }
    public String getMicrobiology() { return microbiology; }
    public void setMicrobiology(String microbiology) { this.microbiology = microbiology; }
    public String getCarcinogenesisAndMutagenesisAndImpairmentOfFertility() { return carcinogenesisAndMutagenesisAndImpairmentOfFertility; }
    public void setCarcinogenesisAndMutagenesisAndImpairmentOfFertility(String carcinogenesisAndMutagenesisAndImpairmentOfFertility) { this.carcinogenesisAndMutagenesisAndImpairmentOfFertility = carcinogenesisAndMutagenesisAndImpairmentOfFertility; }
    public String getRecentMajorChanges() { return recentMajorChanges; }
    public void setRecentMajorChanges(String recentMajorChanges) { this.recentMajorChanges = recentMajorChanges; }
    public String getRisks() { return risks; }
    public void setRisks(String risks) { this.risks = risks; }
    public String getStopUse() { return stopUse; }
    public void setStopUse(String stopUse) { this.stopUse = stopUse; }
}