//package io.smarthealth.tenant.api;
//
//import io.smarthealth.tenant.domain.Tenant;
//import io.smarthealth.tenant.domain.TenantRepository;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import javax.sql.DataSource;
//import org.flywaydb.core.Flyway;
//
//@RestController
//@RequestMapping(value = "/tenants")
//public class TenantController {
//
//    private final TenantRepository repository;
//
//    private final DataSource dataSource;
//
//    public TenantController(TenantRepository repository, DataSource dataSource) {
//        this.repository = repository;
//        this.dataSource = dataSource;
//    }
//
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.CREATED)
//    @Transactional
//    public Tenant createTenant(@RequestBody Tenant tenant) {
//        tenant = repository.save(tenant);
//        String schema = tenant.getSchemaName();
//        if (schema == null) {
//            throw new RuntimeException("schema is null");
//        }
//
//        Flyway flyway = Flyway.configure()
//                .locations("db/migration/tenants")
//                .dataSource(dataSource)
//                .schemas(schema)
//                .load();
//        flyway.migrate();
//
//        return tenant;
//    }
//
//    @DeleteMapping("/{uuid}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteTenant(@RequestParam String uuid) {
//        repository.deleteById(uuid);
//    }
//
//    @GetMapping
//    public Page<Tenant> getTenants(Pageable pageable) {
//        return repository.findAll(pageable);
//    }
//
//}
