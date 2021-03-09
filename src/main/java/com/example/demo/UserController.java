package com.example.demo;

import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public Iterable<User> all() {
        return this.repository.findAll();
    }

    @PostMapping("")
    public User create(@RequestBody User user) {
        return this.repository.save(user);
    }

    @GetMapping("/{id}")
    public Object getUser(@PathVariable Long id){
        return this.repository.existsById(id) ?
                this.repository.findById(id) : "This Id does not exist";

    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody User user, @PathVariable Long userId){
        if(this.repository.existsById(userId)){
            User oldUser = this.repository.findById(userId).get();
            oldUser.setEmail(user.getEmail());
            oldUser.setPassword(user.getPassword());
            return this.repository.save(oldUser);
        }else{
            return this.repository.save(user);
        }

    }

    @DeleteMapping("/{id}")
    public Map<String, Long> deleteUser(@PathVariable Long id){
        this.repository.deleteById(id);
        return Map.of("count", this.repository.count());
    }

    @PostMapping("/authenticate")
    public Object authenticate(@RequestBody Map<String, String> userMap){
        List<User> matches = this.repository.findByEmail(userMap.get("email"));
        for(User u : matches){
            if(u.getPassword().equals(userMap.get("password"))){
                return Map.of("authenticated", true, "user", u);
            }else{
                return Map.of("authenticated", false);
            }
        }
        return null;
    }
}
