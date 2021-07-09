package io.smarthealth.organization.person.patient.service;

public interface ISummaryPatientService {
    int findTotal();
    int findMaleUnder5();
    int findMaleAbove5();
    int findFemaleUnder5();
    int findFemaleAbove5();
}
