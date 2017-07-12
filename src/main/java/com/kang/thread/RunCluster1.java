/**
 * 
 */
package com.kang.thread;

import org.apache.hadoop.util.ToolRunner;

import com.kang.fastcluster.DeltaDistanceJob;
import com.kang.fastcluster.LocalDensityJob;
import com.kang.fastcluster.SortJob;
import com.kang.util.HUtils;
import com.kang.util.Utils;

/**
 * 1. 寻找每个向量的局部密度
 * 2. 寻找每个向量的最远距离
 */
public class RunCluster1 implements Runnable {

	private String input;//输入路径
	private String numReducerDensity;//局部密度MR reduce数目
	private String numReducerDistance;//最短距离MR reduce数目
	private String numReducerSort;//排序MR reduce数目
	private String dc;//距离阀值
	private String method;//密度计算方法
	
	public RunCluster1(String input,String dc,String method,String numReducerDensity,
			String numReducerDistance,String numReducerSort){
		this.input=input;
		this.dc=dc;
		this.method=method;
		this.numReducerDensity=numReducerDensity;
		this.numReducerDistance=numReducerDistance;
		this.numReducerSort=numReducerSort;
	}
	@Override
	public void run() {
		String [] args =new String[]{
				HUtils.getHDFSPath(input),
				HUtils.getHDFSPath(HUtils.LOCALDENSITYOUTPUT),
				dc,
				method,
				numReducerDensity
		};//初始化计算局部密度MR的参数
		try {
			int ret=
			ToolRunner.run(HUtils.getConf(), new LocalDensityJob(),args );//计算局部密度
			if(ret!=0){
				Utils.simpleLog("LocalDensityJob任务运行失败！");
				return ;
			}
			Thread.sleep(3000);// 等待3秒时间
			args=new String[]{
					HUtils.getHDFSPath(input),// 使用距离计算后的路径作为输入
					HUtils.getHDFSPath(HUtils.DELTADISTANCEOUTPUT),
					HUtils.getHDFSPath(HUtils.LOCALDENSITYOUTPUT),
					numReducerDistance
			};//初始化计算最短距离MR的参数
			
			ret=ToolRunner.run(HUtils.getConf(), new DeltaDistanceJob(), args);//计算最短距离
			if(ret!=0){
				Utils.simpleLog("DeltaDistanceJob任务运行失败！");
				return ;
			}
			
			Thread.sleep(3000);// 等待3秒时间
			
			args=new String[]{
					HUtils.getHDFSPath(HUtils.DELTADISTANCEOUTPUT),
					HUtils.getHDFSPath(HUtils.SORTOUTPUT),
					numReducerSort
			};//初始化排序MR的参数
			ret=ToolRunner.run(HUtils.getConf(), new SortJob(), args);//排序
			if(ret!=0){
				Utils.simpleLog("SortJob任务运行失败！");
				return ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}

	public String getDc() {
		return dc;
	}
	public void setDc(String dc) {
		this.dc = dc;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getNumReducerDensity() {
		return numReducerDensity;
	}
	public void setNumReducerDensity(String numReducerDensity) {
		this.numReducerDensity = numReducerDensity;
	}
	public String getNumReducerDistance() {
		return numReducerDistance;
	}
	public void setNumReducerDistance(String numReducerDistance) {
		this.numReducerDistance = numReducerDistance;
	}
	/**
	 * @return the numReducerSort
	 */
	public String getNumReducerSort() {
		return numReducerSort;
	}
	/**
	 * @param numReducerSort the numReducerSort to set
	 */
	public void setNumReducerSort(String numReducerSort) {
		this.numReducerSort = numReducerSort;
	}

}
