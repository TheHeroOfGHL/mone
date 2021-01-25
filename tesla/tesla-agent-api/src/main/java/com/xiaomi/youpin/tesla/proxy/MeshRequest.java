package com.xiaomi.youpin.tesla.proxy;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class MeshRequest implements Serializable {

    private String serviceName;

    private String methodName;

    private String app;

    private String remoteApp;

    private String[] paramTypes;

    private String[] params;

    private long timeout;


}
