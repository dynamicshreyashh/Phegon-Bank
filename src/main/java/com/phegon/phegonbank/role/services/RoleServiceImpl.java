package com.phegon.phegonbank.role.services;

import com.phegon.phegonbank.exceptions.BadRequestException;
import com.phegon.phegonbank.exceptions.NotFoundException;
import com.phegon.phegonbank.res.Response;
import com.phegon.phegonbank.role.entity.Role;
import com.phegon.phegonbank.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
 private final RoleRepo repo;
 @Override public Response<Role> createRole(Role role){String name=normalize(role.getName());if(repo.findByName(name).isPresent())throw new BadRequestException("Role already exists");role.setName(name);return Response.<Role>builder().statusCode(201).message("Role created").data(repo.save(role)).build();}
 @Override public Response<Role> updateRole(Role role){if(role.getId()==null)throw new BadRequestException("Role id is required");Role current=repo.findById(role.getId()).orElseThrow(()->new NotFoundException("Role not found"));String name=normalize(role.getName());repo.findByName(name).filter(r->!r.getId().equals(role.getId())).ifPresent(r->{throw new BadRequestException("Role already exists");});current.setName(name);return Response.<Role>builder().statusCode(200).message("Role updated").data(repo.save(current)).build();}
 @Override public Response<List<Role>> getAllRoles(){return Response.<List<Role>>builder().statusCode(200).message("Roles fetched").data(repo.findAll()).build();}
 @Override public Response<?> deleteRole(Long id){Role r=repo.findById(id).orElseThrow(()->new NotFoundException("Role not found"));if(r.getName().equals("ROLE_CUSTOMER"))throw new BadRequestException("Default customer role cannot be deleted");repo.delete(r);return Response.builder().statusCode(200).message("Role deleted").build();}
 private String normalize(String s){if(s==null||s.isBlank())throw new BadRequestException("Role name is required");String n=s.trim().toUpperCase();return n.startsWith("ROLE_")?n:"ROLE_"+n;}
}
