package com.booking.hotel.service;


import com.booking.hotel.models.ClientOfHotel;
import com.booking.hotel.repositories.ClientOfHotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClientOfHotelService {
    private final ClientOfHotelRepository clientOfHotelRepository;
    @Autowired

    public ClientOfHotelService(ClientOfHotelRepository clientOfHotelRepository) {
        this.clientOfHotelRepository = clientOfHotelRepository;
    }

    public ClientOfHotel findOne(int id){
        Optional<ClientOfHotel> foundPerson = clientOfHotelRepository.findById(id);
        return foundPerson.orElse(null);
    }

    public List<ClientOfHotel> findAll(){
        return clientOfHotelRepository.findAll();
    }
    //
    public ClientOfHotel saveService(ClientOfHotel clientOfHotel){
        return clientOfHotelRepository.save(clientOfHotel);
    }

    public void deleteById(int id){
        clientOfHotelRepository.deleteById(id);
    }

    @Transactional
    public ClientOfHotel save(ClientOfHotel clientOfHotel) {
        return clientOfHotelRepository.save(clientOfHotel);
    }


    @Transactional
    public void delete(int id) {
        clientOfHotelRepository.deleteById(id);
    }

    @Transactional
    public void update(int id,ClientOfHotel updatedClientOfHotel) {
        updatedClientOfHotel.setClientId(id);
        clientOfHotelRepository.save(updatedClientOfHotel);
    }
}
