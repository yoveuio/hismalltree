package com.hismalltree.demo.flink

import com.fasterxml.jackson.annotation.JsonGetter
import org.apache.flink.api.common.eventtime.{WatermarkGenerator, WatermarkGeneratorSupplier, WatermarkStrategy}
import org.apache.flink.api.common.functions.{AggregateFunction, MapFunction}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

import java.time.Instant

object Demo {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)

    // generate watermarks per 5000ms
    env.getConfig.setAutoWatermarkInterval(5000L)

    val numbers: DataStream[(String, Int)] = env.fromElements(
      ("Alam", 12), ("Rose", 18), ("Tom", 21), ("Jerry", 21), ("Benny", 22)
    )

    val persons: DataStream[Person] = numbers.map(new Tuple2ToPersonMapper())
      .assignTimestampsAndWatermarks(new CustomWatermarksStrategy)

    persons.print()
    env.execute()
  }
}

class Tuple2ToPersonMapper extends MapFunction[(String, Int), Person] {

  override def map(value: (String, Int)): Person = Person(name = value._1, age = value._2)

}

@JsonGetter
case class Person
(
  var name: String = null,
  var age: Int = 0,
  var createAt: Long = Instant.now().toEpochMilli
)


class CustomWatermarksStrategy extends WatermarkStrategy[Person] {

  override def createWatermarkGenerator(context: WatermarkGeneratorSupplier.Context): WatermarkGenerator[Person] = {
    ???
  }

}

class AvgTempFunction extends AggregateFunction[(String, Double), (String, Double, Int), (String, Double)] {
  override def createAccumulator(): (String, Double, Int) = {
    ("", 0.0, 0)
  }

  override def add(value: (String, Double), accumulator: (String, Double, Int)): (String, Double, Int) = {
    (value._1, value._2 + accumulator._2, 1 + accumulator._3)
  }

  override def getResult(accumulator: (String, Double, Int)): (String, Double) = {
    (accumulator._1, accumulator._2 / accumulator._3)
  }

  override def merge(a: (String, Double, Int), b: (String, Double, Int)): (String, Double, Int) = {
    (a._1, a._2 + b._2, a._3 + b._3)
  }
}
