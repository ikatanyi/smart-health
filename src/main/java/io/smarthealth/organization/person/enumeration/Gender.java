package io.smarthealth.organization.person.enumeration;

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
    F;

    public String value() {
        return name();
    }

    public static Gender fromValue(String v) {
        return valueOf(v);
    }
}
