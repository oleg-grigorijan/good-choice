package com.goodchoice.domain.image.service

import com.goodchoice.domain.auth.model.UserRole.ADMINISTRATOR
import com.goodchoice.domain.auth.model.UserRole.REVIEWER
import com.goodchoice.domain.auth.model.requireAnyRole
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.image.model.ImageUploadingRequest
import com.goodchoice.domain.image.persistence.ImageRepository
import org.springframework.transaction.annotation.Transactional

interface ImageService {
    fun add(request: ImageUploadingRequest): Reference
}

class ImageServiceImpl(private val imageRepo: ImageRepository, private val authService: AuthService) : ImageService {

    @Transactional
    override fun add(request: ImageUploadingRequest): Reference {
        authService.currentAuth.requireAnyRole(REVIEWER, ADMINISTRATOR)
        return imageRepo.add(request.location)
    }
}