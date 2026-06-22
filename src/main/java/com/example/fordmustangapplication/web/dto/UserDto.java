package com.example.fordmustangapplication.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDto {

    private UUID id;
    private String username;
    private boolean banned;

}
