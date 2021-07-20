package io.smarthealth.accounting.billing.data.nue;

import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;

import java.util.ArrayList;
import java.util.List;

public class BillItemListToDataConverter {
    public static List<BillItemData> billItemDataConverter(List<PatientBillItem> patientBillItems) {
        List<BillItemData> billItemData = new ArrayList<>();
        for (PatientBillItem b : patientBillItems) {
            //ignore for now the receipts and copay from limit checks
            if (b.getItem().getCategory() != ItemCategory.CoPay || b.getItem().getCategory() != ItemCategory.Receipt) {
                continue;
            }
            billItemData.add(b.toData());
        }
        return billItemData;
    }
}
