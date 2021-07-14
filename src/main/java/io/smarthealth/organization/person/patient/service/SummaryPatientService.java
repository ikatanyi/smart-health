package io.smarthealth.organization.person.patient.service;

import io.smarthealth.organization.person.domain.Person;
import io.smarthealth.organization.person.patient.domain.SummaryPatient;
import io.smarthealth.organization.person.patient.domain.SummaryPatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SummaryPatientService implements ISummaryPatientService{
    @Autowired
    private SummaryPatientRepository repo;
//    public int fetchMale(){return repo.findMale();}
//    public int fetchFemale(){return repo.findFemale();}

    public List<Person> fetchAllPa(){return (List<Person>) repo.findAll();}

    @Override
    public int findTotal() {
        return repo.findTotal();
    }

    @Override
    public int findMaleUnder5() {
        return repo.findMaleUnder5();
    }

    @Override
    public int findMaleAbove5() {
        return repo.findMaleAbove5();
    }

    @Override
    public int findFemaleUnder5() {
        return repo.findFemaleUnder5();
    }

    @Override
    public int findFemaleAbove5() {
        return repo.findFemaleAbove5();
    }

}
