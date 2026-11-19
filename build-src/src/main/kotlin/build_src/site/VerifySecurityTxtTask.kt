package build_src.site

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException

@DisableCachingByDefault(because = "Depends on the current date")
abstract class VerifySecurityTxtTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val securityTxt: RegularFileProperty

    @TaskAction
    fun verify() {
        val file = securityTxt.get().asFile
        val error = SecurityTxtExpiry.error(file.readText(), Instant.now())
        if (error != null) {
            throw GradleException("$file: $error")
        }
    }
}

internal object SecurityTxtExpiry {
    private val MIN_REMAINING = Duration.ofDays(30)
    private val MAX_REMAINING = Duration.ofDays(365)
    private const val FIELD = "Expires:"

    fun error(content: String, now: Instant): String? {
        val values = content
            .lines()
            .filter { it.startsWith(FIELD) }
            .map { it.removePrefix(FIELD).trim() }
        if (values.size != 1) {
            return "expected exactly one '$FIELD' field (RFC 9116), found ${values.size}"
        }
        val expires =
            parse(values.single())
                ?: return "'$FIELD ${values.single()}' is not an RFC 3339 timestamp"
        return remainingError(Duration.between(now, expires))
    }

    private fun parse(value: String): Instant? {
        return try {
            Instant.parse(value)
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private fun remainingError(remaining: Duration): String? {
        return when {
            remaining < MIN_REMAINING -> {
                "'$FIELD' is less than ${MIN_REMAINING.toDays()} days away; " +
                    "move it to a date up to ${MAX_REMAINING.toDays()} days ahead and redeploy the site"
            }

            remaining > MAX_REMAINING -> {
                "'$FIELD' must be at most ${MAX_REMAINING.toDays()} days ahead (RFC 9116)"
            }

            else -> {
                null
            }
        }
    }
}
