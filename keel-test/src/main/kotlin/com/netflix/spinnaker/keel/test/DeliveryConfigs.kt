package com.netflix.spinnaker.keel.test

import com.netflix.spinnaker.keel.api.DeliveryConfig
import com.netflix.spinnaker.keel.api.Environment
import com.netflix.spinnaker.keel.api.Resource
import com.netflix.spinnaker.keel.api.artifacts.DeliveryArtifact
import com.netflix.spinnaker.keel.api.artifacts.VirtualMachineOptions
import com.netflix.spinnaker.keel.artifacts.DebianArtifact

/**
 * Helper functions for working with delivery configs
 */

fun deliveryConfig(
  resource: Resource<*> = resource(),
  env: Environment = Environment("test", setOf(resource)),
  application: String = "fnord",
  configName: String = "myconfig",
  artifact: DeliveryArtifact = DebianArtifact(name = "fnord", deliveryConfigName = configName, vmOptions = VirtualMachineOptions(baseOs = "bionic", regions = setOf("us-west-2"))),
  deliveryConfig: DeliveryConfig = DeliveryConfig(
    name = configName,
    application = application,
    serviceAccount = "keel@spinnaker",
    artifacts = setOf(artifact),
    environments = setOf(env),
    metadata = mapOf("some" to "meta")
  )
): DeliveryConfig {
  return deliveryConfig
}

fun deliveryConfig(
  resources: Set<Resource<*>>,
  env: Environment = Environment("test", resources),
  application: String = "fnord",
  configName: String = "myconfig",
  artifact: DeliveryArtifact = DebianArtifact(name = "fnord", deliveryConfigName = configName, vmOptions = VirtualMachineOptions(baseOs = "bionic", regions = setOf("us-west-2"))),
  deliveryConfig: DeliveryConfig = DeliveryConfig(
    name = configName,
    application = application,
    serviceAccount = "keel@spinnaker",
    artifacts = setOf(artifact),
    environments = setOf(env),
    metadata = mapOf("some" to "meta")
  )
): DeliveryConfig {
  return deliveryConfig
}
