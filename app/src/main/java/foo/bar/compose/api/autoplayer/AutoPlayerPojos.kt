package foo.bar.compose.api.autoplayer

import kotlinx.serialization.Serializable

/**
 *
 *
 * <Code>
 *
 * The server returns us turns that look like this:
 *
 * {
 * "xPos":0,
 * "yPos":2
 * }
 *
 * </Code>
 *
 *
 */
@Serializable
data class NextTurnPojo(var xPos: Int, var yPos: Int)
