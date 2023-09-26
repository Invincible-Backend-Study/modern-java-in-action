package org.example

object Bear {
  def main(args: Array[String]): Unit = {
    2 to 6 foreach { n => println(s"Hello ${n} bottles of beer") }


    val numbers = Set(2, 5, 3);
    var newNumbers = numbers + 8;

    println(newNumbers)
    println(numbers)


    val fileLines = List("aaaaaaaaaaaaaaaaaa", "b", "c")
    val linesLongUpper = (fileLines) filter (_.length > 10) map (_.toUpperCase())

    println(linesLongUpper)

    val book = (2018, "모던 자바 인 액션")

    val optionNum = Option(("a", "b"));

    def multiply(x: Int, y: Int) = x * y

    def multiplyCurry(x: Int)(y: Int) = x * y;

    val multiplyByTwo: Int => Int = multiplyCurry(2);
    val r = multiplyByTwo(10);

    class Student(var name: String, var id: Int)
    val s = new Student("Raoul", 1)

  }

}
