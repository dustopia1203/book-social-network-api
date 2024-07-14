package com.dustopia.book_social_network_api.model.mapper;

import com.dustopia.book_social_network_api.model.dto.UserDto;
import com.dustopia.book_social_network_api.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(user.getFullName(), user.getDateOfBirth(), user.getEmail(), user.getRole());
    }

}
