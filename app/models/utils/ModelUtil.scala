package models.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
  * Created by rroche on 11/12/15.
  */
trait ModelUtil {
  val bbDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS")

  def bbStringToTimestamp(date: String): Timestamp = {
    new Timestamp(bbDateFormat.parse(date).getTime())
  }
}
