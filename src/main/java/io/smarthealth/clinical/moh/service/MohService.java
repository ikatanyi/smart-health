package io.smarthealth.clinical.moh.service;

import io.smarthealth.clinical.moh.data.MohData;
import io.smarthealth.clinical.moh.domain.Moh;
import io.smarthealth.clinical.moh.domain.MohRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.ikatanyi
 */
@Service
public class MohService {

    @Autowired
    MohRepository mohRepository;

    @Autowired
    ModelMapper modelMapper;

    public Moh fetchMohById(final Long mohId) {
        return mohRepository.findById(mohId).orElseThrow(() -> APIException.notFound("Moh identified by  {0} not found", mohId));
    }

    public Optional<Moh> findMohById(Long id) {
        return mohRepository.findById(id);
    }

    public Moh getMohWithNoFoundDetection(Long id) {
        return mohRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Moh identified by  {0} not found", id));
    }

    @Transactional
    public Moh createMoh(final MohData mohData) {
        Optional<Moh> name = getMohByName(mohData.getDescription());
        if (name.isPresent()) {
            APIException.badRequest("This diagnosis already exists", "");
        }
        Moh moh = modelMapper.map(mohData, Moh.class);
        mohRepository.save(moh);
        return moh;
    }

    @Transactional
    public List<Moh> createBatchMoh(final List<MohData> mohDataArr) {
        List<Moh> mohArr = new ArrayList();

        for (MohData mohData : mohDataArr) {
            Optional<Moh> name = getMohByName(mohData.getDescription());
            if (name.isPresent()) {
                APIException.badRequest("This diagnosis already exists", "");
            }
            Moh moh = modelMapper.map(mohData, Moh.class);
            mohArr.add(moh);
        }

        
        return mohRepository.saveAll(mohArr);
    }

    @Transactional
    public Moh updateMoh(final Long mohId, final MohData mohData) {
        Moh moh = getMohWithNoFoundDetection(mohId);
        modelMapper.map(mohData, moh);
        mohRepository.save(moh);
        return moh;
    }

    public Page<Moh> fetchAllMoh(final Pageable pgbl) {
        return mohRepository.findAll(pgbl);
    }

    public Optional<Moh> getMohByName(String name) {
        return mohRepository.findByDescriptionContainingIgnoreCase(name);
    }

    public List<MohData> getAllMohs() {
        Page<MohData> list = mohRepository.findAll(Pageable.unpaged()).map(um -> um.toData());
        return list.getContent();
    }
}
