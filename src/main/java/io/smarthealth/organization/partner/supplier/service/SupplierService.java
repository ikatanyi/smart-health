/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.partner.supplier.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.partner.supplier.domain.Supplier;
import io.smarthealth.organization.partner.supplier.domain.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class SupplierService {

    @Autowired
    SupplierRepository supplierRepository;

    public Supplier createSupplier(Supplier supplier) {
        try {
            return supplierRepository.saveAndFlush(supplier);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error creating supplier ", e.getMessage());
        }
    }

    public Page<Supplier> fetchSuppliersList(Pageable pgbl) {
        try {
            return supplierRepository.findAll(pgbl);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error fetching suppliers list", e.getMessage());
        }
    }

    public Supplier fetchSupplierById(String supplierId) {
        try {
            return supplierRepository.findById(supplierId).orElseThrow(() -> APIException.notFound("Supplier ID {0} not found.", supplierId));
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error fetching supplier id " + supplierId, e.getMessage());
        }
    }

}
