/**
 * 
 */
package com.kang.fastcluster.mr;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;

import com.kang.filter.keytype.IntPairWritable;


/**
 * 输入为<距离d_ij,<向量i编号，向量j编号>>
 * 根据距离dc阈值判断距离d_ij是否小于dc，符合要求则
 * 输出
 * 向量i编号，1
 * 向量j编号，1
 */
public class LocalDensityMapper extends Mapper<DoubleWritable, IntPairWritable, IntWritable, DoubleWritable> {

	private double dc;
	private String method =null;
	
	private IntWritable vectorId= new IntWritable();
	private DoubleWritable one= new DoubleWritable(1);
	
	@Override 
	public void setup(Context cxt){
		dc=cxt.getConfiguration().getDouble("DC", 0);
		method = cxt.getConfiguration().get("METHOD", "gaussian");
	}
	
	@Override
	public void map(DoubleWritable key,IntPairWritable value,Context cxt)throws InterruptedException,IOException{
		double distance= key.get();
		
		if(method.equals("gaussian")){
            one.set(Math.pow(Math.E, -(distance/dc)*(distance/dc)));//gaussian计算局部密度
        }
		
		if(distance<dc){//如果用户i和用户j的距离小于阀值，则输出两次，每次输出<id,1>
			vectorId.set(value.getFirst());
			cxt.write(vectorId, one);//i作为输出的key,数字1为value
			vectorId.set(value.getSecond());
			cxt.write(vectorId, one);//j作为输出的key，数字1为value
		}
	}

	
	
}
