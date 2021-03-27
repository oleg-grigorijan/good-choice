package com.goodchoice.domain.auth.model

import com.goodchoice.domain.common.jooq.enums.ActorRole

enum class UserRole {

    REVIEWER,
    BRAND_PRESENTER,
    HR,
    MODERATOR,
    ADMINISTRATOR,
    SYSTEM;

    fun isEmployee() = this == HR || this == MODERATOR || this == ADMINISTRATOR
}

fun ActorRole.toUserRole(): UserRole = when (this) {
    ActorRole.REVIEWER -> UserRole.REVIEWER
    ActorRole.HR -> UserRole.HR
    ActorRole.MODERATOR -> UserRole.MODERATOR
    ActorRole.ADMINISTRATOR -> UserRole.ADMINISTRATOR
    ActorRole.BRAND_PRESENTER -> UserRole.BRAND_PRESENTER
    ActorRole.SYSTEM -> UserRole.SYSTEM
}
