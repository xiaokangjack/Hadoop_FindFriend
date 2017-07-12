/**
 * 
 */
package com.kang.thread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kang.fastcluster.ClusterDataJob;
import com.kang.util.HUtils;
import com.kang.util.Utils;

/**
 * 分类操作线程
 */
public class RunCluster2 implements Runnable {

	private String input;
	private String output;
	private String delta;//距离阀值
	private String k;//聚类中心数
	
	private Logger log = LoggerFactory.getLogger(RunCluster2.class);
	@Override
	public void run() {
		input=input==null?HUtils.FILTER_PREPAREVECTORS:input;
		
		
		try {
			HUtils.clearCenter((output==null?HUtils.CENTERPATH:output));//清除输出目录
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		output=output==null?HUtils.CENTERPATHPREFIX:output+"/iter_";
		
		// 加一个操作，把/user/root/preparevectors里面的数据复制到/user/root/_center/iter_0/unclustered里面
		HUtils.copy(input,output+"0/unclustered");
		try {
			Thread.sleep(200);// 暂停200ms 
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		// 求解dc的阈值
//		double dc =dcs[0];//使用前端传递的值
		
		
		//每次循环执行分类时，阈值都是变化的，这里采取的方式是：
		//1. 计算聚类中心向量两两之间的距离，并按照距离排序，从小到大，每次循环取出距离的一半当做阈值，一直取到最后一个距离；
		//2. 当进行到K*(K-1)/2个距离时，即最后一个距离（K个聚类中心向量）后，下次循环的阈值设置为当前阈值翻倍，即乘以2；并计数，当再循环k次后，此阈值将不再变化；
		//3. 这样设置可以减少误判，同时控制循环的次数；
		
		// 读取聚类中心文件
		Map<Object,Object> vectorsMap= HUtils.readSeq(output+"0/clustered/part-m-00000", Integer.parseInt(k));
		double[][] vectors = HUtils.getCenterVector(vectorsMap);
		double[] distances= Utils.getDistances(vectors);//获取两两聚类中心向量的距离，并按照从小到大排序
		// 这里不使用传入进来的阈值
		
		
		
		int iter_i=0;
		int ret=0;
		double tmpDelta=0;
		int kInt = Integer.parseInt(k);
		try {
			do{//使用do-while循环
				if(iter_i>=distances.length){//distances.length=K*(K-1)/2
					// 当读取到最后一个向量距离时，使用如下方式计算阀值
					tmpDelta=Double.parseDouble(delta);
					while(kInt-->0){// 超过k次后就不再增大
						tmpDelta*=2;// 每次翻倍
					}
					delta=String.valueOf(tmpDelta);//最终的阀值
				}else{
					//距离数组未越界时，直接取读取距离的一半为阀值
					delta=String.valueOf(distances[iter_i]/2);
				}
				log.info("this is the {} iteration,with dc:{}",new Object[]{iter_i,delta});
				String[] ar={
						HUtils.getHDFSPath(output)+iter_i+"/unclustered",
						HUtils.getHDFSPath(output)+(iter_i+1),//output
						//HUtils.getHDFSPath(HUtils.CENTERPATHPREFIX)+iter_i+"/clustered/part-m-00000",//center file
						k,
						delta,
						String.valueOf((iter_i+1))
				};//初始化MapReduce运行参数
				try{
					ret = ToolRunner.run(HUtils.getConf(), new ClusterDataJob(), ar);
					if(ret!=0){
						log.info("ClusterDataJob failed, with iteration {}",new Object[]{iter_i});
						break;
					}	
				}catch(Exception e){
					e.printStackTrace();
				}
				iter_i++;
				HUtils.JOBNUM++;// 每次循环后加1

			}while(shouldRunNextIter());//通过方法shouldRunNextIter()来判断是否终止循环
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
		if(ret==0){
			log.info("All cluster Job finished with iteration {}",new Object[]{iter_i});
		}
		
	}
	
	/**
	 * 是否应该继续下次循环
	 * 直接使用分类记录数和未分类记录数来判断
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	private boolean shouldRunNextIter()  {
		
		if(HUtils.UNCLUSTERED==0||HUtils.CLUSTERED==0){//MapReduce任务运行时会修改HUtils.UNCLUSTERED和HUtils.CLUSTERED的值
			//当两者其中一个为0时，则表示所有数据已经聚类完成，则结束循环
			HUtils.JOBNUM-=2;// 不用监控 则减去2;
			return false;
		}
		return true;
		
	}
	
	public RunCluster2(){}
	
	public RunCluster2(String input,String output,String delta,String k){
		this.delta=delta;
		this.input=input;
		this.output=output;
		this.k=k;
	}
}
