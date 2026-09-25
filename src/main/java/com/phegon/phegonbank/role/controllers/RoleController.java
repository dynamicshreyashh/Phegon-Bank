package com.phegon.phegonbank.role.controllers;
import com.phegon.phegonbank.res.Response;
import com.phegon.phegonbank.role.entity.Role;
import com.phegon.phegonbank.role.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/roles") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class RoleController {
 private final RoleService service;
 @PostMapping public Response<Role> create(@RequestBody Role role){return service.createRole(role);}
 @PutMapping public Response<Role> update(@RequestBody Role role){return service.updateRole(role);}
 @GetMapping public Response<List<Role>> all(){return service.getAllRoles();}
 @DeleteMapping("/{id}") public Response<?> delete(@PathVariable Long id){return service.deleteRole(id);}
}
