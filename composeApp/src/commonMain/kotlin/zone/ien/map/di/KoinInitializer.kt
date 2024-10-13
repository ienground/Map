package zone.ien.map.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    additionalModules: List<Module> = listOf(),
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(
            additionalModules +
            listOf(
                commonModule,
                platformModule
            )
        )
    }
}