package com.kytc.cas.handler;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import com.kytc.cas.exception.AccountNotFoundException;
/**
 * @author yellowcong
 * 创建日期:2018/02/02
 *
 */
public class CustomerHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

	public CustomerHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
			Integer order) {
		super(name, servicesManager, principalFactory, order);
	}
	public String md5(String str) {
        try {
            return DigestUtils.md5Hex(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
//            log.error("不支持的编码。", e);
        }
        return null;
    }
	/**
	 * 用于判断用户的Credential(换而言之，就是登录信息)，是否是俺能处理的
	 * 就是有可能是，子站点的登录信息中不止有用户名密码等信息，还有部门信息的情况
	 */
	@Override
	public boolean supports(Credential credential) {
		//判断传递过来的Credential 是否是自己能处理的类型
		System.out.println("hahhahah");
		System.out.println(credential instanceof UsernamePasswordCredential);
		return credential instanceof UsernamePasswordCredential;
	}
	
	/**
	 * 用于授权处理
	 */
	@Override
	protected HandlerResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
		UsernamePasswordCredential usernamePasswordCredentia = (UsernamePasswordCredential) credential;
		
		//获取传递过来的用户名和密码
		String username = usernamePasswordCredentia.getUsername();
        String password = usernamePasswordCredentia.getPassword();
        String pWord = null;
        Connection conn = null;
    	try {
			Class.forName("com.mysql.jdbc.Driver");
			
			//直接是原生的数据库配置啊
			String url = "jdbc:mysql://localhost:3306/kytc?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false";
			String user = "root";
			String pass = "root";
			
			conn = DriverManager.getConnection(url,user, pass);
			
			//查询语句
			String sql = "SELECT * FROM tb_user WHERE username =?";
			PreparedStatement  ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				pWord = rs.getString("password");
				System.out.println("获取到数据..."+password+"   "+pWord);
				if(pWord.equals(md5(password))){
					//存放数据到里面
					Map<String,Object> result = new HashMap<String,Object>();
					result.put("username", rs.getString("username"));
					result.put("password", rs.getString("password"));
					result.put("sex", rs.getString("sex"));
					result.put("portrait", rs.getString("portrait"));
					result.put("gmtCreate", rs.getDate("gmt_create"));
					result.put("gmtModified", rs.getDate("gmt_modified"));
					System.out.println(result);
	            	//允许登录，并且通过this.principalFactory.createPrincipal来返回用户属性
		            return createHandlerResult(credential, this.principalFactory.createPrincipal(username, result), null);
//					return createHandlerResult(credential, this.principalFactory.createPrincipal(username,result));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    	if(pWord==null){
    		throw new AccountNotFoundException("用户不存在...");
    	}else{
    		throw new FailedLoginException("用户密码错误...");
    	}
    	//当是admin用户的情况，直接就登录了，谁叫他是admin用户呢
//    	if(username.startsWith("admin")) {
//    		//直接返回去了
//    		return createHandlerResult(credential, this.principalFactory.createPrincipal(username, Collections.emptyMap()), null);
//    	}else if (username.startsWith("lock")) {
//            //用户锁定
//            throw new AccountLockedException();
//        } else if (username.startsWith("disable")) {
//            //用户禁用
//            throw new AccountDisabledException();
//        } else if (username.startsWith("invali")) {
//            //禁止登录该工作站登录
//            throw new InvalidLoginLocationException();
//        } else if (username.startsWith("passorwd")) {
//            //密码错误
//            throw new FailedLoginException();
//        } else if (username.startsWith("account")) {
//            //账号错误
//            throw new AccountLockedException();
//        }
//		return null;
	}

}
