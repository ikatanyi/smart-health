/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.lang;

/**
 *
 * @author Kelsas
 */
public class LinkUtil {
     public static final String REL_COLLECTION = "collection";
    public static final String REL_NEXT = "next";
    public static final String REL_PREV = "prev";
    public static final String REL_FIRST = "first";
    public static final String REL_LAST = "last";

    private LinkUtil() { 
    }

    //

    /**
     * Creates a Link Header to be stored in the {@link HttpServletResponse} to provide Discoverability features to the user
     * 
     * @param uri
     *            the base uri
     * @param rel
     *            the relative path
     * 
     * @return the complete url
     */
    public static String createLinkHeader(final String uri, final String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
