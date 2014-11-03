package models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserTest {

	@Test
	public void testEncode() {
		assertEquals("11111310148117119100107116118565366123116116103107106118", User.encode(2, "thierry@36tribus.com"));
		assertEquals("12012211057126128109116125127656275121108120108115110", User.encode(11, "chaman@36tribus.com"));

	}

}
