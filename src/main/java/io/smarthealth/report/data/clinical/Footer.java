/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import io.smarthealth.organization.facility.domain.Facility;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
public class Footer {
    private String msg;
    
    public static Footer map(Facility facility) {
        Footer footer = new Footer();
        footer.setMsg(facility.getFooterMsg());
        return footer;
    }
}
