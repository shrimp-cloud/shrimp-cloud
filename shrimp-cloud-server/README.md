基于netty框架封装的服务端
#核心接口：
DataServer服务实现接口
ExecuteData数据处理接口
#具备以下功能：
1.支持tcp和udp协议，支持管理端，可以配置好web接口动态操作服务 ,具备初步的会话管理能力，以及推送消息到客户端的功能 增加了jt808协议核心部分示例
netty:
# 监听端口
nettyPort: 10000
# 是否写日志
isWriteLog: true
# 服务名称，会在数据对象中体现，可入库备查
appName: nettyApp
# 协议名称 tcp或者udp
serverType: tcp
2.对windows和linux系统分别采用不同的优化实现，分别基于nio和epoll两种实现
3.服务端收到客户端请求并接受到数据后，会把数据封装成数据处理对象，
可采取直接处理和队列处理两种方式，
*队列处理采用disruptor和integration两种方式实现，前者重视性能，后者重视集成性，队列机制有利于高并发下数据完整性和系统健壮性
*直接处理采取异步和同步方式，异步是启动另外的线程处理数据，同步是直接调用方法，性能较慢，可根据实际场景选取
#四种处理数据方式，队列disruptor和queue，异步处理asyn，同步默认处理default
#executeDataName: disruptor
#executeDataName: default
#executeDataName: queue
executeDataName: asyn
4.重要配置参数在application.yml中配置，具体可见示例配置template.yml文件
5.用户自己实现的处理逻辑，需要注册到spring中作为组件，同时方法上采用注解@NettyHandlerMethod，组件采用扫描反射方式获取对象
例如：
@Component
public class DemoService {
private MsgDecoder decoder = new MsgDecoder();
@Resource
LogPlug logPlug;
@NettyHandlerMethod
public byte[] test(byte[] value){
PackageData jt808Msg = this.decoder.queueElement2PackageData(value);
logPlug.writeLog(true,jt808Msg);
return value;
}
}
注解说明：
@NettyHandlerMethod注解支持输入输出类型转换，即字符串和字节数组等类型互转，具备灵活性。
例如：@NettyHandlerMethod(inputConvert="byte_string",outputConvert="string_byte")就是方法输入和输出都是字符串，框架对其进行处理，转换为byte数组
