package cc.ranmc.server.constant;

public class Email {
    public static String text = """
            <div style="background-color: #364f6b;display: flex;justify-content: center;align-items: center;flex-direction: column;height: 100%">
                <br>
                <div style="display: flex;flex-direction: column;align-items: center;">
                    <img style="width: 340px;height: 176px;" src="https://z3.ax1x.com/2021/01/14/sUds6x.png"></img>
                </div>
                <div style="display: flex;flex-direction: column;">
                    <p style="color: #eaeaea">
                        玩家 %player% 正在申请%action%
                        <br>请确认是本人操作后
                        <a style="color: #3fc1c9" href='%url%'>点击确认</a>
                        <br>如果您的邮箱程序不支持链接点击操作
                        <br>请拷贝下方链接至您浏览器地址栏进入
                        <br>
                        <a style="color: #3fc1c9" href='%url%'>%url%</a>
                        <br>
                    </p>
                    <p style="color: #eaeaea">
                        祝您游戏愉快
                        <br>系统邮件，请勿回复
                        <br>桃花源丨纯净生存 呈上
                        <br>服务器地址：ranmc.cc
                        <br>备用地址壹：b1.ranmc.cc
                        <br>备用地址贰：b2.ranmc.cc
                    </p>
                </div>
                <br>
            </div>
            """;
}
