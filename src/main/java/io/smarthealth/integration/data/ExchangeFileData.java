package io.smarthealth.integration.data;


import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ExchangeFileData  {

    private Long id;
    private String globalID;
    private String memberNr;
    private Long admitId;
    private Long progressFlag;
    private String rejectionReason;
    private Long exchangeType;
    private Long inOutType;
    private String locationId;
    private LocalDate smartDate;
    private byte[] smartFile;
    private LocalDate exchangeDate;
    private String exchangeFile;
    private LocalDate resultDate;
    private String resultFile;
    
//    public static ExchangeFileData map(ExchangeFile exchangeFile){
//        ExchangeFileData data=new ExchangeFileData();
//        data.setAdmitId(exchangeFile.getAdmitId());
////        data.setExchangeDate(exchangeFile.getExchangeDate());
//        data.setExchangeFile(exchangeFile.getExchangeFile());
//        data.setExchangeType(exchangeFile.getExchangeType());
//        data.setGlobalID(exchangeFile.getGlobalId());
//        data.setId(exchangeFile.getId());
//        data.setInOutType(exchangeFile.getInoutType());
//        data.setLocationId(exchangeFile.getLocationId());
//        data.setMemberNr(exchangeFile.getMemberNr());
//        data.setProgressFlag(exchangeFile.getProgressFlag());
//        data.setRejectionReason(exchangeFile.getRejectionReason());
//        data.setResultDate(exchangeFile.getResultDate());
//        data.setSmartDate(exchangeFile.getSmartDate());
//        data.setExchangeFile(exchangeFile.getResultFile());
//        data.setSmartFile(exchangeFile.getSmartFile());
//        return data;
//
//    }
}
