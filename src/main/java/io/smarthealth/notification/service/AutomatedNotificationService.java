/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.notification.data.AutomatedNotificationData;
import io.smarthealth.notification.domain.AutomatedNotification;
import io.smarthealth.notification.domain.AutomatedNotificationRepository;
import io.smarthealth.security.domain.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class AutomatedNotificationService {

    private final AutomatedNotificationRepository repository;
    private final UserRepository userRepository;

    public AutomatedNotificationService(AutomatedNotificationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }
  @Transactional
    public AutomatedNotification create(AutomatedNotificationData data) {
        AutomatedNotification notice = new AutomatedNotification();
        notice.setActive(data.isActive());
        notice.setNotificationParameter(data.getNotificationParameter());
        notice.setNotificationType(data.getNotificationType());
        data.getUsers().stream()
                .filter(x -> x.getUserId() != null)
                .map(usr -> userRepository.findById(usr.getUserId()).orElse(null))
                .filter(y -> y != null)
                .forEach(user -> notice.addUser(user));
        return repository.save(notice);
    }
 @Transactional
    public AutomatedNotification update(Long id, AutomatedNotificationData data) {
        AutomatedNotification notice = get(id);

        notice.setActive(data.isActive());
        notice.setNotificationParameter(data.getNotificationParameter());
        notice.setNotificationType(data.getNotificationType());
        data.getUsers().stream()
                .filter(x -> x.getUserId() != null)
                .map(usr -> userRepository.findById(usr.getUserId()).orElse(null))
                .filter(y -> y != null)
                .forEach(user -> notice.addUser(user));
        return repository.save(notice);
    }

    public AutomatedNotification get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Automated Notification with ID {0} Not Found", id));
    }
     public Page<AutomatedNotification> get(Pageable page){
         return repository.findAll(page);
         
     }
     @Transactional
    public void delete(Long id){
        AutomatedNotification notice = get(id);
        repository.delete(notice);
    }

}
