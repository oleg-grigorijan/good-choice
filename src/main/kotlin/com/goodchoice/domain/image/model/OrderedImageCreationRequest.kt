package com.goodchoice.domain.image.model

import com.goodchoice.domain.common.model.Reference

data class OrderedImageCreationRequest(val image: Reference, val ordering: Int)