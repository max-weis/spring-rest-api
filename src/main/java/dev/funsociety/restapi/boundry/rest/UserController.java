package dev.funsociety.restapi.boundry.rest;

import dev.funsociety.restapi.control.UserRepository;
import dev.funsociety.restapi.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private UserRepository repository;

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Find all users");
        return (List<User>) repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable(value = "id") long id) {
        Optional<User> user = repository.findById(id);

        if (user.isPresent()) {
            log.info("User with id {} found", id);
            return ResponseEntity.ok().body(user.get());
        } else {
            log.warn("User with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public User saveUser(@Validated @RequestBody User user) {
        log.info("Create user with name: {}", user.getName());
        kafkaTemplate.send("user", user.getName());
        return repository.save(user);
    }
}
