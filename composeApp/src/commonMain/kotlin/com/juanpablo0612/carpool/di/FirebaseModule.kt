package com.juanpablo0612.carpool.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.koin.dsl.module

// Every feature module depends on one or more of these Firebase SDK singletons, so they get
// their own module instead of living inside whichever feature happened to need them first.
val firebaseModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }
}
