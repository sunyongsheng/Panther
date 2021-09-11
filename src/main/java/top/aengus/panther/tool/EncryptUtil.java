package top.aengus.panther.tool;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/8/28
 */
public class EncryptUtil {
    private static final RSA securityUtil = SecureUtil.rsa(
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC6ZBkpz2iHCxoT" +
                    "a00nLrkgLabeVmyzVeEh5CvKspjzAmYCAISS9Pi/siZtfRcUOtTpV1yuCn5Ap6Cs" +
                    "i491L1r9J1e10MebFUeIqm2k7jK3oi50mYkTW97BjSe6oFm4HizxctVCP2LEv6cY" +
                    "fHG8gXWVaIjrGlC7W4mJBBGQOgmfar7JB2sUm/M2XWYVknAXL6wKQeFoU4K4TAXJ" +
                    "YMQyKJC1EpWluEfGMrcrF8KE0e8nJz4dQthd+tIEsrBN1ZKTItgMmLCXVHbGRKgI" +
                    "J7V+LOs6y8qPaHSnag17tYLe06IN4trLmp2BVyNBe4oDbgXcKnFoWARAv8eyTG/6" +
                    "B3NbcBZbAgMBAAECggEAUySnnODqLaMkJJlutxW+er4ie3dt0alzXZzcc/SrCWt+" +
                    "eVIl0S/5bflX7usnylxb8SKJMxF3M/wSTiWK/qj7G4XbOD1DbMhXOn1bn4CpmwR7" +
                    "WIPMg5gti4UdvcAdhWXggmvI+V4w/rbHIMMSMRZGjzksKkN407qHZVktRHq/JB7D" +
                    "ZbarMdAgk4WbTl5XLQvjzyxHT4EU8BHnQ7GnIHB0LMbe7RVcvLfq10PxckX7gnuQ" +
                    "JnSXUyV7MXfTtAeWjWiyuA9TV4lXAlI9MjIlNpAYJoxEoMu5dvHCauApnx6mcIm3" +
                    "U0eV+p+a/U9tjqKz4d8mxp9vVObu0mfRjXVrvkfH4QKBgQD2VMJwdtZ/mPNSxp1x" +
                    "cSQBYCdAqsEMmMcYgg/2E+ljfSHyQkOjf1ty/Md2vvo/lkaNKYKeDy3tAfTFNRff" +
                    "Gala+5Qo8xv6zqeZNVwaJfF2XDbMTCdRW6yu26J/+XGBDEJIZUHt/FIQU1q5L0vk" +
                    "WfbN+h59Jzt5CC2zzzcAN3cOcwKBgQDBtQj8bvrvflNexp1gfILJ1G8pvFw3lq0X" +
                    "iwQjy4zPZ05Jk+5zvsCkbTvr0AFsV/u6IRZ3yKMe/aLrU1k8i/VPdCnJfGxWhoz3" +
                    "hrQSyG6btaYBojGnQcN80EjqI0HjyPfzHr3nmN3dIxqbtWTsmB64n9J1h8OjyaOu" +
                    "3PBrZuo2eQKBgDeXLUJAL2wgUllN5V4ikGOXFYSshGCZYeUQBRYdrD/9j1mixqzx" +
                    "IuwyS7lqpAMRoN5ejdGpz017cYfH0ZlRqzyTJf6PzrKtTHlw51YnVfTGHr/AN+dO" +
                    "bwwPGP8Uhh7/az33TAevN73jccf0+oWQH/igB51q6ysHQ3kevGTaH44tAoGBAJqy" +
                    "rMmFDEyF86kGL5tl9MGhMWIAdxlB8VyS/ixkHEFrPF+Qj+fKGGl4f1lVVj2jinrc" +
                    "f+gLyRIv95O+jc7H65KXr+OkxZTYge4QmvFBNuyNxlUZplPwNmIQqZqrq6xxzGtS" +
                    "StGSCOXZah+eX0qANlC1+ss4MKs2ls1Iz4zGtRfpAoGBANZ5bpofWrdKbHDlgEHi" +
                    "FegtvL8XVuHBn9lBnnvpexkCGEQWlxUI83AA1PYUap0y0ZFdZpIGKGpEaK83zl+4" +
                    "oxqy2BFoewkfvgVf7FqRV4UB+LQS6yrtos7UrUXby+Vjlr1NLHzJTpW2PN/UvFLU" +
                    "yMGp9VKSmFafApR0e/OCO68F",
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAumQZKc9ohwsaE2tNJy65" +
                    "IC2m3lZss1XhIeQryrKY8wJmAgCEkvT4v7ImbX0XFDrU6Vdcrgp+QKegrIuPdS9a" +
                    "/SdXtdDHmxVHiKptpO4yt6IudJmJE1vewY0nuqBZuB4s8XLVQj9ixL+nGHxxvIF1" +
                    "lWiI6xpQu1uJiQQRkDoJn2q+yQdrFJvzNl1mFZJwFy+sCkHhaFOCuEwFyWDEMiiQ" +
                    "tRKVpbhHxjK3KxfChNHvJyc+HULYXfrSBLKwTdWSkyLYDJiwl1R2xkSoCCe1fizr" +
                    "OsvKj2h0p2oNe7WC3tOiDeLay5qdgVcjQXuKA24F3CpxaFgEQL/Hskxv+gdzW3AW" +
                    "WwIDAQAB");

    public static String encrypt(String text) {
        return securityUtil.encryptBase64(text, KeyType.PrivateKey);
    }

    public static String decrypt(String text) {
        return securityUtil.decryptStr(text, KeyType.PublicKey);
    }

}
