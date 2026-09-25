package com.phegon.phegonbank.auth_users.services;
import com.phegon.phegonbank.auth_users.dtos.*;
import com.phegon.phegonbank.res.Response;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
public interface UserService {
 Response<UserDTO> getMe();
 Response<List<UserDTO>> getUsers(int page,int size);
 Response<UserDTO> updatePassword(UpdatePasswordRequest request);
 Response<UserDTO> uploadProfilePicture(MultipartFile file);
}
