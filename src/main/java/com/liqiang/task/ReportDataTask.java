package com.liqiang.task;

import com.liqiang.SimpeEcode.Message;
import com.liqiang.utils.AESUtil;
import com.liqiang.utils.ByteUtil;
import com.liqiang.utils.CBCUtil;
import com.liqiang.utils.XmlUtil;
import com.liqiang.xml.*;
import io.netty.channel.Channel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

//import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReportDataTask implements TimerTask {
    private final Channel channel;
    private final Timer timer;

    public ReportDataTask(Timer timer, Channel channel) {
        this.timer = timer;
        this.channel = channel;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (channel.isActive()) {
            // 发送消息到服务器
//            String message = "Hello from client!";
            List<Function> functionList = new ArrayList<>();
            functionList.add(new Function("1", "abc", "330102E066090001-1090", "0", "20230509155330", "66"));
//            typeList.add("time");
            List<Meter> meterList = new ArrayList<>();
            meterList.add(new Meter("1", "330102E066090001", "conn", functionList));
            List<String> typeList = new ArrayList<>();
            typeList.add("report");

//            Report report = new Report("result", "pass", "20230509152030");
            Report report = new Report(new Common("330400A001", "01", typeList), new ReplyData("1", "no", "20230509170530", "report", meterList));
            //转换成xml字符串
            String xmlStr = XmlUtil.beanToXml(report, "UTF-8");
            //AES加密
            byte[] aesXmlBytes = AESUtil.encrypt(xmlStr);
            //计算内容长度
            int contentLength = 4 + aesXmlBytes.length;
            //计算CBC校验
            int contentSN = 3;
            short cbc_check = ByteUtil.reverseBytes(CBCUtil.calculateCBCChecksum(ByteUtil.byteMerger(ByteUtil.intToBytesBigEndian(contentSN), aesXmlBytes)));
            //心跳消息构建
            Message reportDataMessage = new Message(contentLength, contentSN, aesXmlBytes, cbc_check);
//			HeartResult heartResult = new HeartResult()
            channel.writeAndFlush(reportDataMessage);

            // 继续注册下一个定时任务
            timer.newTimeout(this, 5, TimeUnit.SECONDS);
        }
    }
}
