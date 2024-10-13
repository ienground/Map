package zone.ien.map.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoinAndroid(
    appComponent: AndroidApplicationComponent,
    appDeclaration: KoinAppDeclaration = {}
) {
    initKoin(
        listOf(module { single { appComponent } }),
        appDeclaration
    )
}