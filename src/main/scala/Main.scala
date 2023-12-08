import java.io.{FileNotFoundException, IOException}
import java.time.format.DateTimeParseException
import scala.io.Source
import scala.util.Using

@main def transformDates(filePath: String, sourcePattern: String, destinationPattern: String): Unit =
  try {
    Using.resource(Source.fromFile(filePath)) { source =>
      println(TransformerUtilities.transformFile(filePath, sourcePattern, destinationPattern))
    }
  } catch {
    case e: FileNotFoundException => println(s"File $filePath was not found")
    case e: IOException => println(s"Error reading file $filePath - message: ${e.getMessage}")
    case e: DateTimeParseException => println(s"Error transforming dates with sourcePattern: $sourcePattern, " +
      s"destinationPattern: $destinationPattern - message: ${e.getMessage}")
    case e: Exception => println(s"Unexpected error transforming file $filePath - message: ${e.getMessage}")
  }

