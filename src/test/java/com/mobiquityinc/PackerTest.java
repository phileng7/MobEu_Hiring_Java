package com.mobiquityinc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.Packer;

public class PackerTest {

	@Test
	public void testPacker() throws APIException {
		String filePath = "\\tmp\\packages_test.txt";
		String res = Packer.pack(filePath);
		String expected = "4\n" + "-\n" + "2,7\n" + "8,9\n";
		assertEquals(expected, res);
	}
}
