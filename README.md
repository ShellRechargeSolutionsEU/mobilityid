## EVCO ID Utils [![Build Status](https://travis-ci.org/thenewmotion/evcoid.png?branch=master)](https://travis-ci.org/thenewmotion/evcoid)

### About the library ###

Scala utils which parse, validate and convert electric mobility account
identifier strings according to the ISO 15118-1 and DIN SPEC 91286 standards.

### Where to get it ###

To get the latest version of the library, add the following to your SBT build:

``` scala
resolvers += "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"
```

And use the following library dependency:

```
libraryDependencies += "com.thenewmotion" %% "evcoid" % "0.3"
```

### Documentation and getting help ###

The algorithm of check digit calculation for ISO 15118-1 Contract IDs is described here:  
http://www.ochp.eu/id-validator/e-mobility-ids_evcoid_check-digit-calculation_explanation/
