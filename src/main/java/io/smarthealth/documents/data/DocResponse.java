/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.documents.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DocResponse {

    private String documentId;
    private String documentName;
    private String url;
    private String fileType;
}
