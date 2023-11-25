package com.hismalltree.demo.scala

class Variance {

}

sealed trait Item:
  def productNumber: String


sealed trait Buyable extends Item:
  def price: Int


sealed trait Book extends Buyable:
  def isbn: String


trait Pipeline[T]:
  def process(t: T): T

trait Producer[+T]:
  def make: T

trait Consumer[-T]:
  def take(t: T): Unit

def makeTwo(p: Producer[Buyable]): Int =
  p.make.price + p.make.price

def consume(c: Consumer[Buyable], buyable: Buyable): Unit =
  c.take(buyable)
