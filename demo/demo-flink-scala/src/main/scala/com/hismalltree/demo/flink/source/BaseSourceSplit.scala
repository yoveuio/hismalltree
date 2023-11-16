package com.hismalltree.demo.flink.source

import org.apache.flink.api.connector.source.SourceSplit
 class BaseSourceSplit extends SourceSplit {

   override def splitId(): String = {
     "1"
   }

}
