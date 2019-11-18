/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.data;

import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class BankBranchData {

    private String branchName;
    private String branchAddress;
    private String swiftCode;
    private String branchContact;
    private String branchEmail;
    private Long branchId;

}
