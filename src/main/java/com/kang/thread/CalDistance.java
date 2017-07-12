/**
 * 
 */
package com.kang.thread;

import org.apache.hadoop.util.ToolRunner;

import com.kang.filter.CalDistanceJob;
import com.kang.util.HUtils;

/**
 * 计算距离
 */
public class CalDistance implements Runnable {

	private String input;
	private String output;
	
	public CalDistance(String input,String output){
		this.input=input;
		this.output=output;
	}
	
	@Override
	public void run() {
		String [] args ={
				HUtils.getHDFSPath(input),
				HUtils.getHDFSPath(output)
		};
		try {
			ToolRunner.run(HUtils.getConf(), new CalDistanceJob(),args );
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

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
	

}
