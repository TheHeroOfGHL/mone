package com.xiaomi.youpin.tesla.proxy;

import org.apache.dubbo.rpc.proxy.MeshDubboRequest;
import org.apache.dubbo.rpc.proxy.MeshDubboResponse;

/**
 * @author goodjava@qq.com
 */
public interface MeshService {

    MeshResponse invoke(MeshRequest request);


    MeshDubboResponse invokeDubbo(MeshDubboRequest request);

}
