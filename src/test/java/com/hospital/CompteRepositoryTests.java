package com.hospital;


import com.hospital.entities.Compte;
import com.hospital.repository.CompteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class CompteRepositoryTests {


    @Autowired
    private CompteRepository compteRepository;

    @Test
    public void testSaveCompte(){
        Compte compte = new Compte("enzo","edougajean@gmail.com", true);
        compteRepository.save(compte);
        Compte compte1 =    compteRepository.findByUsername("enzo");
        assertNotNull(compte);
        assertEquals(compte1.getUsername(), compte.getUsername());
        assertEquals(compte1.getPassword(), compte.getPassword());
    }

    @Test
    public void testGetCompte() {

        Compte compte = new Compte("enzo","edougajean@gmail.com", true);
        compteRepository.save(compte);
        Compte compte1 = compteRepository.findByUsername("enzo");
        assertNotNull(compte);
        assertEquals(compte1.getUsername(), compte.getUsername());
        assertEquals(compte1.getPassword(), compte.getPassword());
    }

    @Test
    public void findAllCompte(){
        Compte compte = new Compte("enzo","edougajean@gmail.com", true);
        compteRepository.save(compte);
        assertNotNull(compteRepository.findAll());

    }

    @Test
    public void deleteByCompteIdTest(){
        Compte compte = new Compte("enzo","edougajean@gmail.com", true);
        Compte compte1 =  compteRepository.save(compte);
        compteRepository.deleteById(compte1.getId());
    }


    @Test
    public void testCompteUpdate(){
        Compte compte = new Compte("enzo","edougajean@gmail.com", true);
        Compte compte1 =  compteRepository.save(compte);
        Optional<Compte> optional = compteRepository.findById(compte1.getId());
        Compte compte2 = optional.get();
        compte2.setUsername("django");
        compte2.setPassword("enzo123");
        compteRepository.save(compte2);
    }
}
