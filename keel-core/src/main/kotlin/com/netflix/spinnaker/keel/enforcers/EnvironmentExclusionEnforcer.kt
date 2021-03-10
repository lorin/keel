package com.netflix.spinnaker.keel.enforcers

import com.netflix.spinnaker.keel.api.Environment
import com.netflix.spinnaker.keel.api.persistence.KeelReadOnlyRepository
import com.netflix.spinnaker.keel.api.verification.VerificationContext
import com.netflix.spinnaker.keel.persistence.EnvironmentLeaseRepository
import com.netflix.spinnaker.keel.persistence.Lease

class StuffIsHappeningInTheEnvironment(msg: String) : Exception(msg) {

}

class EnvironmentExclusionEnforcer(
  private val environmentLeaseRepository: EnvironmentLeaseRepository,
  private val keelRepository: KeelReadOnlyRepository
) {

  /**
   * To get a verification lease against an environment, need:
   *
   * 1. An environment lease
   * 2. No active deployments
   * 3. No active verifications
   */
  fun <T> withVerificationLease(context: VerificationContext, action: () -> T) : T {
    val environment = context.environment

    return environmentLeaseRepository.tryAcquireLease(environment).withLease {
      ensureNoActiveDeployments(environment)
      ensureNoActiveVerifications(environment)

      action.invoke()
   }
  }

  /**
   * To get an actuation lease against an environment, need:
   *
   * 1. An environment lease
   * 2. No active verifications
   *
   * It's ok if other actuations (e.g., deployments) are going on.
   */
  suspend fun <T> withActuationLease(environment: Environment, action: suspend () -> T) : T =
    environmentLeaseRepository.tryAcquireLease(environment).withLeaseSuspend {
      ensureNoActiveVerifications(environment)

      action.invoke()
    }


  /**
   * @throws StuffIsHappeningInTheEnvironment if there's an active deployment
   */
  private fun ensureNoActiveDeployments(environment: Environment) {
    /**
     * Use keelRepository to check if deployment is happening in this environment
     */
    TODO("Not yet implemented")
  }

  private fun ensureNoActiveVerifications(environment: Environment): Boolean {
    /**
     * Use keelRepository to check if verification is running in this environment
     */

    TODO("Not yet implemented")
  }

  /**
   * take an action and then release the lease.
   */
  private fun <T> Lease.withLease(action: () -> T): T =
    try {
      action.invoke()
    } catch (ex: Exception) {
      throw ex
    } finally {
      environmentLeaseRepository.release(this)
    }

  /**
   * take an action and then release the lease, suspend flavor
   */
  private suspend fun <T> Lease.withLeaseSuspend(action: suspend () -> T) : T =
    try {
      action.invoke()
    } catch (ex: Exception) {
      throw ex
    } finally {
      environmentLeaseRepository.release(this)
    }
}