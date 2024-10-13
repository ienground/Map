package zone.ien.map.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.UIKit.UIViewController

val NAVER_MAP_SCREEN = named("NaverMapScreen")

actual val platformModule = module {
//    single<UIViewController>(NAVER_MAP_SCREEN) { get<IosApplicationComponent>().mapScreen }
//    single<AppTerminator> { get<IosApplicationComponent>().appTerminator }
}