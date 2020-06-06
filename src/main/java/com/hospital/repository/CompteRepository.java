package com.hospital.repository;

import com.hospital.entities.Compte;
import com.hospital.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.util.Optional;
=======
import java.util.Collection;
import java.util.List;
>>>>>>> owner-developer

@Repository
public interface CompteRepository  extends JpaRepository<Compte, Long> {

    Compte findByUsername(String username);
    List<Compte> findByRolesName(String roleName);
   Compte findByPassword(String password);
   Compte findByEmail(String email);


}
