package com.example.reognitionapp.api.dto

import com.example.reognitionapp.domain.User

class UserDTOMapper {

    fun fromUserDTOToUserDomain(userDTO: UserDTO) =
        User(userDTO.id, userDTO.email, userDTO.authenticationToken)
}