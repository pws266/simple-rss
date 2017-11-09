package test.java.com.dataart.rss;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;

import main.java.com.dataart.rss.process.UserAuthorization;

/**
 * Operations verification of user authorization module:
 * - password encryption verification using known "salt" sequence and inner private method;
 * - checking authorization procedure.
 *
 * @author Sergey Sokhnyshev
 * Created on 26.09.17.
 */
public class UserAuthorizationTest {
    private static final String userPassword = "1q2w3e4r5t";
    private static final String anotherUserPassword = "2017xxx_SecUritY-Pass_1294";

    private static final String knownSalt = "7f7b54083f70b0fc26e5ae764f5f702cd2c72a59b25e011b";
    private static final String knownUserHash = "2611862ecbacf8fbb3e37f7b54083f70b0fc26e5ae76e946a9ba2f7195e278b54" +
                                                "f5f702cd2c72a59b25e011b3fcd483958ea83f44862c398";

    private UserAuthorization authModule;

    @Before
    public void before() {
        authModule = new UserAuthorization();
    }

    @Test
    public void testInnerEncryptPassword() throws Exception {
        // getting general method "encryptPassword" for test purposes
        Class[] paramTypes = new Class[] { String.class, byte[].class };
        Method innerEncryption = authModule.getClass().getDeclaredMethod("encryptPassword", paramTypes);

        // making this method as "public"
        innerEncryption.setAccessible(true);

        // assigning set of predefined arguments
        Object[] args = new Object[] { userPassword, DatatypeConverter.parseHexBinary(knownSalt)};

        String userHash = (String) innerEncryption.invoke(authModule, args);

        // comparing obtained hash with known for "userPassword"
        Assert.assertEquals(knownUserHash.equals(userHash), true);
    }

    @Test
    public void testIsCorrectPassword() throws NoSuchAlgorithmException {
        String correctHash = authModule.encryptPassword(userPassword);

        Assert.assertEquals(authModule.isCorrectPassword(userPassword, correctHash), true);
        Assert.assertEquals(authModule.isCorrectPassword(anotherUserPassword, correctHash), false);
    }

}