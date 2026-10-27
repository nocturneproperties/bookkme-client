#!/usr/bin/env sh

# Usage: ./feature.template.sh <feature_name> <FeatureNameCapitalized>
feature_name=$1
feature_capital=$2

if [ -z "$feature_name" ] || [ -z "$feature_capital" ]; then
    echo "Usage: ./feature.template.sh <feature_name> <FeatureNameCapitalized>"
    exit 1
fi

root_package="me/bookk"
root_package_dotted="me.bookk"
ROOT_PACKAGE=$root_package_dotted
FEATURE_NAME=$feature_name
FEATURE_NAME_CAPITAL=$feature_capital

# Function to replace placeholders using sed (macOS compatible)
process_template() {
    input=$1
    output=$2
    sed -e "s/\${ROOT_PACKAGE}/${ROOT_PACKAGE}/g" \
        -e "s/\${FEATURE_NAME}/${FEATURE_NAME}/g" \
        -e "s/\${FEATURE_NAME_CAPITAL}/${FEATURE_NAME_CAPITAL}/g" \
        "$input" > "$output"
}

# Create feature directory (relative to script location, it's one level up)
FEATURE_DIR="../$feature_name"
mkdir -p "$FEATURE_DIR"

# 1. Data Source
mkdir -p "$FEATURE_DIR/data/source/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/datasource"
process_template "feature/DataSourceGradle.template" "$FEATURE_DIR/data/source/build.gradle.kts"

# 2. Data
mkdir -p "$FEATURE_DIR/data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/datasource"
mkdir -p "$FEATURE_DIR/data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/di"
mkdir -p "$FEATURE_DIR/data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/remote/api"
mkdir -p "$FEATURE_DIR/data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/remote/model"
process_template "feature/DataGradle.template" "$FEATURE_DIR/data/build.gradle.kts"
process_template "feature/DataDI.template" "$FEATURE_DIR/data/src/commonMain/kotlin/$root_package/feature/$feature_name/data/di/${feature_capital}DataDi.kt"

# 3. Domain API
mkdir -p "$FEATURE_DIR/domain/api/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/api"
mkdir -p "$FEATURE_DIR/domain/api/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/api/entity"
process_template "feature/DomainApiGradle.template" "$FEATURE_DIR/domain/api/build.gradle.kts"

# 4. Domain Impl
mkdir -p "$FEATURE_DIR/domain/impl/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/impl/di"
process_template "feature/DomainImplGradle.template" "$FEATURE_DIR/domain/impl/build.gradle.kts"
process_template "feature/DomainImplDI.template" "$FEATURE_DIR/domain/impl/src/commonMain/kotlin/$root_package/feature/$feature_name/domain/impl/di/${feature_capital}DomainDi.kt"

# 5. Presentation
mkdir -p "$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/di"
mkdir -p "$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/di"
mkdir -p "$FEATURE_DIR/presentation/src/iosMain/kotlin/$root_package/feature/$feature_name/presentation/di"
process_template "feature/PresentationGradle.template" "$FEATURE_DIR/presentation/build.gradle.kts"
process_template "feature/PresentationCommonDi.template" "$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/di/${feature_capital}PresentationDi.kt"
process_template "feature/PresentationAndroidDi.template" "$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/di/Android${feature_capital}PresentationDi.kt"
process_template "feature/PresentationIosDi.template" "$FEATURE_DIR/presentation/src/iosMain/kotlin/$root_package/feature/$feature_name/presentation/di/Ios${feature_capital}PresentationDi.kt"

# State Factory
process_template "feature/StateFactory.template" "$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/${feature_capital}StateFactory.kt"

mkdir -p "$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation"
cat > "$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/Android${feature_capital}StateFactory.kt" << EOF
package $root_package_dotted.feature.$feature_name.presentation

class Android${feature_capital}StateFactory : ${feature_capital}StateFactory {

}
EOF

# 6. Shared DI
mkdir -p "../../shared/src/commonMain/kotlin/$root_package/di/feature"
process_template "feature/SharedDi.template" "../../shared/src/commonMain/kotlin/$root_package/di/feature/${feature_capital}DI.kt"

# 7. Wiring DISetup.kt
DI_SETUP="../../shared/src/commonMain/kotlin/$root_package/di/DISetup.kt"
# Add import after the last feature import
sed -i '' "/import ${root_package_dotted}.di.feature.settingsDiModule/a\\
import ${root_package_dotted}.di.feature.${feature_name}DiModule
" "$DI_SETUP"

# Add to modules list
sed '$d' "$DI_SETUP" > tmp && mv tmp "$DI_SETUP"
sed -i '' '$s/$/,/' "$DI_SETUP"
cat >> "$DI_SETUP" << EOF
    ${feature_name}DiModule()
)
EOF

# 8. Wiring StateFactoryCreator.kt
SF_CREATOR="../../shared/src/commonMain/kotlin/$root_package/presentation/StateFactoryCreator.kt"
# Add import
sed -i '' "/import ${root_package_dotted}.feature.settings.presentation.SettingsStateFactory/a\\
import ${root_package_dotted}.feature.${feature_name}.presentation.${feature_capital}StateFactory
" "$SF_CREATOR"

# Add method
sed '$d' "$SF_CREATOR" > tmp && mv tmp "$SF_CREATOR"
cat >> "$SF_CREATOR" << EOF
    fun create${feature_capital}Factory(): ${feature_capital}StateFactory
}
EOF

# 9. Wiring AndroidStateFactoryCreator.kt
AS_CREATOR="../../androidApp/src/main/kotlin/$root_package/android/AndroidStateFactoryCreator.kt"
# Add imports
sed -i '' "/import ${root_package_dotted}.feature.services.presentation.ServicesStateFactory/a\\
import ${root_package_dotted}.feature.${feature_name}.presentation.Android${feature_capital}StateFactory\\
import ${root_package_dotted}.feature.${feature_name}.presentation.${feature_capital}StateFactory
" "$AS_CREATOR"

# Add method implementation
sed '$d' "$AS_CREATOR" > tmp && mv tmp "$AS_CREATOR"
cat >> "$AS_CREATOR" << EOF
    override fun create${feature_capital}Factory(): ${feature_capital}StateFactory {
        return Android${feature_capital}StateFactory()
    }
}
EOF

# 10. Wiring settings.gradle.kts
SETTINGS_GRADLE="../../settings.gradle.kts"
if ! grep -q ":feature:${feature_name}:presentation" "$SETTINGS_GRADLE"; then
cat >> "$SETTINGS_GRADLE" << EOF

//${feature_capital}
include(":feature:${feature_name}:data")
include(":feature:${feature_name}:data:source")
include(":feature:${feature_name}:domain:api")
include(":feature:${feature_name}:domain:impl")
include(":feature:${feature_name}:presentation")
EOF
fi

# 11. IOS
mkdir -p "../../iosApp/iosApp/Feature/$feature_capital"
cat > "../../iosApp/iosApp/Feature/$feature_capital/IOS${feature_capital}StateFactory.swift" << EOF
import shared

@MainActor
class IOS${feature_capital}StateFactory: @MainActor ${feature_capital}StateFactory {

}
EOF

# Wiring IOSStateFactoryCreator.swift
IOS_SF="../../iosApp/iosApp/Core/IOSStateFactoryCreator.swift"
sed '$d' "$IOS_SF" > tmp && mv tmp "$IOS_SF"
cat >> "$IOS_SF" << EOF
	func create${feature_capital}Factory() -> any ${feature_capital}StateFactory {
		return IOS${feature_capital}StateFactory()
	}
}
EOF

echo "Feature ${feature_capital} created successfully!"
