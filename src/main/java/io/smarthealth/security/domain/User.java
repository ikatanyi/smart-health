package io.smarthealth.security.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.security.data.UserData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.smarthealth.security.service.UserPhoneNumber;

/**
 * Authentication User
 *
 * @author Kelsas
 */
@Entity
@Table(name = "auth_user")
public class User extends Identifiable implements UserDetails, UserPhoneNumber {

    private String email;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name;

    private boolean enabled;

    @Column(name = "account_locked")
    private boolean accountNonLocked;

    @Column(name = "account_expired")
    private boolean accountNonExpired;

    @Column(name = "credentials_expired")
    private boolean credentialsNonExpired;

    private boolean verified;

    private LocalDateTime lastLogin;

    private boolean firstTimeLogin;

    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "auth_role_user",
            joinColumns = {
                @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_auth_user_user_id"))},
            inverseJoinColumns = {
                @JoinColumn(name = "role_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_auth_user_roles_id"))})
    private Set<Role> roles;

    @OneToMany(mappedBy = "excessAmountAuthorisedBy")
    private List<PaymentDetails> paymentDetailss;

    public User() {
        this.enabled = true;
        this.firstTimeLogin = true;
    }

    public User(String email, String username, String password, String name, Set<Role> roles) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.roles = roles;
        this.enabled = true;
        this.firstTimeLogin = true;
    }

    public User(String email, String username, String name) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.enabled = true;
        this.firstTimeLogin = true;
    }

    public User(String email, String username, String password, String name) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;

        this.enabled = true;
        this.firstTimeLogin = true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountNonLocked;
    }

    /*
	 * Get roles and permissions and add them as a Set of GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        roles.forEach(r -> {
            authorities.add(new SimpleGrantedAuthority(r.getName()));
            r.getPermissions()
                    .forEach(p -> {
                        authorities.add(new SimpleGrantedAuthority(p.getName()));
                    });
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UserData toData() {
        UserData data = new UserData();
        data.setId(this.getId());
        data.setAccount_expired(this.accountNonExpired);
        data.setAccount_locked(this.accountNonLocked);
        data.setCredentials_expired(this.credentialsNonExpired);
        data.setEmail(this.email);
        data.setEnabled(this.enabled);
        data.setLastLogin(this.lastLogin);
        data.setName(this.name);
        data.setFirstTimeLogin(this.firstTimeLogin);
        data.setPhoneNumber(this.phoneNumber);
//        data.setPassword(this.password);
        List<String> rolelist = new ArrayList<>();
        this.roles
                .forEach(x -> rolelist.add(x.getName()));
        data.setRoles(rolelist);
        data.setUsername(this.username);
        data.setVerified(this.verified);
        return data;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
