#!/usr/bin/env sh
feature_name=example
feature_capital=Example
root_package="me/bookk"
root_package_dotted="me.bookk"
export ROOT_PACKAGE=$root_package_dotted
export FEATURE_NAME=$feature_name
export FEATURE_NAME_CAPITAL=$feature_capital
mkdir -p $feature_name
mkdir -p ../iosApp/iosApp/Feature/$feature_capital
cd $feature_name || exit
#Data source
mkdir -p data/source/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/datasource
envsubst < ../featuretemplate/DataSourceGradle.template > data/source/build.gradle.kts
#Data
mkdir -p data/src/commonMain/kotlin/$root_package/feature/$feature_name/data
mkdir -p data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/datasource
mkdir -p data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/di
mkdir -p data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/remote
mkdir -p data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/remote/api
mkdir -p data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/remote/model
envsubst < ../featuretemplate/DataGradle.template > data/build.gradle.kts
envsubst < ../featuretemplate/DataDI.template > data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/di/${feature_capital}DataDi.kt
#Domain API
mkdir -p domain/api/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/api
mkdir -p domain/api/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/api/entity
envsubst < ../featuretemplate/DomainApiGradle.template > domain/api/build.gradle.kts
#Domain Impl
mkdir -p domain/impl/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/impl
mkdir -p domain/impl/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/impl/di
envsubst < ../featuretemplate/DomainImplGradle.template > domain/impl/build.gradle.kts
envsubst < ../featuretemplate/DomainImplDI.template > domain/impl/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/impl/di/${feature_capital}DomainDi.kt
#Presentation
mkdir -p presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/di
mkdir -p presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/di
mkdir -p presentation/src/iosMain/kotlin/$root_package/feature/$feature_name/presentation/di
envsubst < ../featuretemplate/PresentationGradle.template > presentation/build.gradle.kts
envsubst < ../featuretemplate/PresentationCommonDi.template > presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/di/${feature_capital}PresentationDi.kt
envsubst < ../featuretemplate/PresentationAndroidDi.template > presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/di/Android${feature_capital}PresentationDi.kt
envsubst < ../featuretemplate/PresentationIosDi.template > presentation/src/iosMain/kotlin/$root_package/feature/$feature_name/presentation/di/Ios${feature_capital}PresentationDi.kt
#shared
envsubst < ../featuretemplate/SharedDi.template > ../../shared/src/commonMain/kotlin/$root_package/di/feature/${feature_capital}DI.kt
sed '$d' ../../shared/src/commonMain/kotlin/$root_package/di/DISetup.kt > tmp && mv tmp ../../shared/src/commonMain/kotlin/$root_package/di/DISetup.kt
cat >> ../../shared/src/commonMain/kotlin/$root_package/di/DISetup.kt << EOF
    ${feature_name}DiModule()
)
EOF
#Install modules into gradle
cat >> ../../settings.gradle.kts<< EOF

//${feature_capital}
include(":feature:${feature_name}:data")
include(":feature:${feature_name}:data:source")
include(":feature:${feature_name}:domain:api")
include(":feature:${feature_name}:domain:impl")
include(":feature:${feature_name}:presentation")
EOF

#Android
#Insert state factory method
sed '$d' ../../shared/src/commonMain/kotlin/${root_package}/presentation/StateFactoryCreator.kt > tmp && mv tmp ../../shared/src/commonMain/kotlin/${root_package}/presentation/StateFactoryCreator.kt
cat >> ../../shared/src/commonMain/kotlin/${root_package}/presentation/StateFactoryCreator.kt<< EOF
    fun create${feature_capital}Factory(): ${feature_capital}StateFactory
}
EOF
#Insert state factory method implementation
sed '$d' ../../androidApp/src/main/java/${root_package}/android/AndroidStateFactoryCreator.kt > tmp && mv tmp androidApp/src/main/java/${root_package}/android/AndroidStateFactoryCreator.kt
cat >> ../../androidApp/src/main/java/${root_package}/android/AndroidStateFactoryCreator.kt << EOF
    override fun create${feature_capital}Factory(): ${feature_capital}StateFactory {
        return Android${feature_capital}StateFactory()
    }
}
EOF
cat >> presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/Android${feature_capital}StateFactory.kt << EOF
package $root_package.feature.$feature_name.presentation

import$root_package.feature.$feature_name.presentation.${feature_capital}StateFactory

class Android${feature_capital}StateFactory : ${feature_capital}StateFactory {

}
EOF

#IOS
#Insert state factory method implementation
cat >> ../../iosApp/iosApp/Feature/$feature_capital/IOS${feature_capital}StateFactory.swift << EOF
import shared

@MainActor
class IOS${feature_capital}StateFactory: @MainActor ${feature_capital}StateFactory {

}
EOF
#Insert state factory to the base factory
sed '$d' ../../iosApp/iosApp/IOSStateFactoryCreator.swift > tmp && mv tmp ../../iosApp/iosApp/IOSStateFactoryCreator.swift
cat >> ../../iosApp/iosApp/IOSStateFactoryCreator.swift << EOF
	func create${feature_capital}Factory() -> any ${feature_capital}StateFactory {
		return IOS${feature_capital}StateFactory()
	}
}
EOF
#Cleanup
rm tmp