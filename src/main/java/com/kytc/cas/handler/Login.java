package com.kytc.cas.handler;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Login extends AbstractUsernamePasswordAuthenticationHandler {
     private static final org.slf4j.Logger LOGGER =LoggerFactory.getLogger(Login.class);

    public Login(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
            Integer order) {
        super(name, servicesManager, principalFactory, order);
        // TODO Auto-generated constructor stub
    }

//  private final String sql="select password from sec_user where username=?";
    private String sql="select * from tb_user where username=?";
    private String jdbcUrl = "jdbc:mysql://localhost:3306/kytc?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false";

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential transformedCredential,
            String originalPassword) throws GeneralSecurityException, PreventedException {
        // TODO Auto-generated method stub
        DriverManagerDataSource d=new DriverManagerDataSource();
        d.setDriverClassName("com.mysql.jdbc.Driver");
        d.setUrl(jdbcUrl);
        d.setUsername("root");
        d.setPassword("root");
        JdbcTemplate template=new JdbcTemplate();
        template.setDataSource(d);




        String username=transformedCredential.getUsername();
        String pd=transformedCredential.getPassword();
//      //查询数据库加密的的密码
        UserPO user=template.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper<UserPO>(UserPO.class));


//      if(sqlpd.equals(pd)){
//          return createHandlerResult(transformedCredential, principalFactory.createPrincipal(username, null), null);
//      }
        LOGGER.info("密码:"+pd);
        if(user==null){
            throw new FailedLoginException("没有该用户");
        }
        //返回多属性
        Map<String, Object> map=new HashMap<>();
        map.put("id", user.getId());
        map.put("nickname",user.getNickname());
        map.put("username", user.getUsername());
        map.put("sex", user.getSex());
        map.put("portrait", user.getPortrait());
        map.put("gmtCreate", user.getGmtCreate());
        map.put("gmtModified", user.getGmtModified());
//        for(String key:user.keySet()){
//        	System.out.println(key+"   :   "+user.get(key));
//        }
        LOGGER.info("++++++++++++++++++++zjzjzjz",user);

//        if(PasswordUtil.decodePassword(user.getPassword(), pd, username)){
            return createHandlerResult(transformedCredential, principalFactory.createPrincipal(username, map), null);
//        }
//        throw new FailedLoginException("Sorry, login attemp failed.");
//      return  null;
    }

}