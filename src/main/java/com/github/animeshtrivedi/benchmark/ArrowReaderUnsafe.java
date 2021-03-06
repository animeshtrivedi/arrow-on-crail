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
package com.github.animeshtrivedi.benchmark;

import org.apache.arrow.vector.*;

public class ArrowReaderUnsafe extends ArrowReader {

    ArrowReaderUnsafe(){}

    private boolean isValid(long baseAddress, int rowIndex){
//        final int byteIndex = rowIndex >> 3;
//        final byte b = Platform.getByte(null, baseAddress+byteIndex);
//        final int bitIndex = rowIndex & 7;
//        // v1 - default from the arrow source code
//        //return Long.bitCount(b & (1L << bitIndex)) == 0;
//        //v2
//        return (b & (1 << bitIndex)) != 0;

        return (Platform.getByte(null, baseAddress + (rowIndex >> 3)) & (1 << (rowIndex & 7))) != 0;
    }

    final public void consumeInt4(IntVector vector) {
        final int valCount = vector.getValueCount();
        final long valididtyAddress = vector.getValidityBufferAddress();
        long dataAddress = vector.getDataBufferAddress();
        // this has lots of calculations
        final boolean allValid = vector.getNullCount() == 0;

        long totalIntx = 0, checksum = 0;
        if(allValid){
            for (int i = 0; i < valCount; i++) {
                totalIntx++;
                checksum += Platform.getInt(null, dataAddress);
                dataAddress += 4;//IntVector.TYPE_WIDTH;
            }
        } else {
            for (int i = 0; i < valCount; i++) {
                if(isValid(valididtyAddress, i)) {
                    totalIntx++;
                    checksum += Platform.getInt(null, dataAddress);
                }
                dataAddress += 4;//IntVector.TYPE_WIDTH;
            }
        }
        this.intCount+=totalIntx;
        this.checksum+=checksum;
    }

    final public void _consumeInt4(IntVector vector) {
        if(vector.getNullCount() == 0 || true)
            fast_consumeInt4(vector);
        else
            slow_consumeInt4(vector);
    }

    private void fast_consumeInt4(IntVector vector) {
        final int valCount = vector.getValueCount();
        long dataAddress = vector.getDataBufferAddress();
        long checksum = 0;
        for (int i = 0; i < valCount; i++) {
            checksum += Platform.getInt(null, dataAddress);
            dataAddress += 4;//IntVector.TYPE_WIDTH;
        }
        this.intCount+=valCount;
        this.checksum+=checksum;
    }

    private void slow_consumeInt4(IntVector vector) {
        final int valCount = vector.getValueCount();
        final long valididtyAddress = vector.getValidityBufferAddress();
        final byte[] map = {1, 2, 4, 8, 16, 32, 64, (byte) 128};
        long dataAddress = vector.getDataBufferAddress();
        long totalIntx = 0, checksum = 0;
        for (int i = 0; i < valCount; i++) {
            if((Platform.getByte(null, valididtyAddress + (i >> 3)) & map[(i & 7)]) != 0) {
                totalIntx++;
                checksum += Platform.getInt(null, dataAddress);
            }
            dataAddress += 4;//IntVector.TYPE_WIDTH;
        }
        this.intCount+=totalIntx;
        this.checksum+=checksum;
    }

    final protected void consumeBigInt(BigIntVector vector) {
        int valCount = vector.getValueCount();
        long valididtyAddress = vector.getValidityBufferAddress();
        long dataAddress = vector.getDataBufferAddress();
        for(int i = 0; i < valCount; i++) {
            if (isValid(valididtyAddress, i)) {
                this.longCount++;
                this.checksum += Platform.getLong(null, dataAddress);
            }
            dataAddress += BigIntVector.TYPE_WIDTH;
        }
    }

    final protected void consumeFloat4(Float4Vector vector) {
        int valCount = vector.getValueCount();
        long valididtyAddress = vector.getValidityBufferAddress();
        long dataAddress = vector.getDataBufferAddress();
        for(int i = 0; i < valCount; i++) {
            if (isValid(valididtyAddress, i)) {
                this.float4Count++;
                this.checksum += Platform.getFloat(null, dataAddress);
            }
            dataAddress += Float4Vector.TYPE_WIDTH;
        }
    }

    final protected void consumeFloat8(Float8Vector vector) {
        int valCount = vector.getValueCount();
        long valididtyAddress = vector.getValidityBufferAddress();
        long dataAddress = vector.getDataBufferAddress();
        for (int i = 0; i < valCount; i++) {
            if (isValid(valididtyAddress, i)) {
                this.float8Count++;
                this.checksum += Platform.getDouble(null, dataAddress);
            }
            dataAddress += Float8Vector.TYPE_WIDTH;
        }
    }

    final protected void consumeBinary(VarBinaryVector vector) {
        //TODO: this is not tested yet
        int valCount = vector.getValueCount();
        long valididtyAddress = vector.getValidityBufferAddress();
        long dataAddress = vector.getDataBufferAddress();
        long offsetAddress = vector.getOffsetBufferAddress();
        for (int i = 0; i < valCount; i++) {
            if (isValid(valididtyAddress, i)) {
                int start = Platform.getInt(null, offsetAddress);
                int length = Platform.getInt(null, offsetAddress + BaseVariableWidthVector.OFFSET_WIDTH) - start;
                this.binaryCount++;
                this.checksum += length;
                this.binarySizeCount+=length;
                //get binary play load from the data address
                //this.valueBuffer.getBytes(start, byte[], 0, dataLength);
            }
            offsetAddress+=(BaseVariableWidthVector.OFFSET_WIDTH);
        }
    }

}
