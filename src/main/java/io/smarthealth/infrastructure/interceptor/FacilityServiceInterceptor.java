/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Simon.Waweru
 */
@Component
public class FacilityServiceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // System.out.println("Pre Handle method is Calling");
//        final Map<String, String> pathVariables = (Map<String, String>) request
//                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//
//        System.out.println("Request parm " + request.getParameter("facilityid"));
//        System.out.println("pathVariables " + pathVariables.toString());
//        final String facilityid = String.valueOf(pathVariables.get("facilityid"));
//        System.out.println("facilityid " + facilityid);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
//        final Map<String, String> pathVariables = (Map<String, String>) request
//                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//        final String facilityid = String.valueOf(pathVariables.get("facilityid"));
//        System.out.println("facilityid " + facilityid);
        // System.out.println("Post Handle method is Calling");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
//        final Map<String, String> pathVariables = (Map<String, String>) request
//                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//        final String facilityid = String.valueOf(pathVariables.get("facilityid"));
//        System.out.println("facilityid " + facilityid);
        //System.out.println("Request and Response is completed");
    }
}
