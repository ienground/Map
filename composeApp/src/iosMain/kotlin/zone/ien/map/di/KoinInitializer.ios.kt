package zone.ien.map.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoinIos(
    appComponent: IosApplicationComponent
) {
    initKoin(
        listOf(module { single { appComponent } }),
    )
}