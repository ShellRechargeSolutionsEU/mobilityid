## Mobility ID Utils [![Build Status](https://travis-ci.org/thenewmotion/mobilityid.png?branch=master)](https://travis-ci.org/thenewmotion/mobilityid)

### About the library ###

Scala utils to parse, validate and convert electric mobility account
identifier strings according to the ISO 15118-1 and DIN SPEC 91286 standards.

### Where to get it ###

To get the latest version of the library, add the following to your SBT build:

``` scala
resolvers += "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"
```

And use the following library dependency:

```
libraryDependencies += "com.thenewmotion" %% "mobilityid" % "0.4"
```


### How to use ###

You can create an EmaId object from a string in ISO 15118-1 or DIN SPEC 91286 format:

```
scala> EmaId("NL-TNM-012204-5")
res1: Option[com.thenewmotion.evcoid.EmaId] = Some(NL-TNM-000122045-U)

scala> EmaId("NL-TNM-000122045")
res2: Option[com.thenewmotion.evcoid.EmaId] = Some(NL-TNM-000122045-U)

// this fails because of an illegal character
EmaId("NL-T|M-000122045")
res8: Option[com.thenewmotion.evcoid.EmaId] = None
```

or if you have more detailed field information you can create an EmaId from the separate fields:

```
scala> EmaId("NL", "TNM", "000122045")
res3: com.thenewmotion.evcoid.EmaId = NL-TNM-000122045-U

scala> EmaId("NL", "TNM", "000122045", 'U')
res4: com.thenewmotion.evcoid.EmaId = NL-TNM-000122045-U

// this fails because of an invalid check digit
scala> EmaId("NL", "TNM", "000122045", 'X')
java.lang.IllegalArgumentException: requirement failed

// this fails because of an illegal character
scala> EmaId("NL", "T|M", "000122045")
java.lang.IllegalArgumentException: requirement failed
```

Once you have an EmaId object you can use it for intelligent comparison:

```
// checking if DIN ID equals an ISO ID
EmaId("NL-TNM-000122045") == EmaId("NL-TNM-012204-5")
res9: Boolean = true
```

and print them in various formats:

```
scala> val emaId = EmaId("NL", "TNM", "012345678")
emaId: com.thenewmotion.evcoid.EmaId = NL-TNM-012345678-W

// note how this EMA ID cannot be represented in DIN format
scala> emaId.toDinString
res13: Option[String] = None

scala> emaId.toCompactString
res14: String = NLTNM012345678W

scala> emaId.toCompactStringWithoutCheckDigit
res15: String = NLTNM012345678

scala> emaId.toString
res16: String = NL-TNM-012345678-W
```

### Documentation and getting help ###

This library is using the algorithm of check digit calculation for ISO 15118-1 Contract IDs described here:
http://www.ochp.eu/id-validator/e-mobility-ids_evcoid_check-digit-calculation_explanation/
