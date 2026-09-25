package com.phegon.phegonbank.auth_users.controllers;
import com.phegon.phegonbank.auth_users.dtos.*;
import com.phegon.phegonbank.auth_users.services.UserService;
import com.phegon.phegonbank.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
 private final UserService service;
 @GetMapping("/me") public Response<UserDTO> me(){return service.getMe();}
 @GetMapping public Response<List<UserDTO>> users(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.getUsers(page,size);}
 @PutMapping("/password") public Response<UserDTO> password(@Valid @RequestBody UpdatePasswordRequest r){return service.updatePassword(r);}
 @PostMapping("/profile-picture") public Response<UserDTO> picture(@RequestParam("file") MultipartFile file){return service.uploadProfilePicture(file);}
}
