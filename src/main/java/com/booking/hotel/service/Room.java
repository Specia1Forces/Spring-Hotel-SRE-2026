package com.booking.hotel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class Room {
    /*
    private final RoomRepository roomRepository;

    @Autowired
    public Room(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Contracts findOne(int id){
        Optional<Contracts> foundPerson = roomRepository.findById(id);
        return foundPerson.orElse(null);
    }

    public List<Contracts> findAll(){
        return roomRepository.findAll();
    }
    //
    public Contracts saveService(Contracts contracts){
        return roomRepository.save(contracts);
    }

    public void deleteById(int id){
        roomRepository.deleteById(id);
    }

    @Transactional
    public void save(Contracts contracts) {
        roomRepository.save(contracts);
    }


    @Transactional
    public void delete(int id) {
        roomRepository.deleteById(id);
    }

    @Transactional
    public void update(int id,Contracts updatedContracts) {
        updatedContracts.setContractNumber(id);
        roomRepository.save(updatedContracts);
    }

     */
}
