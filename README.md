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

``` scala
libraryDependencies += "com.thenewmotion" %% "mobilityid" % "0.9-SNAPSHOT"
```

### How to use ###

#### EMA-ID ####

You can create an EmaId object from a string in ISO 15118-1 or DIN SPEC 91286 format:

``` scala
scala> import com.thenewmotion.mobilityid._
import com.thenewmotion.mobilityid._

scala> EmaId("NL-TNM-012204-5")
res1: Option[com.thenewmotion.mobilityid.EmaId] = Some(NL-TNM-000122045-U)

scala> EmaId("NL-TNM-000122045")
res2: Option[com.thenewmotion.mobilityid.EmaId] = Some(NL-TNM-000122045-U)
```

This fails because of an illegal character:

``` scala
scala> EmaId("NL-T|M-000122045")
res3: Option[com.thenewmotion.mobilityid.EmaId] = None
```

If you have more detailed field information you can create an EmaId from the separate fields:

``` scala
scala> EmaId("NL", "TNM", "000122045")
res4: com.thenewmotion.mobilityid.EmaId = NL-TNM-000122045-U

scala> EmaId("NL", "TNM", "000122045", 'U')
res5: com.thenewmotion.mobilityid.EmaId = NL-TNM-000122045-U
```

This fails because of an invalid check digit:

``` scala
scala> EmaId("NL", "TNM", "000122045", 'X')
java.lang.IllegalArgumentException: requirement failed
```

This fails because of an illegal character:

```  scala
scala> EmaId("NL", "T|M", "000122045")
java.lang.IllegalArgumentException: requirement failed
```

Once you have an EmaId object you can use it for intelligent comparison (checking if DIN ID equals an ISO ID):

``` scala
scala> EmaId("NL-TNM-000122045") == EmaId("NL-TNM-012204-5")
res8: Boolean = true
```

Also you can print them in various formats (note that EMA ID cannot be represented in DIN format):

``` scala
scala> val emaId = EmaId("NL", "TNM", "012345678")
emaId: com.thenewmotion.mobilityid.EmaId = NL-TNM-012345678-W

scala> emaId.toDinString
res9: Option[String] = None

scala> emaId.toCompactString
res10: String = NLTNM012345678W

scala> emaId.toCompactStringWithoutCheckDigit
res11: String = NLTNM012345678

scala> emaId.toString
res12: String = NL-TNM-012345678-W
```

#### EVSE-ID ####

You can create an EvseId object from a string in ISO 15118-1 or DIN SPEC 91286 format:

``` scala
scala> import com.thenewmotion.mobilityid._
import com.thenewmotion.mobilityid._

scala> EvseId("NL*TNM*E01225045")
res1: Option[com.thenewmotion.mobilityid.EvseId] = Some(NL*TNM*E01225045)

scala> EvseId("NLTNME01225045")
res2: Option[com.thenewmotion.mobilityid.EvseId] = Some(NL*TNM*E01225045)

scala> EvseId("+31*734*7734634")
res3: Option[com.thenewmotion.mobilityid.EvseId] = Some(31*734*7734634)
```

This fails because of an illegal character:

``` scala
scala> EvseId("NL*T|M*E01225045")
res4: Option[com.thenewmotion.mobilityid.EvseId] = None
```

If you have more detailed field information you can create an EvseId from the separate fields:

``` scala
scala> EvseId("NL", "TNM", "E000122045")
res5: com.thenewmotion.mobilityid.EvseId = NL*TNM*E000122045

scala> EvseId("+31", "734", "000122045")
res6: com.thenewmotion.mobilityid.EvseId = +31*734*000122045
```

This fails because of an illegal character in the operator id for DIN format:

```  scala
scala> EvseId("+31", "7A4", "000122045")
java.lang.IllegalArgumentException: Invalid operatorId for DIN format
```

This fails because powerOutletId must begin with 'E' when using ISO format

```  scala
scala> EvseId("NL", "TNM", "000122045")
java.lang.IllegalArgumentException: Invalid powerOutletId for ISO format
```

You can pattern match on whether the EVSEId is of ISO or DIN format:

```scala
scala> def getFormat(e: EvseId) = e match { case _: EvseIdIso => "IsIso" case _: EvseIdDin => "IsDin" }
getFormat: (e: com.thenewmotion.mobilityid.EvseId)String

scala> val evseIdIso = EvseId("NL", "TNM", "E000122045")
evseIdIso: com.thenewmotion.mobilityid.EvseId = NL*TNM*E000122045

scala> getFormat(evseIdIso)
res7: String = IsIso

scala> val evseIdDin = EvseId("+31", "734", "000122045")
evseIdDin: com.thenewmotion.mobilityid.EvseId = +31*734*000122045

scala> getFormat(evseIdDin)
res8: String = IsDin

```

Also you can print them in full format or compact format (only ISO standard):

``` scala
scala> val evseIdIso = EvseId("NL", "TNM", "E000122045")
evseIdIso: com.thenewmotion.mobilityid.EvseId = NL*TNM*E000122045

scala> evseIdIso.toString
res9: String = NL*TNM*E000122045

scala> evseIdIso match { case e: EvseIdIso => e.toCompactString }
res10: String = NLTNME000122045

scala> val evseIdDin = EvseId("+31", "734", "000122045")
evseIdDin: com.thenewmotion.mobilityid.EvseId = +31*734*000122045

scala> evseIdDin.toString
res11: String = +31*734*000122045

```


##### Notes

It is *NOT* possible to compare two EvseId objects where one is DIN format the other is ISO, as the 2 formats use different country identifiers and different operator codes

### Documentation and getting help ###

This library is using the algorithm of check digit calculation for ISO 15118-1 Contract IDs described here:
http://www.ochp.eu/id-validator/e-mobility-ids_evcoid_check-digit-calculation_explanation/
