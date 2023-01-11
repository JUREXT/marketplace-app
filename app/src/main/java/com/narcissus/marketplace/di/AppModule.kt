package com.narcissus.marketplace.di

import com.narcissus.marketplace.ui.sign_in.di.SignInQualifiers
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val appModule = module {
    factory(qualifier<SignInQualifiers.DefaultWebClientId>()) {
        //  androidContext().getString(R.string.default_web_client_id) // FIXME: Fake value for client id.
        "680743353019-8luoef29t24tmkui7mvl1d78hrj0hh8e.apps.googleusercontent.com"
    }
}
