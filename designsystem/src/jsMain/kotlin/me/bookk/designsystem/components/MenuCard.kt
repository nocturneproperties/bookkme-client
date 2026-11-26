package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.SectionRowTextStyle
import me.bookk.designsystem.theme.error
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val MENU_ROW_HORIZONTAL_PADDING_REM = 1.0
private const val MENU_ROW_GAP_REM = 0.75
private val MenuDividerInset =
    (MENU_ROW_HORIZONTAL_PADDING_REM + IconTileSize.SMALL.sideRem + MENU_ROW_GAP_REM).cssRem

enum class MenuItemTrailing(
    val icon: ImageResource,
) {
    CHEVRON(DesignSystem.images.material_chevron_right),
    EXTERNAL(DesignSystem.images.material_open_in_new),
}

enum class MenuItemTone(
    val color: ColorToken,
) {
    DEFAULT(ColorToken.ActionText),
    DESTRUCTIVE(ColorToken.Error),
}

class MenuItem(
    val id: String,
    val text: StringDesc,
    val icon: ImageResource,
    val onClick: () -> Unit,
    val status: StringDesc? = null,
    val statusColor: ColorToken = ColorToken.Error,
    val trailing: MenuItemTrailing = MenuItemTrailing.CHEVRON,
    val tone: MenuItemTone = MenuItemTone.DEFAULT,
)

@Composable
fun MenuCard(items: List<MenuItem>, modifier: Modifier = Modifier) {
    if (items.isNotEmpty()) {
        AppCard(
            Modifier
                .fillMaxWidth()
                .padding(0.px)
                .gap(0.px)
                .then(modifier),
        ) {
            items.forEachIndexed { index, item ->
                key(item.id) { MenuRow(item) }
                if (index != items.lastIndex) MenuDivider()
            }
        }
    }
}

@Composable
fun MenuRow(item: MenuItem) {
    ListRowButton(
        onClick = item.onClick,
        modifier = Modifier
            .minHeight(3.25.cssRem)
            .padding(leftRight = MENU_ROW_HORIZONTAL_PADDING_REM.cssRem, topBottom = 0.5.cssRem),
        endIcon = item.trailing.icon,
    ) {
        IconTile(item.icon, color = item.tone.color, size = IconTileSize.SMALL)
        Span(menuTextModifier(item.tone).toAttrs()) { Text(item.text.localized()) }
        item.status?.let { StatusText(it, item.statusColor) }
    }
}

@Composable
private fun menuTextModifier(tone: MenuItemTone): Modifier {
    val modifier = SectionRowTextStyle
        .toModifier()
        .flexGrow(1)
    return if (tone == MenuItemTone.DESTRUCTIVE) modifier.error() else modifier
}

@Composable
private fun MenuDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .padding(left = MenuDividerInset),
    ) {
        HorizontalDivider()
    }
}
