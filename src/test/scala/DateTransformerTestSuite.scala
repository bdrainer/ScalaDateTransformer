import TransformerUtilities.{DayOfMonthOptions, MonthDayRegEx, Months, ShortMonths, YearRegEx, convertDateFormat, convertToRegex, dateToString}

import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.format.DateTimeParseException

class DateTransformerTestSuite extends munit.FunSuite {

  test("should throws FileNotFoundException") {
    val exception = intercept[FileNotFoundException](
      TransformerUtilities.transformFile(
        "files/filenotfound.txt", "MMMM d'th', yyyy", "yyyy-MM-dd")
    )
    assert(clue(exception.getMessage).contains("files/filenotfound.txt"))
  }

  test("should throws DateTimeParseException") {
    val exception = intercept[DateTimeParseException](
      TransformerUtilities.transformFile(
        "files/file1.txt", "MMMM d'th', yyyy", "yyyy-MM-dd")
    )
    assert(clue(exception.getMessage).contains("could not be parsed"))
  }

  test("should parse year, month, and day from a LocaleDate instance") {
    val ldt = LocalDate.of(2023, 3, 7)

    assertEquals(dateToString(ldt, "YYYY-M-d"), "2023-3-7")

    assertEquals(dateToString(ldt, "YYYY"), "2023")
    assertEquals(dateToString(ldt, "yyyy"), "2023")
    assertEquals(dateToString(ldt, "YY"), "23")
    assertEquals(dateToString(ldt, "yy"), "23")
    assertEquals(dateToString(ldt, "YYY"), "2023")
    assertEquals(dateToString(ldt, "yyy"), "2023")
    assertEquals(dateToString(ldt, "Y"), "2023")
    assertEquals(dateToString(ldt, "y"), "2023")

    assertEquals(dateToString(ldt, "M"), "3")
    assertEquals(dateToString(ldt, "MM"), "03")
    assertEquals(dateToString(ldt, "MMM"), "Mar")
    assertEquals(dateToString(ldt, "MMMM"), "March")

    assertEquals(dateToString(ldt, "d"), "7")
    assertEquals(dateToString(ldt, "d'th'"), "7th")
    assertEquals(dateToString(ldt, "dd"), "07")
    assertEquals(dateToString(ldt, "dd'th'"), "07th")
  }

  test("should convert date source pattern to a regular expression") {
    val yearMonthDayRegEx = s"($YearRegEx-$MonthDayRegEx-$MonthDayRegEx($DayOfMonthOptions)?)".r.toString()

    assertEquals(convertToRegex("Y-MM-dd").toString(), yearMonthDayRegEx)
    assertEquals(convertToRegex("YY-MM-dd").toString(), yearMonthDayRegEx)
    assertEquals(convertToRegex("YYY-MM-dd").toString(), yearMonthDayRegEx)
    assertEquals(convertToRegex("YYYY-MM-dd").toString(), yearMonthDayRegEx)

    assertEquals(convertToRegex("YYYY-M-dd").toString(), yearMonthDayRegEx)
    assertEquals(convertToRegex("YYYY-MM-dd").toString(), yearMonthDayRegEx)
    assertEquals(convertToRegex("YYYY-MMM-dd").toString(),
      s"($YearRegEx-($ShortMonths)-$MonthDayRegEx($DayOfMonthOptions)?)".r.toString())
    assertEquals(convertToRegex("YYYY-MMMM-dd").toString(),
      s"($YearRegEx-($Months)-$MonthDayRegEx($DayOfMonthOptions)?)".r.toString())

    assertEquals(convertToRegex("YYYY-MM-d").toString(), yearMonthDayRegEx)

    assertEquals(convertToRegex("MMM dd, YYYY").toString(),
      s"(($ShortMonths) $MonthDayRegEx($DayOfMonthOptions)?, $YearRegEx)".r.toString())
    assertEquals(convertToRegex("MMMM dd, YYYY").toString(),
      s"(($Months) $MonthDayRegEx($DayOfMonthOptions)?, $YearRegEx)".r.toString())
    assertEquals(convertToRegex("MMMM dd'th', YYYY").toString(),
      s"(($Months) $MonthDayRegEx($DayOfMonthOptions)?, $YearRegEx)".r.toString())
  }

  test("should convert date string from source pattern to destination pattern") {
    assertEquals(
      convertDateFormat("1955-11-05", "yyyy-MM-dd", "MMMM dd, yyyy"),
      "November 05, 1955")
    assertEquals(
      convertDateFormat("Mar 07, 1975", "MMM dd, yyyy", "yyyy-MMMM-d"),
      "1975-March-7")
    assertEquals(
      convertDateFormat("November 05, 1955", "MMMM dd, yyyy", "yyyy-MM-dd"),
      "1955-11-05")
    assertEquals(
      convertDateFormat("9/6/78", "M/d/yy", "MMMM dd, yyyy"),
      "September 06, 2078")
    assertEquals(
      convertDateFormat("10/6/78", "M/d/yy", "MMMM dd, yyyy"),
      "October 06, 2078")
    assertEquals(
      convertDateFormat("September 06, 1978", "MMMM dd, yyyy", "M/d/yy"),
      "9/6/78")
    assertEquals(
      convertDateFormat("Sep 06, 1978", "MMM dd, yyyy", "M/d/yy"),
      "9/6/78")
    assertEquals(
      convertDateFormat("2008-04-26", "yyyy-MM-dd", "MMMM dd'th', yyyy"),
      "April 26th, 2008")
    assertEquals(
      convertDateFormat("April 1st, 2008", "MMMM d'st', yyyy", "yyyy-MM-dd"),
      "2008-04-01")
    assertEquals(
      convertDateFormat("April 2nd, 2008", "MMMM d'nd', yyyy", "yyyy-MM-dd"),
      "2008-04-02")
    assertEquals(
      convertDateFormat("April 3rd, 2008", "MMMM d'rd', yyyy", "yyyy-MM-dd"),
      "2008-04-03")
    assertEquals(
      convertDateFormat("April 26th, 2008", "MMMM d'th', yyyy", "yyyy-MM-dd"),
      "2008-04-26")
  }

  test("should reformat text with source pattern \"MMMM d'st', yyyy\" and destination pattern 'yyyy-MM-dd'") {
    val expected = "2008-04-01 was a day in time."
    val obtained = TransformerUtilities.transform("April 1st, 2008 was a day in time.",
      "MMMM d'st', yyyy", "yyyy-MM-dd")
    assertEquals(obtained, expected)
  }

  test("should reformat text with source pattern \"MMMM d'nd', yyyy\" and destination pattern 'yyyy-MM-dd'") {
    val expected = "2008-04-02 was a day in time."
    val obtained = TransformerUtilities.transform("April 2nd, 2008 was a day in time.",
      "MMMM d'nd', yyyy", "yyyy-MM-dd")
    assertEquals(obtained, expected)
  }

  test("should reformat text with source pattern \"MMMM d'rd', yyyy\" and destination pattern 'yyyy-MM-dd'") {
    val expected = "2008-04-03 was a day in time."
    val obtained = TransformerUtilities.transform("April 3rd, 2008 was a day in time.",
      "MMMM d'rd', yyyy", "yyyy-MM-dd")
    assertEquals(obtained, expected)
  }

  test("should reformat text with source pattern \"MMMM d'th', yyyy\" and destination pattern 'yyyy-MM-dd'") {
    val expected = "2008-04-26 was a day in time."
    val obtained = TransformerUtilities.transform("April 26th, 2008 was a day in time.",
      "MMMM d'th', yyyy", "yyyy-MM-dd")
    assertEquals(obtained, expected)
  }

  test("should reformat text with source pattern 'M/d/yy' and destination pattern 'MMMM dd, yyyy'") {
    val expected =
      """Dear diary,
        |
        |On September 06, 2078 a really neat thing happened.  But on October 10, 2003 thing went poorly.
        |I am hopeful that 10/11 will be better.""".stripMargin

    val text =
      """Dear diary,
        |
        |On 9/6/78 a really neat thing happened.  But on 10/10/03 thing went poorly.
        |I am hopeful that 10/11 will be better.""".stripMargin

    val obtained = TransformerUtilities.transform(text, "M/d/yy", "MMMM dd, yyyy")
    assertEquals(obtained, expected)
  }
}
