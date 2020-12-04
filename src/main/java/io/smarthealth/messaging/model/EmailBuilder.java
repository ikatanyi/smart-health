/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.messaging.model;

import org.springframework.context.MessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 *
 * @author Kelsas
 */
public class EmailBuilder {

    private String subject;
    private String mailTo;
    private String mailFrom;
    private String template;
    private Object attachment;
    private final Context context;
    private final SpringTemplateEngine templateEngine;

    public EmailBuilder() {
        this.mailTo = "";
        this.mailFrom = "";
        this.subject = "";
        this.template = "";
        this.attachment = null;
        this.context = new Context();
        templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());

    }

    public EmailBuilder Subject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailBuilder To(String to) {
        this.mailTo = to;
        return this;
    }

    public EmailBuilder From(String from) {
        this.mailFrom = from;
        return this;
    }

    public EmailBuilder Template(String template) {
        this.template = template;
        return this;
    }

    public EmailBuilder Attachment(Object attachment) {
        this.attachment = attachment;
        return this;
    }

    public EmailBuilder AddContext(String key, String value) {
        context.setVariable(key, value);
        return this;
    }

    public EmailBuilder AddContext(String key, Object value) {
        context.setVariable(key, value);
        return this;
    }

    public Mail createMail() throws IllegalArgumentException {
        //Check state of the mails.
        if (this.mailTo.isEmpty() || this.mailFrom.isEmpty()) {
            throw new IllegalArgumentException("Missing mail headers");
        }
        //select template
        String content = templateEngine.process(this.template, context);

        //Build mail object
        Mail result = new Mail();
        result.setMailTo(this.mailTo);
        result.setMailFrom(this.mailFrom);
        result.setMailContent(content);
        result.setMailSubject(this.subject);
        result.setAttachment(attachment);

        return result;
    }

    private ITemplateResolver htmlTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/mail");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF8");
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
