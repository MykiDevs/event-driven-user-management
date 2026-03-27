package com.mykidevs.userservice.mappers;


import com.mykidevs.userservice.dto.requests.UserCreateRequest;
import com.mykidevs.userservice.dto.requests.UserUpdateRequest;
import com.mykidevs.userservice.dto.responses.UserResponse;
import com.mykidevs.userservice.models.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateRequest user);
    UserResponse toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UserUpdateRequest req, @MappingTarget User user);

}
