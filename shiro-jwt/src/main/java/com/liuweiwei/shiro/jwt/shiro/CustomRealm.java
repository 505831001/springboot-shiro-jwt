package com.liuweiwei.shiro.jwt.shiro;

import com.liuweiwei.shiro.jwt.mapper.UserMapper;
import com.liuweiwei.shiro.jwt.util.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA
 *
 * @author liuweiwei 505831001@qq.com
 * @since 2018-04-06
 */
@Component
public class CustomRealm extends AuthorizingRealm {
    private final UserMapper userMapper;

    @Autowired
    public CustomRealm(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("<<Shiro 身份鉴定认证>> - Login 登录信息：" + authenticationToken);
        String token = (String) authenticationToken.getCredentials();
        String username = JWTUtil.getUsername(token);
        if (username == null || !JWTUtil.verify(token, username)) {
            throw new AuthenticationException("token认证失败！");
        }
        String password = userMapper.getPassword(username);
        if (password == null) {
            throw new AuthenticationException("该用户不存在！");
        }
        int ban = userMapper.checkUserBanStatus(username);
        if (ban == 1) {
            throw new AuthenticationException("该用户已被封号！");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(token, token, "MyRealm");
        System.out.println(info.getCredentials());
        System.out.println(info.getCredentialsSalt());
        System.out.println(info.getPrincipals());
        System.out.println(info.toString());
        return info;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("<<Shiro 鉴定认证授权>> - Login 登录信息：" + principalCollection);
        String username = JWTUtil.getUsername(principalCollection.toString());
        SimpleAuthorizationInfo authority = new SimpleAuthorizationInfo();
        String role = userMapper.getRole(username);
        String rolePermission = userMapper.getRolePermission(username);
        String permission = userMapper.getPermission(username);
        Set<String> roles = new HashSet<>();
        roles.add(role);
        authority.setRoles(roles);
        Set<String> stringPermissions = new HashSet<>();
        stringPermissions.add(rolePermission);
        stringPermissions.add(permission);
        authority.setStringPermissions(stringPermissions);
        return authority;
    }
}
