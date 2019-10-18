/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author Kelsas
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceBookData {
//    "pricebook_id": "2022841000000070001",
//      "name": "General Corporate ",
//      "description": "General Corporate rates",
//      "currency_id": "",
//      "currency_code": "",
//      "status": "active",
//      "pricebook_type": "fixed_percentage",
//      "sales_or_purchase_type": "sales",
//      "percentage": 10,
//      "is_increase": true,
//      "rounding_type": "no_rounding",
//      "rounding_type_formatted": "No Rounding",
//      "decimal_place": 0
}
