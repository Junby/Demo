package com.example.demo.login.config;

import com.example.demo.login.bean.User;
import com.example.demo.login.service.LoginService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyShiroRealm extends AuthorizingRealm
{
    @Autowired
    private LoginService loginService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection)
    {
//        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
//        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        UserInfo userInfo  = (UserInfo)principalCollection.getPrimaryPrincipal();
//        for(SysRole role:userInfo.getRoleList()){
//            authorizationInfo.addRole(role.getRole());
//            for(SysPermission p:role.getPermissions()){
//                authorizationInfo.addStringPermission(p.getPermission());
//            }
//        }
//        return authorizationInfo;
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException
    {

        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        String username = userToken.getUsername();

        List<User> userList = loginService.getUserByNameAndPassword(username, String.valueOf(userToken.getPassword()));
        User user = null;
        if (userList.size() != 0)
        {
            user = userList.get(0);
        }
        if (null == user)
        {
            return null;
        }

        return new SimpleAuthenticationInfo(user,user.getPassword(),this.getClass().getName());
    }
}
