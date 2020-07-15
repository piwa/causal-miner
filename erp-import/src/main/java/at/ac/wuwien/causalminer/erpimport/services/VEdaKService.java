package at.ac.wuwien.causalminer.erpimport.services;

import at.ac.wuwien.causalminer.erpimport.repositories.VEdaKRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class VEdaKService {

    @Autowired
    private VEdaKRepository vEdaKRepository;

    public List<VEdaKDto> findAll() {
        return StreamSupport.stream(vEdaKRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

}
