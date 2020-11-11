package io.smarthealth.notify.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface TextMessageRepository extends JpaRepository<TextMessage, Long>, JpaSpecificationExecutor<TextMessage> {

}
