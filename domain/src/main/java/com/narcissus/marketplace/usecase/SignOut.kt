package com.narcissus.marketplace.usecase

import com.narcissus.marketplace.repository.remote.UserRepository

class SignOut(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.signOut()
}