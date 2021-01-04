package com.netflix.spinnaker.keel.core.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.netflix.spinnaker.keel.api.Verification

data class VerificationSummary(
  @JsonIgnore
  val verification: Verification
) {
  val id: String = verification.id
  val type: String = verification.type
}
