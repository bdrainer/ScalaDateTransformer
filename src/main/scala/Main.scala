import Control.using

import java.io.{FileNotFoundException, IOException}
import scala.io.Source

@main def transformDates(filePath: String, sourcePattern: String, destinationPattern: String): Unit =
  try {
    using(Source.fromFile(filePath)) { source =>
      val fileContents = source.getLines.mkString(sys.props("line.separator"))
      println(TransformerUtilities.transform(fileContents, sourcePattern, destinationPattern))
    }
  } catch {
    case e: FileNotFoundException => println(s"File $filePath was not found")
    case e: IOException => println(s"IOException reading file $filePath")
    case e: Exception => println(s"Unexpected error reformatting file")
  }
