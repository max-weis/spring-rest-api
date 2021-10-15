package dev.funsociety.restapi.boundry.rest;

import dev.funsociety.restapi.control.UserRepository;
import dev.funsociety.restapi.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Find all users")
    @GetMapping
    public List<User> findAllUsers() {
        log.info("Find all users");
        return (List<User>) repository.findAll();
    }

    @Operation(summary = "Find user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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

    @Operation(summary = "Create a new user")
    @PostMapping
    public User saveUser(@Validated @RequestBody User user) {
        log.info("Create user with name: {}", user.getName());
        kafkaTemplate.send("user", user.getName());
        return repository.save(user);
    }
}
