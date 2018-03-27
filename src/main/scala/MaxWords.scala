import java.io.{BufferedInputStream, File, FileInputStream}
import java.util.zip.GZIPInputStream


import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object MaxWords extends App {

  def calculate(args: Array[String]) = {
    var wordsNum = 0
    args.length match {
      case 0 => throw new Exception("Arguments missing, words number and file/dir paths list")
      case 1 => throw new Exception("Argument missing, file/dir paths list")
      case _ => println("Validating arguments ...")
    }

    try {
      wordsNum = args(0).trim.toInt
    } catch {
      case e: Exception => throw new Exception("Max word number argument is invalid " + e.printStackTrace())
    }

    printf("Words number entered is : %s\n", wordsNum)
    assert(wordsNum >= 0)

    val paths = args.drop(1)
    printf("Given paths are : \n\t%s\n", paths.mkString("\n\t"))


    val invalidPaths = validatePaths(paths)
    if (invalidPaths.size > 0) {
      throw new Exception("File/dir paths are invalid : " + invalidPaths.mkString(" "))
    }

    val allFilesPath = getAllFiles(paths)
    printf("Files found are : \n\t%s\n", allFilesPath.mkString("\n\t"))

    val sortedWordsMap = countWords(allFilesPath)
    printWordsCounters(sortedWordsMap, wordsNum)

  }

  def validatePaths(paths: Array[String]): ArrayBuffer[String] = {
    var invalidPaths = ArrayBuffer[String]()
    for (path <- paths) {
      val file = new File(path)
      if (!file.exists()) {
        invalidPaths += path
      }
    }
    invalidPaths
  }

  def getAllFiles(inputPaths: Array[String]): ArrayBuffer[File] = {
    var allFiles = new ArrayBuffer[File]()
    for (path <- inputPaths) {
      var file = new File(path)
      if (file.isFile) {
        allFiles += file
      } else if (file.isDirectory) {
        allFiles = allFiles ++ getDirFiles(file).to[ArrayBuffer]
      }
    }
    allFiles
  }

  def getDirFiles(folder: File): Array[File] = {
    if (!folder.canRead) {
      return new Array[File](0)
    }
    val these = folder.listFiles
    these.filter(_.isFile) ++ these.filter(_.isDirectory).flatMap(getDirFiles)
  }

  def countWords(files: ArrayBuffer[File]): mutable.LinkedHashMap[String, Int] = {
    var wordsOccMap = new mutable.LinkedHashMap[String, Int]()
    val concurWordCounters: Future[Seq[mutable.HashMap[String, Int]]] = Future.traverse(files) { file: File =>
      Future {
        countWordsInFile(file)
      }
    }
    val wordsOccMapList : Seq[mutable.HashMap[String, Int]] = Await.result(concurWordCounters, 300.seconds)
    wordsOccMapList.seq.foreach(ctrMap =>
      ctrMap.map{case (word, counter) =>
        if (wordsOccMap.contains(word)) {
          wordsOccMap(word) += counter
        } else {
          wordsOccMap += (word -> counter)
        }
      })

    mutable.LinkedHashMap(wordsOccMap.toSeq.sortWith(_._2 > _._2): _*)
  }

  def countWordsInFile(file: File) : mutable.HashMap[String, Int] = {
    val words = """([A-Za-z])+""".r
    val filePath = file.getPath
    var wordOccMap = new mutable.HashMap[String, Int]()
    if (file.length() != 0) {
      if (filePath.endsWith(".gz")) {
        val in = new GZIPInputStream(new BufferedInputStream(new FileInputStream(filePath)))
        io.Source.fromInputStream(in).getLines.flatMap(words.findAllIn).toList.
          foreach(word =>
            if (wordOccMap.contains(word)) {
              wordOccMap(word) += 1
            } else {
              wordOccMap += (word -> 1)
            }
          )
      } else {
        io.Source.fromFile(filePath, "UTF-8").getLines.flatMap(words.findAllIn).toList.
          foreach(word =>
            if (wordOccMap.contains(word)) {
              wordOccMap(word) += 1
            } else {
              wordOccMap += (word -> 1)
            }
          )
      }
    }
    return wordOccMap
  }


  def printWordsCounters(wordsMap: mutable.LinkedHashMap[String, Int], num: Int) = {
    var max = num
    var counter = 1
    if (wordsMap.size == 0) {
      println("No words found in files path")
    } else {
      if (wordsMap.size < num) {
        max = wordsMap.size
        printf("Required to print %s words counters, but actual number of words is %s\n", num, max)
      }
      printf("Maximum %s words:\n", max)
      for ((k, v) <- wordsMap if counter <= max) {
        printf("Word: %s, occurred: %s times\n", k, v)
        counter += 1
      }
    }
  }

}
