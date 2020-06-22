/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Documented // are to be documented by javadoc
@AuthenticationPrincipal // Resolves Authentication.getPrincipal() to inject the authenticated user
@Retention(RetentionPolicy.RUNTIME) // Runtime retention
@Target({ElementType.PARAMETER, ElementType.TYPE}) // The contexts in which an annotation type is applicable
public @interface CurrentUser {
}
