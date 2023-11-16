package com.hismalltree.demo.flink.source

case class DataRow(fields: Array[Object], rowKind: DataRowKind)

enum DataRowKind {
  case Insert
  case UPDATE
  case DELETE
}
