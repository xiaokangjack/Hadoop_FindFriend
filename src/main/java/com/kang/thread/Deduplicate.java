/**
 * 
 */
package com.kang.thread;

import org.apache.hadoop.util.ToolRunner;

import com.kang.filter.DeduplicateJob;
import com.kang.util.HUtils;

/**
 * 去重
 */
public class Deduplicate implements Runnable {

	private String input;
	private String output;
	
	public Deduplicate(String input,String output){
		this.input=input;
		this.output=output;
	}
	@Override
	public void run() {
		String [] args ={
				HUtils.getHDFSPath(input),//获取输入路径
				HUtils.getHDFSPath(output)//获取输出路径
		};
		try {
			ToolRunner.run(HUtils.getConf(), new DeduplicateJob(),args );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
