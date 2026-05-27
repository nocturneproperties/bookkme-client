#!/usr/bin/env sh

# Usage: ./screen.template.sh <feature_name> <ScreenName> <screen.package>
feature_name=$1
screen_name=$2
screen_package=$3

if [ -z "$feature_name" ] || [ -z "$screen_name" ] || [ -z "$screen_package" ]; then
    echo "Usage: ./screen.template.sh <feature_name> <ScreenName> <screen.package>"
    exit 1
fi

root_package="me/bookk"
root_package_dotted="me.bookk"
ROOT_PACKAGE=$root_package_dotted
FEATURE_NAME=$feature_name
SCREEN_NAME=$screen_name
SCREEN_PACKAGE=$screen_package

# Try to find FeatureNameCapitalized
feature_capital=$(echo "$feature_name" | awk '{print toupper(substr($0,1,1))substr($0,2)}')
FEATURE_NAME_CAPITAL=$feature_capital
SCREEN_NAME_SMALL=$(echo "$SCREEN_NAME" | awk '{print tolower(substr($0,1,1))substr($0,2)}')

# Function to replace placeholders
process_template() {
    input=$1
    output=$2
    sed -e "s/\${ROOT_PACKAGE}/${ROOT_PACKAGE}/g" \
        -e "s/\${FEATURE_NAME}/${FEATURE_NAME}/g" \
        -e "s/\${FEATURE_NAME_CAPITAL}/${FEATURE_NAME_CAPITAL}/g" \
        -e "s/\${SCREEN_NAME}/${SCREEN_NAME}/g" \
        -e "s/\${SCREEN_PACKAGE}/${SCREEN_PACKAGE}/g" \
        -e "s/\${SCREEN_NAME_SMALL}/${SCREEN_NAME_SMALL}/g" \
        "$input" > "$output"
}

package_path=$(echo "$screen_package" | tr '.' '/')
FEATURE_DIR="../$feature_name"

# 1. commonMain
mkdir -p "$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/$package_path"
process_template "screen/ViewModel.template" "$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/$package_path/${screen_name}ViewModel.kt"
process_template "screen/State.template" "$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/$package_path/${screen_name}State.kt"
process_template "screen/NavigationDestination.template" "$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/$package_path/${screen_name}NavigationDestination.kt"

# 2. androidMain
mkdir -p "$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/$package_path"
process_template "screen/AndroidState.template" "$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/$package_path/Android${screen_name}State.kt"
process_template "screen/Screen.template" "$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/$package_path/${screen_name}Screen.kt"

# 3. Wiring StateFactory.kt
SF_INTERFACE="$FEATURE_DIR/presentation/src/commonMain/kotlin/$root_package/feature/$feature_name/presentation/${feature_capital}StateFactory.kt"
if [ -f "$SF_INTERFACE" ]; then
    # Add import
    sed -i '' "/package ${root_package_dotted}.feature.${feature_name}.presentation/a\\
import ${root_package_dotted}.feature.${feature_name}.presentation.${screen_package}.${screen_name}State
" "$SF_INTERFACE"
    
    # Add method
    sed '$d' "$SF_INTERFACE" > tmp && mv tmp "$SF_INTERFACE"
    cat >> "$SF_INTERFACE" << EOF
    fun create${screen_name}State(): ${screen_name}State
}
EOF
fi

# 4. Wiring AndroidStateFactory.kt
ASF_IMPL="$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/Android${feature_capital}StateFactory.kt"
if [ -f "$ASF_IMPL" ]; then
    # Add imports
    sed -i '' "/package ${root_package_dotted}.feature.${feature_name}.presentation/a\\
import ${root_package_dotted}.feature.${feature_name}.presentation.${screen_package}.Android${screen_name}State\\
import ${root_package_dotted}.feature.${feature_name}.presentation.${screen_package}.${screen_name}State
" "$ASF_IMPL"
    
    # Add method implementation
    sed '$d' "$ASF_IMPL" > tmp && mv tmp "$ASF_IMPL"
    cat >> "$ASF_IMPL" << EOF
    override fun create${screen_name}State(): ${screen_name}State {
        return Android${screen_name}State()
    }
}
EOF
fi

# 5. Wiring Presentation DI
DI_FILE="$FEATURE_DIR/presentation/src/androidMain/kotlin/$root_package/feature/$feature_name/presentation/di/Android${feature_capital}PresentationDi.kt"
if [ -f "$DI_FILE" ]; then
    # Add import
    sed -i '' "/import org.koin.dsl.module/a\\
import ${root_package_dotted}.feature.${feature_name}.presentation.${screen_package}.${screen_name}ViewModel
" "$DI_FILE"
    
    # Add to module
    sed '$d' "$DI_FILE" > tmp && mv tmp "$DI_FILE"
    cat >> "$DI_FILE" << EOF
    viewModelOf(::${screen_name}ViewModel)
}
EOF
fi

# 6. IOS Presentation DI (iosMain)
IOS_DI_FILE="$FEATURE_DIR/presentation/src/iosMain/kotlin/$root_package/feature/$feature_name/presentation/di/Ios${feature_capital}PresentationDi.kt"
if [ -f "$IOS_DI_FILE" ]; then
    # Add import
    sed -i '' "/package ${root_package_dotted}.feature.${feature_name}.presentation.di/a\\
import ${root_package_dotted}.feature.${feature_name}.presentation.${screen_package}.${screen_name}ViewModel
" "$IOS_DI_FILE"
    
    # Add to module (platform...DiModule)
    sed -i '' "/internal actual fun platform${feature_capital}DiModule(): Module = module {/a\\
    factory { ${screen_name}ViewModel(get(), get()) }
" "$IOS_DI_FILE"

    # Add @UsedInSwift function at the end
    cat >> "$IOS_DI_FILE" << EOF

@UsedInSwift
fun ${SCREEN_NAME_SMALL}VM(): ${screen_name}ViewModel =
    KoinPlatform.getKoin().get()
EOF
fi

# 7. IOS Swift files
IOS_FEATURE_DIR="../../iosApp/iosApp/Feature/${feature_capital}/${screen_name}"
mkdir -p "$IOS_FEATURE_DIR"
process_template "screen/IOSState.template" "$IOS_FEATURE_DIR/IOS${screen_name}State.swift"
process_template "screen/IOSScreen.template" "$IOS_FEATURE_DIR/${screen_name}Screen.swift"

# 8. Wiring IOSStateFactory.swift
IOS_SF_IMPL="../../iosApp/iosApp/Feature/${feature_capital}/IOS${feature_capital}StateFactory.swift"
if [ -f "$IOS_SF_IMPL" ]; then
    sed '$d' "$IOS_SF_IMPL" > tmp && mv tmp "$IOS_SF_IMPL"
    cat >> "$IOS_SF_IMPL" << EOF
	func create${screen_name}State() -> any ${screen_name}State {
		return IOS${screen_name}State()
	}
}
EOF
fi

echo "Screen ${screen_name} created and wired successfully in feature ${feature_name}"
