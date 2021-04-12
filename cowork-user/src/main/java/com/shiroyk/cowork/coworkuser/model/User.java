package com.shiroyk.cowork.coworkuser.model;

import com.shiroyk.cowork.coworkcommon.constant.Role;
import com.shiroyk.cowork.coworkcommon.dto.UserDto;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class User {
    @Id
    private String id;
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String group;
    private String avatar;
    private Role role;
    private Boolean isEnable = true;
    private List<String> recent = new ArrayList<>();
    private Set<String> star = new HashSet<>();
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();

    public User() {
    }

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserDto toUserDtoS() {
        return new UserDto(id, username, nickname, email);
    }

    public UserDto toUserDtoM() {
        return new UserDto(id, username, nickname, email, avatar);
    }

    public UserDto toUserDtoL() {
        return new UserDto(id, username, nickname, email, group, avatar);
    }

    public UserDto toUserDto() {
        return new UserDto(id, username, nickname, email, group, avatar, recent, star, role);
    }

    public User setIsEnable() {
        isEnable = !isEnable;
        return this;
    }

    public void setUpdateTime() {
        this.updateTime = LocalDateTime.now();
    }
}