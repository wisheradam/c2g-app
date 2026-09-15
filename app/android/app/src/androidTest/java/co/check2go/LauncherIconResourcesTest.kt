package co.check2go

import android.content.pm.PackageManager
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Resource checks for Android 006 (official Check2GO logo integration): the manifest wires up
 * real icon/roundIcon resources, and the launcher/in-app logo drawables and colors resolve to
 * the values supplied in design-assets/logo/C2Glogo.svg.
 */
class LauncherIconResourcesTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun manifestDeclaresNonDefaultLauncherIcons() {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )

        assertNotEquals(0, applicationInfo.icon)
    }

    @Test
    fun adaptiveIconLayersInflate() {
        assertNotNull(context.getDrawable(R.mipmap.ic_launcher))
        assertNotNull(context.getDrawable(R.mipmap.ic_launcher_round))
        assertNotNull(context.getDrawable(R.drawable.ic_launcher_foreground))
        assertNotNull(context.getDrawable(R.drawable.ic_launcher_legacy))
    }

    @Test
    fun inAppLogoMarkInflates() {
        assertNotNull(context.getDrawable(R.drawable.ic_check2go_mark))
    }

    @Test
    fun officialLogoColorsMatchSourceArtwork() {
        assertEquals(0xFF114DDC.toInt(), context.getColor(R.color.c2g_logo_blue))
        assertEquals(0xFF27DC11.toInt(), context.getColor(R.color.c2g_logo_green))
        assertEquals(0xFFF4F7FA.toInt(), context.getColor(R.color.ic_launcher_background))
    }
}
