package io.smarthealth.organization.person.domain.enumeration;

/**
 *
 * @author Kelsas
 */
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Gender {
    /**
     * Male
     *
     */
    M,

    /**
     * Female
     *
     */
    F,
    /**
     * Other
     *
     */
    O;
    public String value() {
        return name();
    }

    public static Gender fromValue(String v) {
        return valueOf(v);
    }

    public String getFormattedValue(){
       if(this.name().equals("M")){
           return "Male";
       }else if(this.name().equals("F")){
           return "Female";
       }else
           return "Others";
    }
}
