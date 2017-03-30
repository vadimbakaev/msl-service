package models

/**
  * @author vadimbakaev
  */
object AirIndex extends Enumeration {
  type AirIndex = Value

  val NO2   = Value(101)
  val SO2   = Value(102)
  val O3    = Value(103)
  val CO    = Value(105)
  val C6H6  = Value(106)
  val PM2_5 = Value(107)
  val PM10  = Value(117)

}



