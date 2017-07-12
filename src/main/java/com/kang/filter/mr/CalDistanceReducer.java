/**
 * 
 */
package com.kang.filter.mr;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;

import com.kang.filter.FilterCounter;
import com.kang.filter.keytype.IntPairWritable;

/**
 * 计算向量距离reduce方法
 */
public class CalDistanceReducer extends
		Reducer<DoubleWritable, IntPairWritable, DoubleWritable, IntPairWritable> {

	public void reduce(DoubleWritable key,Iterable<IntPairWritable> values,Context cxt)throws InterruptedException,IOException{
		for(IntPairWritable v:values){
			cxt.getCounter(FilterCounter.REDUCE_COUNTER).increment(1);
			cxt.write(key, v);
		}
	}
}
