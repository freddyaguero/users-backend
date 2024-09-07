package com.springboot.backend.userapp.users_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.userapp.users_backend.entities.Role;
import com.springboot.backend.userapp.users_backend.entities.User;
import com.springboot.backend.userapp.users_backend.models.IUser;
import com.springboot.backend.userapp.users_backend.models.UserRequest;
import com.springboot.backend.userapp.users_backend.repositories.RoleRepository;
import com.springboot.backend.userapp.users_backend.repositories.UserRepository;

@Service
public class UserServiceImpl  implements UserService{


    private UserRepository repository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository ) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    @Transactional(readOnly=true)
    public List<User> findAll() {
       return (List) this.repository.findAll();
    }

    @Override
    @Transactional(readOnly=true)
    public Page<User> findAll(Pageable pageable){
        return this.repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly=true)
    public Optional<User> findById(Long id) {
       return repository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        List<Role> roles = getRoles(user);

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
       return repository.save(user);
    }

    

    @Override
    @Transactional
    public Optional<User> update(UserRequest user, Long id){
        Optional<User> userOptional = repository.findById(id);

        if(userOptional.isPresent()){
            User userDb = userOptional.get();
            userDb.setEmail(user.getEmail());
            userDb.setLastname(user.getLastname());
            userDb.setName(user.getName());
            userDb.setUsername(user.getUsername());

            List<Role> roles = getRoles(user);
    
            userDb.setRoles(roles);
            repository.save(userDb);
            return Optional.of(userDb);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private List<Role> getRoles(IUser user) {
        List<Role> roles = new ArrayList<>();

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        optionalRoleUser.ifPresent(role -> roles.add(role));
        //optionalRoleUser.ifPresent(roles::add);

        if (user.isAdmin()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }
        return roles;
    }
}
