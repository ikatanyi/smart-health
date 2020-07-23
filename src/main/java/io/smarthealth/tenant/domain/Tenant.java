//package io.smarthealth.tenant.domain;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//import org.hibernate.annotations.GenericGenerator;
//
// https://blog.aliprax.me/schema-based-multitenancy/
//import javax.persistence.*;
//
//@Entity
//@Table(name = "tenants", schema = "default_schema")
//public class Tenant implements Serializable {
//
//    @Id
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
//    private String uuid;
//
//    private String schemaName;
//
//    private String tenantName;
//
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
//
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }
//
//    public String getSchemaName() {
//        return schemaName;
//    }
//
//    public void setSchemaName(String schemaName) {
//        this.schemaName = schemaName;
//    }
//
//    public String getTenantName() {
//        return tenantName;
//    }
//
//    public void setTenantName(String tenantName) {
//        this.tenantName = tenantName;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//}
