import java.io.File


import scala.collection.mutable.ArrayBuffer
import MaxWords._


object LogsParser extends App {
  def parse(args: Array[String]) = {

    val wordsNum = args(0).trim.toInt
    val timeStart = args(1).trim.toLong
    val timeStop = args(3).trim.toLong
    val dirPath = Array[String]{"/var/logs"}

    val invalidPaths = validatePaths(dirPath)
    if (invalidPaths.size > 0) {
      throw new Exception("Hardcoded dir path is invalid : " + invalidPaths.mkString(" "))
    }

    val relevantFiles = getFilesInTimeWindow(dirPath, timeStart, timeStop)
    val sortedWordsMap = countWords(relevantFiles)
    if (args.last == "--debug") {
      printf("Included files for time window %s - %s are :\n%s\n", timeStart, timeStop, relevantFiles.mkString(" \n"))
    }

    printWordsCounters(sortedWordsMap, wordsNum)
  }

  def getFilesInTimeWindow(dirPath: Array[String], timeStart: Long, timeStop: Long): ArrayBuffer[File] = {
    val allFilesInPath = getAllFiles(dirPath)
    var relevantFiles = ArrayBuffer[File]()

    allFilesInPath.seq.foreach(file => {
      if (file.lastModified() >= timeStart && file.lastModified() <= timeStop) {
        relevantFiles += file
      }
    })

    relevantFiles
  }
}
