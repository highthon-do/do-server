package com.highthon.challenge.domain.user.repository

import com.highthon.challenge.domain.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?

    fun existsByUsername(username: String): Boolean
}
