/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.jobs.service;

/**
 *
 * @author Kelsas
 */
public enum JobName {

    AUTO_CHECK_OUT_PATIENT("Auto Checking Out of Patient Expired Visit"),
    EXECUTE_REPORT_MAILING_JOBS("Execute Report Mailing Jobs"),;

    private final String name;

    private JobName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
