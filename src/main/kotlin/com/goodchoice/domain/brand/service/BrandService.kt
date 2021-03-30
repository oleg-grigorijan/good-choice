package com.goodchoice.domain.brand.service

import com.goodchoice.domain.brand.persistence.BrandRepository

interface BrandService {
}

class BrandServiceImpl(
    brandRepo: BrandRepository
) : BrandService {

}
