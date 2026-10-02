package com.booking.hotel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DocumentationPersonality {
    /*
    private final  DocumentationPersonality  documentationPersonality;
    @Autowired
    public DocumentationPersonality(DocumentationPersonality documentationPersonality) {
        this.documentationPersonality = documentationPersonality;
    }

    public Contracts findOne(int id){
        Optional<Contracts> foundPerson = documentationPersonality.findById(id);
        return foundPerson.orElse(null);
    }

    public List<Contracts> findAll(){
        return documentationPersonality.findAll();
    }
    //
    public Contracts saveService(Contracts contracts){
        return documentationPersonality.save(contracts);
    }

    public void deleteById(int id){
        documentationPersonality.deleteById(id);
    }

    @Transactional
    public void save(Contracts contracts) {
        documentationPersonality.save(contracts);
    }


    @Transactional
    public void delete(int id) {
        documentationPersonality.deleteById(id);
    }

    @Transactional
    public void update(int id,Contracts updatedContracts) {
        updatedContracts.setContractNumber(id);
        documentationPersonality.save(updatedContracts);
    }

     */
}
