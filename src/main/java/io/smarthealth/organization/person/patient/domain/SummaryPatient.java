package io.smarthealth.organization.person.patient.domain;

import io.smarthealth.organization.person.data.PersonData;
import org.hibernate.annotations.Formula;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.Objects;

@Entity
//@Table(name = "person")
public class SummaryPatient {
    private Long id;

//    @javax.persistence.Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    private int total;
    private int male_under_5;
    private int male_above_5;
    private int female_under_5;
    private int female_above_5;

    public SummaryPatient(){super();}
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMale_under_5() {
        return male_under_5;
    }

    public void setMale_under_5(int male_under_5) {
        this.male_under_5 = male_under_5;
    }

    public int getMale_above_5() {
        return male_above_5;
    }

    public void setMale_above_5(int male_above_5) {
        this.male_above_5 = male_above_5;
    }

    public int getFemale_under_5() {
        return female_under_5;
    }

    public void setFemale_under_5(int female_under_5) {
        this.female_under_5 = female_under_5;
    }

    public int getFemale_above_5() {
        return female_above_5;
    }

    public void setFemale_above_5(int female_above_5) {
        this.female_above_5 = female_above_5;
    }
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 79 * hash + Objects.hashCode(this.id);
//        hash = 79 * hash + Objects.hashCode(this.total);
//        hash = 79 * hash + this.male_under_5;
//        return hash;
//    }
}
