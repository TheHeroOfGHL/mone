package com.xiaomi.youpin.tesla.agent.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class LogReq implements Serializable {


    private String cmd;

    private String path;

    private long pointer;

    private int lineNum;


}
