/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.dmesh.interceptor;

import com.google.gson.Gson;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.dmesh.MeshResponse;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshReference;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 * <p>
 * 这里会处理dubbo的调用(看起来是调用本地方法,其实是调用远程的dubbo服务)
 */
@Slf4j
public class CallMethodInterceptor implements MethodInterceptor {

    private Gson gson = new Gson();

    private Ioc ioc;
    private Config config;
    private MeshReference reference;

    public CallMethodInterceptor(Ioc ioc, Config config, MeshReference reference) {
        this.ioc = ioc;
        this.config = config;
        this.reference = reference;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        String mn = method.getName();
        if ("getClass".equals(mn)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if ("hashCode".equals(mn)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if ("toString".equals(mn)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if ("equals".equals(mn)) {
            if (objects.length == 1) {
                return methodProxy.invokeSuper(o, objects);
            }
            throw new IllegalArgumentException("Invoke method [" + mn + "] argument number error.");
        }

        //信息发送server(mesh)层
        UdsClient client = ioc.getBean(UdsClient.class);
        UdsCommand command = UdsCommand.createRequest();
        command.setApp(config.get("uds_app", ""));
        command.setCmd("dubboCall");
        command.setServiceName(reference.interfaceClass().getName());
        command.setMethodName(method.getName());
        //远程的app
        command.setRemoteApp(reference.app());
        command.setTimeout(reference.timeout());
        //是网格dubbo调用,还是普通dubbo调用
        command.putAtt("mesh", String.valueOf(reference.mesh()));
        command.putAtt("group", reference.group());
        command.putAtt("version", reference.version());
        command.putAtt("timeout", String.valueOf(reference.timeout()));

        command.setParamTypes(Arrays.stream(method.getParameterTypes()).map(it -> it.getName()).toArray(String[]::new));
        command.setParams(Arrays.stream(objects).map(it -> gson.toJson(it)).toArray(String[]::new));

        UdsCommand res = client.call(command);

        //调用发生了错误
        if (res.getCode() != 0) {
            throw new RuntimeException(res.getMessage());
        }

        String data = res.getData(String.class);
        log.info("mesh plugin receive:{}", data);

        MeshResponse r = new Gson().fromJson(data, MeshResponse.class);

        Object resObj = gson.fromJson(r.getData(), method.getReturnType());
        return resObj;
    }
}
