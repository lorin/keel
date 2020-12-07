# Constraint evaluation ordering


## Overview

The `EnvironmentConstraintRunner` runs constraints in the following order:

1. stateless, implicit
2. stateless, explicit
3. stateful, implicit
4. stateful, explicit

Within each of the above four categories, Spring orders the constraints
using the `@Order` annotation.

The code uses an `@Order` annotation ordering that is consistent with the environment constraint
runner's precedence (e.g., stateless constraints run before stateful) to avoid confusion.

## Ordering

### stateless, implicit

1. ImageExistsConstraintEvaluator

### stateless, explicit

2. AllowedTimesConstraintEvaluator

3. DependsOnConstraintEvaluator

### stateful, implicit

(none)

### stateful, explicit

4. PipelineConstraintEvaluator

5. CanaryConstraintEvaluator

6. ManualJudgementConstraintEvaluator


Note: we set manual judgment to the lowest precedence so that we do not query users
for judgment until all other constraints have already been met.
