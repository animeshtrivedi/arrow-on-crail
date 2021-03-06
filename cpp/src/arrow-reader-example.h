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

#ifndef BENCHMARK_ARROW_CPP_ARROWREADEREXAMPLE_H


#include <arrow/memory_pool.h>
#include <arrow/io/file.h>
#include <arrow/status.h>
#include <arrow/ipc/reader.h>
#include "BenchmarkResult.h"

class ArrowReadExample : public BenchmarkResult {
private:
    const char *_file_name;
    std::shared_ptr<arrow::Schema> _sptr_schema;
    std::shared_ptr<arrow::ipc::RecordBatchFileReader> _sptr_file_reader;
    std::shared_ptr<arrow::io::MemoryMappedFile> _sptr_mmaped_file;

    arrow::Status process_batch(std::shared_ptr<arrow::RecordBatch> batch);
public:
    explicit ArrowReadExample(const char* filename);
    arrow::Status init();
    arrow::Status debug_show();
    arrow::Status read();
    arrow::Status consume_int32(std::shared_ptr<arrow::Array> col, int64_t num_rows);
    arrow::Status consume_int64(std::shared_ptr<arrow::Array> col, int64_t num_rows);
    arrow::Status consume_float8(std::shared_ptr<arrow::Array> col, int64_t num_rows);
    //TODO: do a binary column example
};
#define BENCHMARK_ARROW_CPP_ARROWREADEREXAMPLE_H

#endif //BENCHMARK_ARROW_CPP_ARROWREADEREXAMPLE_H
