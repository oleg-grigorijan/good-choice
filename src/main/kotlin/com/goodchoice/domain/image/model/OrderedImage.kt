package com.goodchoice.domain.image.model

import java.net.URL
import java.util.*

data class OrderedImage(val id: UUID, val location: URL, val ordering: Int)