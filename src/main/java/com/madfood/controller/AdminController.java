package com.madfood.controller;

import com.madfood.entity.User;
import com.madfood.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8000"})
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            User u = user.get();
            u.setIsActive(false);
            userService.updateUser(id, u);
            return ResponseEntity.ok("User blocked successfully");
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            User u = user.get();
            u.setIsActive(true);
            userService.updateUser(id, u);
            return ResponseEntity.ok("User unblocked successfully");
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
