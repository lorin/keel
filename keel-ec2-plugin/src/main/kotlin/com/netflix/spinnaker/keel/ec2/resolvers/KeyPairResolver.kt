package com.netflix.spinnaker.keel.ec2.resolvers

import com.netflix.spinnaker.keel.api.Resource
import com.netflix.spinnaker.keel.api.ec2.ClusterSpec
import com.netflix.spinnaker.keel.api.ec2.EC2_CLUSTER_V1_1
import com.netflix.spinnaker.keel.api.ec2.LaunchConfigurationSpec
import com.netflix.spinnaker.keel.api.plugins.Resolver
import com.netflix.spinnaker.keel.clouddriver.CloudDriverCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
/**
 * A [Resolver] that looks up the default key pair configured in clouddriver for an account and uses that to fill out
 * missing key pair details in the [ClusterSpec].
 */
class KeyPairResolver(private val cloudDriverCache: CloudDriverCache) : Resolver<ClusterSpec> {
  override val supportedKind = EC2_CLUSTER_V1_1

  private val log by lazy { LoggerFactory.getLogger(javaClass) }

  override fun invoke(resource: Resource<ClusterSpec>): Resource<ClusterSpec> =
    resource.run {
      copy(
        spec = spec.withResolvedKeyPairs(spec.locations.account)
      )
    }

  private fun ClusterSpec.withResolvedKeyPairs(account: String): ClusterSpec {
    val defaultKeyPair = cloudDriverCache.defaultKeyPairForAccount(account)
    var defaultLaunchConfig: LaunchConfigurationSpec? = null
    val overrideLaunchConfigs = mutableMapOf<String, LaunchConfigurationSpec>()

    if (defaultKeyPair.contains(REGION_PLACEHOLDER)) {
      // if the default key pair in clouddriver is templated, we can't use that as a default but can try and apply it
      // to the overrides, if necessary.
      locations.regions.forEach { region ->
        val override = overrides[region.name]
        val launchConfiguration = override?.launchConfiguration
        if (launchConfiguration != null) {
          if (launchConfiguration.keyPair == null) {
            overrideLaunchConfigs[region.name] = launchConfiguration.copy(
              keyPair = defaultKeyPair.replace(REGION_PLACEHOLDER, region.name)
            )
          }
        } else {
          overrideLaunchConfigs[region.name] = LaunchConfigurationSpec(
            keyPair = defaultKeyPair.replace(REGION_PLACEHOLDER, region.name)
          )
        }
      }
    } else {
      // if it's not templated, we can attempt to set it as the default if not conflicting with the spec
      if (defaults.launchConfiguration?.keyPair != null && !defaults.launchConfiguration!!.keyPair.equals(defaultKeyPair)) {
        log.warn(
          "Default key pair specified in cluster spec (${defaults.launchConfiguration?.keyPair}) " +
            "does not match default configured in clouddriver ($defaultKeyPair)"
        )
      } else {
        defaultLaunchConfig = if (defaults.launchConfiguration != null) {
          defaults.launchConfiguration!!.copy(keyPair = defaultKeyPair)
        } else {
          LaunchConfigurationSpec(keyPair = defaultKeyPair)
        }
      }
    }

    return copy(
      _defaults = defaults.copy(launchConfiguration = defaultLaunchConfig ?: defaults.launchConfiguration),
      overrides = overrides.toMutableMap().also {
        overrideLaunchConfigs.forEach { (region, launchConfig) ->
          if (it[region] == null) {
            it[region] = ClusterSpec.ServerGroupSpec(launchConfiguration = launchConfig)
          } else {
            it[region] = it[region]!!.copy(launchConfiguration = launchConfig)
          }
        }
      }
    )
  }

  private companion object {
    const val REGION_PLACEHOLDER = "{{region}}"
  }
}
