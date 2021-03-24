package com.goodchoice.domain.auth

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.common.jooq.enums.ActorRole

fun ActorRole.toUserRole(): UserRole = when (this) {
    ActorRole.REVIEWER -> UserRole.REVIEWER
    ActorRole.HR -> UserRole.HR
    ActorRole.MODERATOR -> UserRole.MODERATOR
    ActorRole.ADMINISTRATOR -> UserRole.ADMINISTRATOR
    ActorRole.BRAND_PRESENTER -> UserRole.BRAND_PRESENTER
    ActorRole.SYSTEM -> UserRole.SYSTEM
}
