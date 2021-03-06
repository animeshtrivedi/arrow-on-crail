/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.animeshtrivedi.benchmark

import java.text.NumberFormat
import java.util.Locale

import org.apache.hadoop.fs.{FileStatus, Path}

/**
  * Created by atr on 11.09.18.
  */
object Utils {
  def ok(path:Path):Boolean = {
    val fname = path.getName
    fname(0) != '_' && fname(0) != '.'
  }

  def sizeToSizeStr2(size: Long): String = {
    val kbScale: Long = 1024
    val mbScale: Long = 1024 * kbScale
    val gbScale: Long = 1024 * mbScale
    val tbScale: Long = 1024 * gbScale
    if (size > tbScale) {
      twoLongToDoubleDiv(size , tbScale) + " TiB"
    } else if (size > gbScale) {
      twoLongToDoubleDiv(size , gbScale)  + " GiB"
    } else if (size > mbScale) {
      twoLongToDoubleDiv(size , mbScale) + " MiB"
    } else if (size > kbScale) {
      twoLongToDoubleDiv(size, kbScale) + " KiB"
    } else {
      size + "B"
    }
  }

  def twoLongToDoubleDiv(l1:Long, l2:Long):Double = {
    BigDecimal(l1.toDouble / l2.toDouble).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def enumerateWithSize(directoryName:String):Array[(String, Long)] = {
    if(directoryName != null) {
      val path = new Path(directoryName)
      val conf = new org.apache.hadoop.conf.Configuration()
      val fileSystem = path.getFileSystem(conf)
      // we get the file system
      val fileStatus: Array[FileStatus] = fileSystem.listStatus(path)
      val files = fileStatus.map(_.getPath).filter(ok).toList
      files.map(fx => (fx.toString, fileSystem.getFileStatus(fx).getLen)).toArray
    } else {
      /* this will happen for null io */
      List[(String, Long)]().toArray
    }
  }

  def commaLongNumber(num:Long):String = {
    NumberFormat.getNumberInstance(Locale.US).format(num)
  }
}
