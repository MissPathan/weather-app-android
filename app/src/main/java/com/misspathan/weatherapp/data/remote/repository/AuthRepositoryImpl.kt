package com.misspathan.weatherapp.data.remote.repository

import com.misspathan.weatherapp.data.local.dao.UserDao
import com.misspathan.weatherapp.data.local.entity.UserEntity
import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.User
import com.misspathan.weatherapp.domain.repository.AuthRepository
import java.security.MessageDigest

class AuthRepositoryImpl(private val userDao: UserDao) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        val exists = userDao.emailExists(email)
        if (exists > 0) {
            return Result.Error("An account with this email already exists.")
        }
        return try {
            val entity = UserEntity(
                name = name,
                email = email,
                passwordHash = hashPassword(password)
            )
            userDao.insertUser(entity)
            val saved = userDao.getUserByEmail(email)
            if (saved != null) {
                Result.Success(saved.toDomain())
            } else {
                Result.Error("Registration failed, please try again.")
            }
        } catch (e: Exception) {
            Result.Error("Registration failed: ${e.message}", e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val user = userDao.getUserByEmail(email)
            ?: return Result.Error("No account found with this email.")

        val inputHash = hashPassword(password)
        return if (inputHash == user.passwordHash) {
            Result.Success(user.toDomain())
        } else {
            Result.Error("Incorrect password.")
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
