package com.siha.study

/**
  * Created by 1002375 on 2017. 3. 2..
  */
object test extends App {
  val list = List(1, 2, 3, 4, 5)
  val pred = (a: Int) => 10 > a

  list.foldLeft(true)((a1, a2: Int) => {
    println(s"$a1, $a2")
    a1 && pred(a2)
  })

  list.synchronized()
  val t = thread{
    Thread.sleep(1000)
    println("new Thread running")
    Thread.sleep(1000)
  };
  println(s"thread: ${t.getName}")

  def thread(body: => Unit): Thread = {
    val t = new Thread {
      override def run = body
    }
    t.start()
    t
  }

  try {
    throw new NullPointerException
  } catch {
    case e: NullPointerException => {
      println(s"Null Exception ${e.getMessage}")
    }
  }
}
