package io.smarthealth.clinical.visit.domain;

import io.smarthealth.infrastructure.domain.Identifiable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "visit_temp_limit")
public class TempLimit extends Identifiable {

}
