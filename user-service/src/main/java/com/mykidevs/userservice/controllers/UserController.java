package com.mykidevs.userservice.controllers;


import com.mykidevs.userservice.dto.requests.UserCreateRequest;
import com.mykidevs.userservice.dto.requests.UserPagePaginationRequest;
import com.mykidevs.userservice.dto.responses.UserResponse;
import com.mykidevs.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public final Page<UserResponse> getAllUsersWithPage(@Valid UserPagePaginationRequest request) {
        return userService.getAllUsersWithPagePagination(request);
    }

    @GetMapping("/verify-token")
    //simple return entity for verification process
    public final ResponseEntity<String> verifyToken(@RequestParam(name = "token") String token) {
        if(!userService.verifyToken(token)) return ResponseEntity.badRequest().body("Invalid Token!");
        return ResponseEntity.ok("Token was successfully validated!");
    }

    @PostMapping("/new")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final UserResponse createUser(@Valid @RequestBody UserCreateRequest dto) {
        return userService.create(dto);
    }

    @GetMapping("/{id}")
    public final UserResponse getUser(@PathVariable Long id) {
        return userService.get(id);
    }

    @DeleteMapping("/{id}")
    public final ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
