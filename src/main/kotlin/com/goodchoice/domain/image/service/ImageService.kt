package com.goodchoice.domain.image.service

import com.goodchoice.domain.auth.model.UserRole.ADMINISTRATOR
import com.goodchoice.domain.auth.model.UserRole.REVIEWER
import com.goodchoice.domain.auth.model.requireAnyRole
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.image.model.ImageModificationRequest
import com.goodchoice.domain.image.persistence.ImageRepository

interface ImageService {
    fun add(request: ImageModificationRequest): Reference
}

class ImageServiceImpl(private val imageRepo: ImageRepository, private val authService: AuthService) : ImageService {
    override fun add(request: ImageModificationRequest): Reference {
        authService.currentAuth.requireAnyRole(setOf(REVIEWER, ADMINISTRATOR))
        return imageRepo.add(request.location)
    }
}