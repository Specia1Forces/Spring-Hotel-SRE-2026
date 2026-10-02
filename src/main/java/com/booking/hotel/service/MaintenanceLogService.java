package com.booking.hotel.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MaintenanceLogService {/*
    private final MaintenanceLogRepository maintenanceLogRepository;
    @Autowired
    public MaintenanceLogService(MaintenanceLogRepository maintenanceLogRepository) {
        this.maintenanceLogRepository = maintenanceLogRepository;
    }




    public Maids findOne(int id){
        Optional<Maids> foundMaids =  maintenanceLogRepository.findById(id);
        return foundMaids.orElse(null);
    }

    public List<Maids> findAll(){
        return maintenanceLogRepository.findAll();
    }
    //
    public Maids saveService(Maids maids){
        return  maintenanceLogRepository.save(maids);
    }

    public void deleteById(int id){
        maintenanceLogRepository.deleteById(id);
    }

    @Transactional
    public void save(Maids maids) {
        maintenanceLogRepository.save(maids);
    }


    @Transactional
    public void delete(int id) {
        maintenanceLogRepository.deleteById(id);
    }

    @Transactional
    public void update(int id, Maids updatedMaids) {
        updatedMaids.setMaidId(id);
        maintenanceLogRepository.save(updatedMaids);
    }
    */
}
