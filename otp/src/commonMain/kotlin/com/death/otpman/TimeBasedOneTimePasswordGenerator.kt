package com.death.otpman

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.floor
import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToByteArray

/**
 * Generator for the RFC 6238 "TOTP: Time-Based One-Time Password Algorithm"
 * (https://tools.ietf.org/html/rfc6238)
 *
 * @property secret the shared secret as a byte array.
 * @property config the [TimeBasedOneTimePasswordConfig] for this generator.
 */
open class TimeBasedOneTimePasswordGenerator(private val secret: ByteArray, private val config: TimeBasedOneTimePasswordConfig) {
  // -- Companion Object -------------------------------------------------------------------------------------------- //
  // -- Properties -------------------------------------------------------------------------------------------------- //

  private val hmacOneTimePasswordGenerator: HmacOneTimePasswordGenerator = HmacOneTimePasswordGenerator(secret, config)

  // -- Initialization ---------------------------------------------------------------------------------------------- //
  // -- Exposed Methods --------------------------------------------------------------------------------------------- //

  /**
   * Calculate the current time slot.
   *
   * The timeslot is basically the number of `timeStep`s from
   * [TimeBasedOneTimePasswordConfig] which fits into the [timestamp].
   *
   * @param timestamp The Unix timestamp against the counting of the time
   * steps is calculated. The default value is the current system time from
   * [System.currentTimeMillis].
   */
  fun counter(timestamp: Long = Clock.System.now().toEpochMilliseconds()): Long {
    if (config.timeStep == 0L) {
      // To avoid a divide by zero
      return 0
    }

    return floor(timestamp.toDouble().div(config.timeStepUnit.times(config.timeStep.toInt()).duration.inWholeMilliseconds)).toLong()
  }

  /**
   * Convenience method for [counter].
   */
  fun counter(instant: Instant): Long = counter(instant.toEpochMilliseconds())

  /**
   * Calculates the start of the given time slot.
   *
   * This is basically the reverse calculation of counter(timestamp) method.
   *
   * @param counter The counter representing the time slot.
   * @return The Unix timestamp where the given time slot starts.
   */
  fun timeslotStart(counter: Long): Long {
    val timeStepMillis = config.timeStepUnit.times(config.timeStep.toInt()).duration.inWholeMilliseconds
    return (counter * timeStepMillis)
  }

  /**
   * Generates a code representing the time-based one-time password.
   *
   * The TOTP algorithm uses the HTOP algorithm via [HmacOneTimePasswordGenerator.generate],
   * with a counter parameter that represents the number of `timeStep`s from
   * [TimeBasedOneTimePasswordConfig] which fits into the [timestamp].
   *
   * The timestamp can be seen as the challenge to be solved. This should
   * normally be a continuous value over time (e.g. the current time).
   *
   * @param timestamp The Unix timestamp against the counting of the time
   * steps is calculated. The default value is the current system time from
   * [System.currentTimeMillis].
   */
  fun generate(timestamp: Long = Clock.System.now().toEpochMilliseconds()): String =
    hmacOneTimePasswordGenerator.generate(counter(timestamp))


  /**
   * Convenience method for [generate].
   */
  fun generate(instant: Instant): String = generate(instant.toEpochMilliseconds())

  /**
   * Validates the given code.
   *
   * @param code the code calculated from the challenge to validate.
   * @param timestamp the used challenge for the code. The default value is the
   *                  current system time from [System.currentTimeMillis].
   */
  fun isValid(code: String, timestamp: Long = Clock.System.now().toEpochMilliseconds()): Boolean {
    return code == generate(timestamp)
  }

  /**
   * Convenience method for [isValid].
   */
  fun isValid(code: String, instant: Instant) = isValid(code, instant.toEpochMilliseconds())
}