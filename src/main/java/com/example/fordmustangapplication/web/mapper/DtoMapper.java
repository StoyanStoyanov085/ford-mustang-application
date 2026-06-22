package com.example.fordmustangapplication.web.mapper;

import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.web.dto.EditRequest;
import com.example.fordmustangapplication.web.dto.UserDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static EditRequest mapUserToUserEditRequest(User user) {

        return EditRequest.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .build();
    }

    public static UserDto mapUserToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .banned(user.isBanned())
                .build();
    }
}
