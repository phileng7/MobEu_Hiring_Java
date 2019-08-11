package com.mobiquityinc.packer.run;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.Packer;

//Another package that will run the Packer package
public class PackerRun {

	public static void main(String[] args) throws APIException {
		//Must have one argument, File Pathname
		if (args.length<1) {
			throw new APIException("Filepath argument is missing");
		}
		String filePath = args[0];
		String res = Packer.pack(filePath);
		System.out.println(res);
	}
}
