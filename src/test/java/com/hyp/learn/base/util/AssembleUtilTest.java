package com.hyp.learn.base.util;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;

import java.util.Arrays;

/** 
* AssembleUtil Tester. 
* 
* @author <Authors name> 
* @since <pre>���� 22, 2018</pre> 
* @version 1.0 
*/ 
public class AssembleUtilTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: getAssemble(String sourceStr, int max) 
* 
*/ 
@Test
public void testGetAssembleForSourceStrMax() throws Exception {
    String[] sourceArr = new String[]{"1", "2", "3", "4", "5", "6", "15"};
    String[] resultArr = AssembleUtil.getAssemble(sourceArr, 3);
    System.out.println("累计组合：" + resultArr.length + "," + Arrays.toString(resultArr));

} 

/** 
* 
* Method: getAssemble(String[] sourceArray, int max) 
* 
*/ 
@Test
public void testGetAssembleForSourceArrayMax() throws Exception {

    String sourceStr = "1,2,3,4,5,6,7";
    String[] resultArr2 = AssembleUtil.getAssemble(sourceStr, 3);
    System.out.println("累计组合：" + resultArr2.length + "," + Arrays.toString(resultArr2));
} 


/** 
* 
* Method: doSet(String start, String[] sourceList, int max) 
* 
*/ 
@Test
public void testDoSet() throws Exception {

}

} 
