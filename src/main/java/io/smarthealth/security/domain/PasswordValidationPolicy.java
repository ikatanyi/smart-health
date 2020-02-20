package io.smarthealth.security.domain;

 
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "auth_password_validation_policy")
public class PasswordValidationPolicy extends Identifiable {

    @Column(name = "regex", nullable = false)
    private String regex;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;

    public PasswordValidationPolicy(final String regex, final String description, final boolean active) {
        this.description = description;
        this.regex = regex;
        this.active = active;
    }

    public PasswordValidationPolicy() {
        this.active = false;
    }

    public String getDescription() {
        return description;
    }

    public String getRegex() {
        return regex;
    }

    public boolean getActive() {
        return this.active;
    }

    public Map<String, Object> activate() {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(1);

        final String active = "active";

        if (!this.active) {

            actualChanges.put(active, true);
            this.active = true;
        }

        return actualChanges;
    }

    public boolean isActive() {
        return this.active;
    }

    public void deActivate() {
        this.active = false;
    }

}