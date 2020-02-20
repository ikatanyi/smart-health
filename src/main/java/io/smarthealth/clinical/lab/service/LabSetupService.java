/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.ContainerData;
import io.smarthealth.clinical.lab.data.DisciplineData;
import io.smarthealth.clinical.lab.data.SpecimenData;
import io.smarthealth.clinical.lab.domain.Container;
import io.smarthealth.clinical.lab.domain.ContainerRepository;
import io.smarthealth.clinical.lab.domain.Discipline;
import io.smarthealth.clinical.lab.domain.DisciplineRepository;
import io.smarthealth.clinical.lab.domain.Specimen;
import io.smarthealth.clinical.lab.domain.SpecimenRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class LabSetupService {

    private final ModelMapper modelMapper;
  private final DisciplineRepository disciplineRepository;
    private final ContainerRepository containerRepository;
    private final SpecimenRepository specimenRepository;

    @Transactional
    public List<SpecimenData> createSpecimens(List<SpecimenData> specimenDatalist) {

        List<Specimen> specs = new ArrayList();
        for (SpecimenData specData : specimenDatalist) {
            Specimen specimen = SpecimenData.map(specData);
            Optional<Container> cont = containerRepository.findById(specData.getContainerId());

            if (cont.isPresent()) {
                specimen.setContainer(cont.get());
            }
            specs.add(specimen);
        }

        List<Specimen> saved = specimenRepository.saveAll(specs);

        List<SpecimenData> savedlist = new ArrayList<>();
        saved.forEach((s) -> {
            savedlist.add(SpecimenData.map(s));
        });
        return savedlist;
    }

    @Transactional
    public List<ContainerData> createContainers(List<ContainerData> containerData) {
        java.lang.reflect.Type listType = new TypeToken<List<Container>>() {
        }.getType();
        List<Container> containers = modelMapper.map(containerData, listType);

        List<Container> containerList = containerRepository.saveAll(containers);
        return modelMapper.map(containerList, new TypeToken<List<ContainerData>>() {
        }.getType());
    }

    public Page<ContainerData> fetchAllContainers(Pageable pgbl) {
        return containerRepository.findAll(pgbl).map(p -> convertContainerToData(p));
    }

    public ContainerData fetchContainerById(Long id) {
        return containerRepository.findById(id).map(p -> convertContainerToData(p)).orElseThrow(() -> APIException.notFound("Container identified by {0} not found.", id));
    }

    public void deleteContainerById(Long id) {
        containerRepository.deleteById(id);
    }

    public ContainerData convertContainerToData(Container container) {
        ContainerData containerData = modelMapper.map(container, ContainerData.class);
        return containerData;
    }

    public Container convertDataToContainer(ContainerData containerData) {
        Container container = modelMapper.map(containerData, Container.class);
        return container;
    }

    public Page<SpecimenData> fetchAllSpecimens(Pageable pgbl) {
        return specimenRepository.findAll(pgbl).map(p -> SpecimenData.map(p));
    }

    public Specimen fetchSpecimenById(Long id) {
        return specimenRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Specimen identified by {0} not found.", id));//.map(p -> SpecimenData.map(p)).orElseThrow(() -> APIException.notFound("Specimen identified by {0} not found.", id));
    }

    public void deleteTestById(Long id) {
        specimenRepository.deleteById(id);
    }
 public void deleteDisciplineById(Long id) {
        disciplineRepository.deleteById(id);
    }
 @Transactional
    public List<DisciplineData> createDisciplines(List<DisciplineData> disciplineData) {

        java.lang.reflect.Type listType = new TypeToken<List<Discipline>>() {
        }.getType();
        List<Discipline> disciplines = modelMapper.map(disciplineData, listType);

        List<Discipline> disciplineList = disciplineRepository.saveAll(disciplines);
        return modelMapper.map(disciplineList, new TypeToken<List<DisciplineData>>() {
        }.getType());
    }

    public Page<DisciplineData> fetchAllDisciplines(Pageable pgbl) {
        return disciplineRepository.findAll(pgbl).map(p -> convertDisciplineToData(p));
    }

    public DisciplineData fetchDisciplineById(Long id) {
        return disciplineRepository.findById(id).map(p -> convertDisciplineToData(p)).orElseThrow(() -> APIException.notFound("Discipline identified by {0} not found.", id));
    }
    public DisciplineData convertDisciplineToData(Discipline discipline) {
        DisciplineData disciplineData = modelMapper.map(discipline, DisciplineData.class);
        return disciplineData;
    }

    public Discipline convertDataToSpecimen(DisciplineData disciplineData) {
        Discipline discipline = modelMapper.map(disciplineData, Discipline.class);
        return discipline;
    }
    
}
