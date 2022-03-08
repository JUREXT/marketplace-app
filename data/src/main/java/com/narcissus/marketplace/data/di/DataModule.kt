package com.narcissus.marketplace.data.di

import com.narcissus.marketplace.apiclient.di.apiClientModule
import com.narcissus.marketplace.data.CartLocalRepositoryImpl
import com.narcissus.marketplace.data.DepartmentsRepositoryImpl
import com.narcissus.marketplace.data.OrderRepositoryImpl
import com.narcissus.marketplace.data.ProductsDetailsRepositoryImpl
import com.narcissus.marketplace.data.ProductsPreviewRepositoryImpl
import com.narcissus.marketplace.data.UserRepositoryImpl
import com.narcissus.marketplace.repository.local.CartLocalRepository
import com.narcissus.marketplace.repository.remote.DepartmentsRepository
import com.narcissus.marketplace.data.persistence.di.persistenceModule
import com.narcissus.marketplace.repository.remote.OrderRepository
import com.narcissus.marketplace.repository.remote.ProductsDetailsRepository
import com.narcissus.marketplace.repository.remote.ProductsPreviewRepository
import com.narcissus.marketplace.repository.remote.UserRepository
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val dataModule = module {
    loadKoinModules(listOf(apiClientModule, persistenceModule))

    single<CartLocalRepository> { CartLocalRepositoryImpl() }
    single<DepartmentsRepository> { DepartmentsRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl() }

    single<ProductsDetailsRepository> {
        ProductsDetailsRepositoryImpl(apiService = get())
    }

    single<ProductsPreviewRepository> {
        ProductsPreviewRepositoryImpl(apiService = get())
    }

    single<UserRepository> {
        UserRepositoryImpl(productsDao = get())
    }
}
