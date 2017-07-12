/**
 * 
 */
package com.kang.fastcluster.mr;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import com.kang.util.Utils;


/**
 * map端的输出格式为<i,list(1,1,1---)>,i为用户id号
 * reduce任务就是把list(1,1,1---)中的所有“1”相加即可。
 */
public class LocalDensityReducer extends
		Reducer<IntWritable, DoubleWritable, IntWritable, DoubleWritable> {
	private DoubleWritable sumAll = new DoubleWritable();
	
	@Override
	public void reduce(IntWritable key, Iterable<DoubleWritable> values,Context cxt)
	throws IOException,InterruptedException{
		double sum =0;
		for(DoubleWritable v:values){
			sum+=v.get();//每遍历一次加1
		}
		sumAll.set(sum);// 统计总数
		cxt.write(key, sumAll);
		Utils.simpleLog("vectorI:"+key.get()+",density:"+sumAll);
	}
}
