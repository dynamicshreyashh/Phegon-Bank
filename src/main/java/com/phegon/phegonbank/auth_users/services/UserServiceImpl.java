package com.phegon.phegonbank.auth_users.services;

import com.phegon.phegonbank.auth_users.dtos.*;
import com.phegon.phegonbank.auth_users.entity.User;
import com.phegon.phegonbank.auth_users.repo.UserRepo;
import com.phegon.phegonbank.aws.AwsS3Service;
import com.phegon.phegonbank.exceptions.*;
import com.phegon.phegonbank.res.Response;
import com.phegon.phegonbank.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service @RequiredArgsConstructor
public class UserServiceImpl implements UserService {
 private final UserRepo repo; private final PasswordEncoder encoder; private final ModelMapper mapper; private final AwsS3Service s3;
 @Override public Response<UserDTO> getMe(){return ok(current());}
 @Override @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')") public Response<List<UserDTO>> getUsers(int page,int size){var p=repo.findAll(PageRequest.of(Math.max(0,page),Math.min(Math.max(1,size),100)));return Response.<List<UserDTO>>builder().statusCode(200).message("Users fetched").data(p.getContent().stream().map(u->mapper.map(u,UserDTO.class)).toList()).meta(java.util.Map.of("page",p.getNumber(),"size",p.getSize(),"totalElements",p.getTotalElements(),"totalPages",p.getTotalPages())).build();}
 @Override @Transactional public Response<UserDTO> updatePassword(UpdatePasswordRequest r){User u=current();if(!encoder.matches(r.getOldPassword(),u.getPassword()))throw new BadRequestException("Old password is incorrect");u.setPassword(encoder.encode(r.getNewPassword()));return ok(repo.save(u));}
 @Override @Transactional public Response<UserDTO> uploadProfilePicture(MultipartFile file){if(file==null||file.isEmpty())throw new BadRequestException("Profile picture is required");if(file.getSize()>5*1024*1024)throw new BadRequestException("Profile picture must be 5MB or smaller");if(file.getContentType()==null||!file.getContentType().startsWith("image/"))throw new BadRequestException("Only image files are allowed");if(s3==null)throw new BadRequestException("S3 storage is not configured");User u=current();try{String old=u.getProfilePictureUrl();String url=s3.upload(file.getInputStream(),file.getSize(),file.getContentType(),file.getOriginalFilename());u.setProfilePictureUrl(url);repo.save(u);s3.deleteByUrl(old);return ok(u);}catch(Exception e){throw new BadRequestException("Profile picture upload failed");}}
 private User current(){if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthUser au)return repo.findById(au.getUser().getId()).orElseThrow(()->new NotFoundException("User not found"));throw new BadRequestException("Authentication required");}
 private Response<UserDTO> ok(User u){return Response.<UserDTO>builder().statusCode(200).message("User fetched").data(mapper.map(u,UserDTO.class)).build();}
}
