package com.narcissus.marketplace.di

import com.narcissus.marketplace.data.di.dataModule
import com.narcissus.marketplace.domain.usecase.AddToCart
import com.narcissus.marketplace.domain.usecase.GetCart
import com.narcissus.marketplace.domain.usecase.GetCartCost
import com.narcissus.marketplace.domain.usecase.GetCartItemsAmount
import com.narcissus.marketplace.domain.usecase.GetProductDetails
import com.narcissus.marketplace.domain.usecase.GetRandomProducts
import com.narcissus.marketplace.domain.usecase.GetRecentlyVisitedProducts
import com.narcissus.marketplace.domain.usecase.GetTopRatedProducts
import com.narcissus.marketplace.domain.usecase.GetTopSalesProducts
import com.narcissus.marketplace.domain.usecase.RemoveFromCart
import com.narcissus.marketplace.domain.usecase.RemoveSelectedCartItems
import com.narcissus.marketplace.domain.usecase.SelectAllCartItems
import com.narcissus.marketplace.domain.usecase.SetCartItemAmount
import com.narcissus.marketplace.domain.usecase.SetCartItemSelected
import com.narcissus.marketplace.domain.usecase.SignInWithEmail
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val domainModule = module {
    loadKoinModules(dataModule)

    factory { GetTopRatedProducts(get()) }
    factory { GetTopSalesProducts(get()) }
    factory { GetRandomProducts(get()) }
    factory { GetRecentlyVisitedProducts(get()) }
    factory { GetProductDetails(get(), get()) }

    factory { GetCart(get()) }
    factory { GetCartItemsAmount(get()) }
    factory { GetCartCost(get()) }
    factory { SetCartItemAmount(get()) }
    factory { SetCartItemSelected(get()) }
    factory { SelectAllCartItems(get()) }
    factory { RemoveSelectedCartItems(get()) }
    factory { RemoveFromCart(get()) }
    factory { AddToCart(get()) }
    factory { SignInWithEmail(get()) }
}
