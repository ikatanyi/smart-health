/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * @author Simon.Waweru
 */
public interface RoomTypeRepository extends JpaRepository<RoomType, Object> {
    Optional<RoomType> findByTypeCode(String code );
}
