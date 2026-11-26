package me.bookk.feature.settings.presentation.export

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportFileSharerTest {

    private class FakeNativeFileShare(
        private val sharesFiles: Boolean,
    ) : NativeFileShare {
        val shared = mutableListOf<ExportFile>()

        override fun canShare(file: ExportFile): Boolean {
            return sharesFiles
        }

        override fun share(file: ExportFile) {
            shared.add(file)
        }
    }

    private class Fixture(
        val nativeShare: FakeNativeFileShare?,
    ) {
        val downloads = mutableListOf<ExportFile>()
        val sut = ExportFileSharer(
            nativeShare = { nativeShare },
            download = { downloads.add(it) },
        )
    }

    private val file = ExportFile("bookkme-personal-data.json", "{}")

    @Test
    fun `opens the native share sheet when the browser can share files`() = runUnitTest {
        given()
        val fixture = Fixture(FakeNativeFileShare(sharesFiles = true))

        whenn()
        fixture.sut.share(file)

        then()
        assertEquals<List<ExportFile>?>(listOf(file), fixture.nativeShare?.shared)
        assertEquals<List<ExportFile>>(emptyList(), fixture.downloads)
    }

    @Test
    fun `downloads the file when the browser can't share files`() = runUnitTest {
        given()
        val fixture = Fixture(FakeNativeFileShare(sharesFiles = false))

        whenn()
        fixture.sut.share(file)

        then()
        assertEquals<List<ExportFile>>(listOf(file), fixture.downloads)
        assertEquals<List<ExportFile>?>(emptyList(), fixture.nativeShare?.shared)
    }

    @Test
    fun `downloads the file when the browser has no share sheet`() = runUnitTest {
        given()
        val fixture = Fixture(nativeShare = null)

        whenn()
        fixture.sut.share(file)

        then()
        assertEquals<List<ExportFile>>(listOf(file), fixture.downloads)
    }
}
