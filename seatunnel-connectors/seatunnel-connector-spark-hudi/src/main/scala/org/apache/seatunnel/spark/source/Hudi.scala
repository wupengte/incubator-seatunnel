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
package org.apache.seatunnel.spark.source

import scala.collection.JavaConversions._

import org.apache.seatunnel.common.config.CheckResult
import org.apache.seatunnel.spark.SparkEnvironment
import org.apache.seatunnel.spark.batch.SparkBatchSource
import org.apache.spark.sql.{Dataset, Row}

class Hudi extends SparkBatchSource {

  override def prepare(env: SparkEnvironment): Unit = {}

  override def checkConfig(): CheckResult = {
    val requiredOptions = Seq("hoodie.datasource.read.paths")
    var missingOptions = new StringBuilder
    requiredOptions.map(opt =>
      if (!config.hasPath(opt)) {
        missingOptions.append(opt).append(",")
      })
    missingOptions.isEmpty match {
      case true =>
        new CheckResult(true, "")
      case false =>
        missingOptions = missingOptions.deleteCharAt(missingOptions.length - 1)
        new CheckResult(false, s"please specify [$missingOptions] as non-empty string")
    }
  }

  override def getData(env: SparkEnvironment): Dataset[Row] = {

    val reader = env.getSparkSession.read.format("org.apache.hudi")
    for (e <- config.entrySet()) {
       reader.option(e.getKey, e.getValue.toString)
     }

    reader.load(config.getString("hoodie.datasource.read.paths"))
  }
}
