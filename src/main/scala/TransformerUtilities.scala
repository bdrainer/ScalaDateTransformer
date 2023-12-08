import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex

object TransformerUtilities {

  val Months = "January|February|March|April|May|June|July|August|September|October|November|December"
  val ShortMonths = "Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec"
  val YearRegEx = "\\d{2,4}"
  val MonthDayRegEx = "\\d{1,2}"
  val DayOfMonthOptions = "st|nd|rd|th"

  def dateToString(ldt: LocalDate, pattern: String): String = {
    ldt.format(DateTimeFormatter.ofPattern(pattern))
  }

  def convertDateFormat(dateString: String, sourcePattern: String, targetPattern: String): String = {
    LocalDate.parse(dateString, DateTimeFormatter.ofPattern(sourcePattern))
      .format(DateTimeFormatter.ofPattern(targetPattern))
  }

  /**
   * Converts a DateTimeFormatter pattern string into a regular expression.  The method assumes only date related
   * patterns are present in the string.  This includes only month (M, MM, MMM, MMMM), day of month (d, dd), and
   * year (Y, YY, YYY, YYYY).  No other patterns are considered, for example time patterns for hour, minute, and second.
   *
   * @param datePattern The date pattern.
   * @return A regular expression.
   */
  def convertToRegex(datePattern: String): Regex = {
    val pattern = datePattern
      .replace("st", "ST")
      .replace("nd", "ND")
      .replace("rd", "RD")
      .replace("th", "TH")
      .replaceAll("([d]{1,2})('ST'|'ND'|'RD'|'TH')?", s"\\\\d{1,2}($DayOfMonthOptions)?")
      .replaceAll("([YyLl]{1,4})", "\\\\d{2,4}")
      .replaceAll("([M]{4})", s"(${Months.toLowerCase})")
      .replaceAll("([M]{3})", s"(${ShortMonths.toLowerCase()})")
      .replaceAll("([M]{1,2})", "\\\\d{1,2}")
      .replace(ShortMonths.toLowerCase, ShortMonths)
      .replace(Months.toLowerCase, Months)

    s"($pattern)".r
  }

  def transform(fileString: String, sourcePattern: String, destinationPattern: String): String = {
    var text = fileString

    convertToRegex(sourcePattern).findAllIn(text).toList.foreach(stringToReplace => {
      text = text.replace(stringToReplace, convertDateFormat(stringToReplace, sourcePattern, destinationPattern))
    })

    text
  }

  def transform(fileLines: List[String], sourcePattern: String, destinationPattern: String): String = {
    var text : String = ""

    convertToRegex(sourcePattern).findAllIn(text).toList.foreach(stringToReplace => {
      text = text.replace(stringToReplace, convertDateFormat(stringToReplace, sourcePattern, destinationPattern))
    })

    text
  }
}
