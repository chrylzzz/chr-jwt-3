package com.chryl.utils;

import com.chryl.bean.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chryl on 2019/7/20.
 */
public class JwtUtil {

    //可以用方法生成
    // SecretKey key = generalKey();生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
    public static final String secret = "chryl";


    /**
     * 创建jwt
     *
     * @param ttlMillis 过期时间
     * @param user
     * @return
     */
    public static String createJWT(long ttlMillis, User user) {

        //签名算法,指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //jwt生成时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //header
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUserName());

        String subject = user.getUserName();
        //jwt -body 声明
        //下面就是在为payload添加各种标准声明和私有声明了
        JwtBuilder builder = Jwts.builder()//这里其实就是new一个JwtBuilder，设置jwt的body
                //header
                .setHeader(header)
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                //iat: jwt的签发时间
                .setIssuedAt(now)
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secret);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            //设置过期时间
            builder.setExpiration(exp);
        }
        return builder.compact();//就开始压缩为xxxxxxxxxxxxxx.xxxxxxxxxxxxxxx.xxxxxxxxxxxxx这样的jwt
    }


    /**
     * Token的解密(jwt解密)
     *
     * @param token 加密后的token
     * @param user  用户的对象
     * @return
     */
    public static Claims parseJWT(String token, User user) {
//        SecretKey key = generalKey();  //签名秘钥，和生成的签名的秘钥一模一样

        //得到DefaultJwtParser
        Claims claims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(secret)
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }

    /**
     * 校验token
     * 在这里可以使用官方的校验，我这里校验的是token中携带的username于数据库一致的话就校验通过
     *
     * @param token
     * @param user
     * @return
     */
    public static Boolean isVerify(String token, User user) {

        //得到DefaultJwtParser
        Claims claims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(secret)
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();

        if (claims.get("username").equals(user.getUserName())) {
            return true;
        }
        return false;
    }

}
