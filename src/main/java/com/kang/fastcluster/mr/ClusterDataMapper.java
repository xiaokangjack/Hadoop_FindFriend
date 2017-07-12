/**
 * 
 */
package com.kang.fastcluster.mr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kang.fastcluster.ClusterCounter;
import com.kang.filter.keytype.DoubleArrIntWritable;
import com.kang.util.HUtils;


/**
 * 输入：
 *     id  ,用户有效向量
 * 输出：
 *     id_i, <type_i,用户有效向量>
 */

// Mapper 的输出有两个
public class ClusterDataMapper extends Mapper<IntWritable, DoubleArrIntWritable, IntWritable, DoubleArrIntWritable> {

	private Logger log = LoggerFactory.getLogger(ClusterDataMapper.class);
//	private String center = null;
//	private int k =-1;
	private double dc =0.0;
	private int iter_i =0;
	private int start =0;
	private DoubleArrIntWritable typeDoubleArr = new DoubleArrIntWritable();
	private IntWritable vectorI = new IntWritable();
	
	private MultipleOutputs<IntWritable,DoubleArrIntWritable> out;

	@Override
	public void setup(Context cxt){
//		center = cxt.getConfiguration().get("CENTER");
//		k = cxt.getConfiguration().getInt("K", 3);
		dc = cxt.getConfiguration().getDouble("DC", Double.MAX_VALUE);
		iter_i=cxt.getConfiguration().getInt("ITER_I", 0);
		start=iter_i!=1?1:0;
		out = new MultipleOutputs<IntWritable,DoubleArrIntWritable>(cxt); 
		cxt.getCounter(ClusterCounter.CLUSTERED).increment(0);//初始化已分类个数为0
		cxt.getCounter(ClusterCounter.UNCLUSTERED).increment(0);//初始化未分类个数为0
		
		log.info("第{}次循环...",iter_i);
	}
	
	@Override
	public void map(IntWritable key,DoubleArrIntWritable  value,Context cxt){
		double[] inputI= value.getDoubleArr();
		
		// hdfs
		Configuration conf = cxt.getConfiguration();
		FileSystem fs = null;
		Path path = null;
		
		SequenceFile.Reader reader = null;
		try {
			fs = FileSystem.get(conf);
			// read all before center files 
			String parentFolder =null;
			double smallDistance = Double.MAX_VALUE;
			int smallDistanceType=-1;
			double distance;
			
			// if iter_i !=0,then start i with 1,else start with 0
			for(int i=start;i<iter_i;i++){// all files are clustered points
				
				parentFolder=HUtils.CENTERPATH+"/iter_"+i+"/clustered";
				RemoteIterator<LocatedFileStatus> files=fs.listFiles(new Path(parentFolder), false);
				
				while(files.hasNext()){
					path = files.next().getPath();
					if(!path.toString().contains("part")){
						continue; // 只读取文件名包含“part”的文件
					}
					reader = new SequenceFile.Reader(conf, Reader.file(path),
							Reader.bufferSize(4096), Reader.start(0));
					IntWritable dkey = (IntWritable) ReflectionUtils.newInstance(
							reader.getKeyClass(), conf);
					DoubleArrIntWritable dvalue = (DoubleArrIntWritable) ReflectionUtils.newInstance(
							reader.getValueClass(), conf);
					while (reader.next(dkey, dvalue)) {// 遍历文件
						distance = HUtils.getDistance(inputI, dvalue.getDoubleArr());//获取距离
					
						if(distance>dc){// 距离大于阀值，不处理
							continue;
						}
						// 这里只要找到离该点最近的点并且其distance<=dc 则该点的所属聚类中心就找到了
						if(distance<smallDistance){
							smallDistance=distance;
							smallDistanceType=dvalue.getIdentifier();//找到其分组对应的type
						}
						
					}// while
				}// while
			}// for
					
			vectorI.set(key.get());// 用户id
			typeDoubleArr.setValue(inputI,smallDistanceType);
			
			if(smallDistanceType!=-1){
				log.info("clustered-->vectorI:{},typeDoubleArr:{}",new Object[]{vectorI,typeDoubleArr.toString()});
				cxt.getCounter(ClusterCounter.CLUSTERED).increment(1);//分类计数加1
				out.write("clustered", vectorI, typeDoubleArr,"clustered/part");	
			}else{
				log.info("unclustered---->vectorI:{},typeDoubleArr:{}",new Object[]{vectorI,typeDoubleArr.toString()});
				cxt.getCounter(ClusterCounter.UNCLUSTERED).increment(1);//未分类计数加1
				out.write("unclustered", vectorI, typeDoubleArr,"unclustered/part");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(reader);
		}

	}
	

	@Override
	public void cleanup(Context cxt){
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
