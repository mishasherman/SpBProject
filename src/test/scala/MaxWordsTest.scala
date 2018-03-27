import org.scalatest.FunSuite

class MaxWordsTest extends FunSuite {
  test("max_words_in_path") {
    MaxWords.calculate(Array[String]("5", "/var/logs"))
  }
}
