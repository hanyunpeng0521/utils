package com.jarvis.base.util;

import junit.framework.Assert;
import junit.framework.Assert.*;

import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.Arrays;

/** 
* Base64Util Tester. 
* 
* @author <Authors name> 
* @since <pre>���� 22, 2018</pre> 
* @version 1.0 
*/ 
public class Base64UtilTest { 

    String text="this is a example,这是一个例子。13839451";
    String base64="dGhpcyBpcyBhIGV4YW1wbGUs6L+Z5piv5LiA5Liq5L6L5a2Q44CCMTM4Mzk0NTE=";
    String notbase64="dGhpcyBpcyBhIGV4YW1wbGUs6L+Z5piv5LiA5Liq5L6L5a2Q44CCMTM4Mzk0NTE=\"";
@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: isBase64(String isValidString) 
* 
*/ 
@Test
public void testIsBase64IsValidString() throws Exception {
    Assert.assertTrue(Base64Util.isBase64(base64));
    Assert.assertFalse(Base64Util.isBase64(notbase64));

} 


/** 
* 
* Method: encodeString(String src) 
* 
*/ 
@Test
public void testEncodeString() throws Exception {
    String s = Base64Util.encodeString(text);
    Assert.assertEquals(base64,s);

} 

/** 
* 
* Method: encodeBytes(byte[] src) 
* 
*/ 
@Test
public void testEncodeBytes() throws Exception {
    String s = Base64Util.encodeByte(text.getBytes());
    Assert.assertEquals(base64,s);
} 


/** 
* 
* Method: decode(String src) 
* 
*/ 
@Test
public void testDecodeSrc() throws Exception {
    String decode = Base64Util.decode(base64);
    Assert.assertEquals(decode,text);
} 

/** 
* 
* Method: decode(String src, String charSet) 
* 
*/ 
@Test
public void testDecodeForSrcCharSet() throws Exception { 

} 

/** 
* 
* Method: decode(byte[] base64Data) 
* 
*/ 
@Test
public void testDecodeBase64Data() throws Exception {
    byte[] encode = Base64Util.encode(text.getBytes());
    byte[] decode = Base64Util.decode(encode);
    Assert.assertTrue(Base64Util.isArrayByteBase64(encode));
    Assert.assertEquals(Base64Util.encodeByte(decode),base64);

} 


private String s;
    /**
* 
* Method: encodeFile(String filePath) 
* 
*/ 
@Test
public void testEncodeFile() throws Exception {
    s= Base64Util.encodeFile("D:\\MyPlan\\实践\\JAVA_S\\utils\\src\\test\\java\\com\\jarvis\\base\\深入理解Android之AOP - 阿里云.html");
    System.out.println(s);
} 

/** 
* 
* Method: decodeToFile(String filePath, String base64) 
* 
*/ 
@Test
public void testDecodeToFile() throws Exception {
    s= Base64Util.encodeFile("D:\\MyPlan\\实践\\JAVA_S\\utils\\src\\test\\java\\com\\jarvis\\base\\深入理解Android之AOP - 阿里云.html");

    String decode = Base64Util.decode(s);
    System.out.println(decode);
} 

/** 
* 
* Method: fileToByte(String filePath) 
* 
*/ 
@Test
public void testFileToByte() throws Exception {
    System.out.println(Arrays.toString(Base64Util.fileToByte("D:\\MyPlan\\实践\\JAVA_S\\utils\\src\\test\\java\\com\\jarvis\\base\\深入理解Android之AOP - 阿里云.html")));


} 

/** 
* 
* Method: byteArrayToFile(byte[] bytes, String filePath) 
* 
*/ 
@Test
public void testByteArrayToFile() throws Exception { 
    Base64Util.byteArrayToFile(Base64Util.fileToByte("D:\\MyPlan\\实践\\JAVA_S\\utils\\src\\test\\java\\com\\jarvis\\base\\深入理解Android之AOP - 阿里云.html"),
            "D:\\MyPlan\\实践\\JAVA_S\\utils\\src\\test\\java\\com\\jarvis\\base\\深入理解Android之AOP - 阿里云(1).html");
}


} 
